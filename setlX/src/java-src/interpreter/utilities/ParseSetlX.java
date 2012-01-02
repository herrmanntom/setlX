package interpreter.utilities;

import grammar.*;

import interpreter.exceptions.EndOfFileException;
import interpreter.exceptions.FileNotReadableException;
import interpreter.exceptions.ParserException;
import interpreter.exceptions.SyntaxErrorException;
import interpreter.expressions.Expr;
import interpreter.statements.Block;
import interpreter.utilities.InputReader;

import org.antlr.runtime.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

public class ParseSetlX {

    private final	static  int EXPR    = 1337;
    private final	static  int BLOCK   = 31337;

    public static Block parseFile(String fileName) throws ParserException {
        try {
            // parse the file contents (Antlr will print its parser errors into stderr ...)
            return (Block) parseFragment(new ANTLRFileStream(fileName), BLOCK);

        } catch (IOException e) {
            throw new FileNotReadableException("File '" + fileName + "' could not be read.");
        }
    }

    public static Block parseInteractive() throws ParserException {
        try {
            InputStream         stream = InputReader.getStream();

            // parse the input (Antlr will print its parser errors into stderr ...)
            return (Block) parseFragment(new ANTLRInputStream(stream), BLOCK);

        } catch (IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    public static Block parseStringToBlock(String input) throws ParserException {
        try {
            InputStream         stream = new ByteArrayInputStream(input.getBytes());

            // parse the input (Antlr will print its parser errors into stderr ...)
            return (Block) parseFragment(new ANTLRInputStream(stream), BLOCK);

        } catch (IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    public static Expr parseStringToExpr(String input) throws ParserException {
        try {
            InputStream         stream = new ByteArrayInputStream(input.getBytes());

            // parse the input (Antlr will print its parser errors into stderr ...)
            return (Expr) parseFragment(new ANTLRInputStream(stream), EXPR);

        } catch (IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    private static CodeFragment parseFragment(ANTLRStringStream input, int type) throws SyntaxErrorException {
        SetlXgrammarParser  parser  = null;
        try {
            SetlXgrammarLexer   lexer  = new SetlXgrammarLexer(input);
            CommonTokenStream   ts     = new CommonTokenStream(lexer);
                                parser = new SetlXgrammarParser(ts);

            // parse the input
            CodeFragment        frag   = readFragment(parser, type);

            // now Antlr will print its parser errors into stderr ...

            /*     check for syntax errors at the end of the input stream     */

            // fill token stream until EOF is reached (parser fills only as far as its lookahead needs)
            ts.fill();
            // current index in stream of tokens
            int index = ts.index();

            /*
             *  If the index into the tokenStream (which was set by the parser)
             *  is not equal to the number of tokens in the complete input (minus EOF),
             *  some tokens where ignored by the parser.
             *
             *  Best guess: The parser encountered a syntax error after a valid rule.
             */
            if (index < (ts.size() - 1)) {
                // parse again to force displaying syntax error in remaining tokenStream
                readFragment(parser, type);

                // now Antlr will (again) print its parser errors into stderr ...

                // check if parser moved the index
                if (index == ts.index()) {
                    /*  Index was not moved. Probably epsilon can be derived from the start-rule.
                     *  However there are still tokens left...
                     *  THIS SHOULD NOT BE POSSIBLE IN SetlX!
                     */
                    throw new SyntaxErrorException("Input includes unidentified errors!");
                }
            }

            if (parser.getNumberOfSyntaxErrors() > 0) {
                throw new NullPointerException(); // different problem, but same handling as NullPointerException
            }

            return frag;
        } catch (RecognitionException re) {
            throw new SyntaxErrorException(re.getMessage());
        } catch (NullPointerException npe) {
            if (parser != null && parser.getNumberOfSyntaxErrors() > 0) {
                // NullPointerException caused by syntax error (or thrown because of errors)
                throw new SyntaxErrorException("" + parser.getNumberOfSyntaxErrors() + " syntax errors encountered.");
            } else { // NullPointer in parse tree itself
                throw new SyntaxErrorException("Parsed tree contains nullpointer.");
            }
        }
    }

    private static CodeFragment readFragment(SetlXgrammarParser parser, int type) throws RecognitionException {
        switch (type) {
            case EXPR:
                return parser.anyExpr();
            case BLOCK:
                return parser.initBlock();
            default:
                /* this should never be reached if above code is correct */
                return null;
        }
    }
}

