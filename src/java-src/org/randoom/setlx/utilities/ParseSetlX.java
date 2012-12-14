package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.EndOfFileException;
import org.randoom.setlx.exceptions.FileNotReadableException;
import org.randoom.setlx.exceptions.ParserException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.grammar.*;
import org.randoom.setlx.statements.Block;

import org.antlr.runtime.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ParseSetlX {

    private final   static  int             EXPR            =  1337;
    private final   static  int             BLOCK           = 31337;
    private         static  int             errors          =     0; // our own error accounting, which survives nested parsing
    private final   static  List<String>    loadedLibraries = new LinkedList<String>();

    /*package*/ static void clearLoadedLibraries() {
        loadedLibraries.clear();
    }

    public static Block parseFile(String fileName) throws ParserException {
        try {
            // allow modification of fileName/path by environment provider
            fileName = Environment.filterFileName(fileName);
            if (new File(fileName).isFile()) {
                // parse the file contents (ANTLR will print its parser errors into stderr ...)
                return parseBlock(new ANTLRFileStream(fileName));
            } else {
                throw new FileNotReadableException("File '" + fileName + "' could not be read.");
            }
        } catch (IOException ioe) {
            throw new FileNotReadableException("File '" + fileName + "' could not be read.");
        }
    }

    public static Block parseLibrary(String name) throws ParserException {
        try {
            // allow modification of name by environment provider
            name = Environment.filterLibraryName(name + ".stlx");
            if (new File(name).isFile()) {
                if (loadedLibraries.contains(name)) {
                    return new Block();
                } else {
                    loadedLibraries.add(name);
                    // parse the file contents (ANTLR will print its parser errors into stderr ...)
                    return parseBlock(new ANTLRFileStream(name));
                }
            } else {
                throw new FileNotReadableException("Library '" + name + "' could not be read.");
            }
        } catch (IOException ioe) {
            throw new FileNotReadableException("Library '" + name + "' could not be found.");
        }
    }

    public static Block parseInteractive() throws ParserException {
        try {
            final InputStream stream = InputReader.getStream();

            // parse the input (ANTLR will print its parser errors into stderr ...)
            return parseBlock(new ANTLRInputStream(stream));

        } catch (IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    public static Block parseStringToBlock(final String input) throws ParserException {
        try {
            final InputStream stream = new ByteArrayInputStream(input.getBytes());

            // parse the input (ANTLR will print its parser errors into stderr ...)
            return parseBlock(new ANTLRInputStream(stream));

        } catch (IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    public static Expr parseStringToExpr(final String input) throws ParserException {
        try {
            final InputStream stream = new ByteArrayInputStream(input.getBytes());

            // parse the input (ANTLR will print its parser errors into stderr ...)
            return parseExpr(new ANTLRInputStream(stream));

        } catch (IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    public static int getErrorCount() {
        return errors;
    }

    public static void resetErrorCount() {
        errors = 0;
    }

    public static void addReportedError() {
        errors++;
    }

    /* private methods */

    private static Block parseBlock(final ANTLRStringStream input) throws SyntaxErrorException {
        return (Block) handleFragmentParsing(input, BLOCK);
    }

    private static Expr parseExpr(final ANTLRStringStream input) throws SyntaxErrorException {
        return (Expr) handleFragmentParsing(input, EXPR);
    }

    private static CodeFragment handleFragmentParsing(final ANTLRStringStream input, final int type) throws SyntaxErrorException {
              SetlXgrammarLexer  lexer  = null;
              SetlXgrammarParser parser = null;
        final LinkedList<String> oldCap = Environment.getParserErrorCapture();
        try {
                                    lexer  = new SetlXgrammarLexer(input);
            final CommonTokenStream ts     = new CommonTokenStream(lexer);
                                    parser = new SetlXgrammarParser(ts);

            // capture parser errors
            Environment.setParserErrorCapture(new LinkedList<String>());

            // parse the input
            final CodeFragment      frag   = parseFragment(parser, type);

            // now ANTLR will add its parser errors into our capture ...

            /* check for unparsed syntax errors at the end of the input stream */

            // fill token stream until EOF is reached (parser fills only as far as its lookahead needs)
            ts.fill();
            // current index in stream of tokens
            final int index = ts.index();

            /*
             *  If the index into the tokenStream (which was set by the parser)
             *  is not equal to the number of tokens in the complete input
             *  (minus EOF), some tokens where ignored by the parser.
             *
             *  Best guess: The parser encountered a syntax error after a valid rule.
             */
            if (index < (ts.size() - 1)) {
                // parse again to force displaying syntax error in remaining tokenStream
                parseFragment(parser, type);

                // now ANTLR will (again) add its parser errors into our capture ...

                // check if parser moved the index
                if (index == ts.index()) {
                    /*  Index was not moved. Probably epsilon can be and was
                     *  derived from the start-rule.
                     *  However there are still unparsed tokens left...
                     *
                     *  Note: In SetlX this can NEVER be the case, because
                     *        epsilon can NOT be derived from any start-rule!
                     */
                    final Token t = ts.get(index);
                    String error  = "line " + t.getLine() + ":" + t.getCharPositionInLine();
                           error += " input '" + ts.toString(index, ts.size()) + "' includes unidentified errors";
                    // fake ANTLR like error message
                    errors++;
                    parser.emitErrorMessage(error);
                }
            }

            errors += parser.getNumberOfSyntaxErrors() + lexer.getNumberOfSyntaxErrors();
            if (errors > 0) {
                throw SyntaxErrorException.create(
                    Environment.getParserErrorCapture(),
                    errors + " syntax error(s) encountered."
                );
            }

            // Optimize fragment
            frag.optimize();

            return frag;
        } catch (RecognitionException re) {
            throw SyntaxErrorException.create(Environment.getParserErrorCapture(), re.getMessage());
        } catch (NullPointerException npe) {
            if (lexer != null) {
                errors += lexer.getNumberOfSyntaxErrors();
            }
            if (parser != null) {
                errors += parser.getNumberOfSyntaxErrors();
            }
            if (errors > 0) {
                // NullPointerException caused by syntax error(s)
                throw SyntaxErrorException.create(
                    Environment.getParserErrorCapture(),
                    errors + " syntax error(s) encountered."
                );
            } else { // NullPointer in parse tree itself
                throw SyntaxErrorException.create(new LinkedList<String>(),"Parsed tree contains nullpointer.");
            }
        } finally {
            // restore error capture
            Environment.setParserErrorCapture(oldCap);
        }
    }

    private static CodeFragment parseFragment(final SetlXgrammarParser parser, final int type) throws RecognitionException {
        switch (type) {
            case EXPR:
                return parser.initExpr();
            case BLOCK:
                return parser.initBlock();
            default:
                /* this should never be reached if surrounding code is correct */
                return null;
        }
    }
}

