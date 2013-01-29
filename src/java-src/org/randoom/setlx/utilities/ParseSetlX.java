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

public class ParseSetlX {

    private final   static  int             EXPR            =  1337;
    private final   static  int             BLOCK           = 31337;

    public static Block parseFile(final State state, String fileName) throws ParserException {
        try {
            // allow modification of fileName/path by environment provider
            fileName = state.filterFileName(fileName);
            if (new File(fileName).isFile()) {
                // parse the file contents (ANTLR will print its parser errors into stderr ...)
                return parseBlock(state, new ANTLRFileStream(fileName));
            } else {
                throw new FileNotReadableException("File '" + fileName + "' could not be read.");
            }
        } catch (final IOException ioe) {
            throw new FileNotReadableException("File '" + fileName + "' could not be read.");
        }
    }

    public static Block parseLibrary(final State state, String name) throws ParserException {
        try {
            // allow modification of name by environment provider
            name = state.filterLibraryName(name + ".stlx");
            if (new File(name).isFile()) {
                if (state.isLibraryLoaded(name)) {
                    return new Block();
                } else {
                    state.libraryWasLoaded(name);
                    // parse the file contents (ANTLR will print its parser errors into stderr ...)
                    return parseBlock(state, new ANTLRFileStream(name));
                }
            } else {
                throw new FileNotReadableException("Library '" + name + "' could not be read.");
            }
        } catch (final IOException ioe) {
            throw new FileNotReadableException("Library '" + name + "' could not be found.");
        }
    }

    public static Block parseInteractive(final State state) throws ParserException {
        try {
            final InputStream stream = InputReader.getStream(state);

            // parse the input (ANTLR will print its parser errors into stderr ...)
            return parseBlock(state, new ANTLRInputStream(stream));

        } catch (final IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    public static Block parseStringToBlock(final State state, final String input) throws ParserException {
        try {
            final InputStream stream = new ByteArrayInputStream(input.getBytes());

            // parse the input (ANTLR will print its parser errors into stderr ...)
            return parseBlock(state, new ANTLRInputStream(stream));

        } catch (final IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    public static Expr parseStringToExpr(final State state, final String input) throws ParserException {
        try {
            final InputStream stream = new ByteArrayInputStream(input.getBytes());

            // parse the input (ANTLR will print its parser errors into stderr ...)
            return parseExpr(state, new ANTLRInputStream(stream));

        } catch (final IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    /* private methods */

    private static Block parseBlock(final State state, final ANTLRStringStream input) throws SyntaxErrorException {
        return (Block) handleFragmentParsing(state, input, BLOCK);
    }

    private static Expr parseExpr(final State state, final ANTLRStringStream input) throws SyntaxErrorException {
        return (Expr) handleFragmentParsing(state, input, EXPR);
    }

    private static CodeFragment handleFragmentParsing(final State state, final ANTLRStringStream input, final int type) throws SyntaxErrorException {
              SetlXgrammarLexer  lexer  = null;
              SetlXgrammarParser parser = null;
        final LinkedList<String> oldCap = state.getParserErrorCapture();
        try {
                                    lexer  = new SetlXgrammarLexer(input);
            final CommonTokenStream ts     = new CommonTokenStream(lexer);
                                    parser = new SetlXgrammarParser(ts);
            final long              startT = state.currentTimeMillis();

            // capture parser errors
            state.setParserErrorCapture(new LinkedList<String>());

            // set state objects for lexer & parser
            lexer.setSetlXState(state);
            parser.setSetlXState(state);

            // parse the input
            final CodeFragment      frag   = parseFragment(parser, type);

            // now ANTLR will add its parser errors into our capture ...

            // start optimizing the fragment
            Thread optimizer = null;
            if (frag != null) {// might happen if parser ran into errors
                optimizer = new OptimizerThread(frag);
                optimizer.start();
            }

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
                    state.addToParserErrorCount(1);
                    parser.emitErrorMessage(error);
                }
            }

            // update error count
            state.addToParserErrorCount(parser.getNumberOfSyntaxErrors() + lexer.getNumberOfSyntaxErrors());

            if (state.getParserErrorCount() > 0) {
                throw SyntaxErrorException.create(
                    state.getParserErrorCapture(),
                    state.getParserErrorCount() + " syntax error(s) encountered."
                );
            }

            // wait for optimization of the fragment, but max until 0.25s after start
            while((state.currentTimeMillis() - startT) < 250 &&
                  optimizer != null && optimizer.isAlive()
            ) {
                try {
                    Thread.sleep(5);
                } catch (final InterruptedException e) { /* don't care */ }
            }

            return frag;
        } catch (final RecognitionException re) {
            throw SyntaxErrorException.create(state.getParserErrorCapture(), re.getMessage());
        } catch (final NullPointerException npe) {
            if (lexer != null) {
                state.addToParserErrorCount(lexer.getNumberOfSyntaxErrors());
            }
            if (parser != null) {
                state.addToParserErrorCount(parser.getNumberOfSyntaxErrors());
            }
            if (state.getParserErrorCount() > 0) {
                // NullPointerException caused by syntax error(s)
                throw SyntaxErrorException.create(
                    state.getParserErrorCapture(),
                    state.getParserErrorCount() + " syntax error(s) encountered."
                );
            } else { // NullPointer in parse tree itself
                throw SyntaxErrorException.create(new LinkedList<String>(),"Parsed tree contains nullpointer.");
            }
        } finally {
            // restore error capture
            state.setParserErrorCapture(oldCap);
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

    // private subclass to execute optimization using a different thread
    private static class OptimizerThread extends Thread {
        private final CodeFragment mFragment;

        public OptimizerThread(final CodeFragment fragment) {
            mFragment = fragment;
        }

        @Override
        public void run() {
            mFragment.optimize();
        }
    }

}

