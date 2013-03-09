package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.EndOfFileException;
import org.randoom.setlx.exceptions.FileNotReadableException;
import org.randoom.setlx.exceptions.ParserException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.grammar.*;
import org.randoom.setlx.statements.Block;

import org.antlr.v4.runtime.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

public class ParseSetlX {

    private final static int EXPR  =  1337;
    private final static int BLOCK = 31337;

    public static Block parseFile(final State state, String fileName) throws ParserException {
        try {
            // allow modification of fileName/path by environment provider
            fileName = state.filterFileName(fileName);
            if (new File(fileName).isFile()) {
                // parse the file contents
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
                    // parse the file contents
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

            // parse the input
            return parseBlock(state, new ANTLRInputStream(stream));

        } catch (final IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    public static Block parseStringToBlock(final State state, final String input) throws ParserException {
        try {
            final InputStream stream = new ByteArrayInputStream(input.getBytes());

            // parse the input
            return parseBlock(state, new ANTLRInputStream(stream));

        } catch (final IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    public static Expr parseStringToExpr(final State state, final String input) throws ParserException {
        try {
            final InputStream stream = new ByteArrayInputStream(input.getBytes());

            // parse the input
            return parseExpr(state, new ANTLRInputStream(stream));

        } catch (final IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    /* private methods */

    private static Block parseBlock(final State state, final ANTLRInputStream input) throws SyntaxErrorException {
        return (Block) handleFragmentParsing(state, input, BLOCK);
    }

    private static Expr parseExpr(final State state, final ANTLRInputStream input) throws SyntaxErrorException {
        return (Expr) handleFragmentParsing(state, input, EXPR);
    }

    private static CodeFragment handleFragmentParsing(final State state, final ANTLRInputStream input, final int type) throws SyntaxErrorException {
              SetlXgrammarLexer  lexer  = null;
              SetlXgrammarParser parser = null;
        final LinkedList<String> oldCap = state.getParserErrorCapture();
        try {
                                    lexer  = new SetlXgrammarLexer(input);
            final CommonTokenStream ts     = new CommonTokenStream(lexer);
                                    parser = new SetlXgrammarParser(ts);
            final long              startT = state.currentTimeMillis();

            parser.setBuildParseTree(false);
            parser.removeErrorListeners();
            parser.addErrorListener(new SetlErrorListener(state));
            if (state.unhideExceptions()) {
                parser.addErrorListener(new DiagnosticErrorListener());
            }

            // capture parser errors
            state.setParserErrorCapture(new LinkedList<String>());

            // set state objects for parser
            parser.setSetlXState(state);

            // parse the input
            final CodeFragment frag = parseFragment(parser, type);

            // now ANTLR will add its parser errors into our capture ...

            // update error count
            state.addToParserErrorCount(parser.getNumberOfSyntaxErrors());

            if (state.getParserErrorCount() > 0) {
                throw SyntaxErrorException.create(
                    state.getParserErrorCapture(),
                    state.getParserErrorCount() + " syntax error(s) encountered."
                );
            }

            // start optimizing the fragment
            Thread optimizer = null;
            if (frag != null) {
                optimizer = new OptimizerThread(frag, state);
                optimizer.start();

                // wait for optimization of the fragment, but max until 0.25s after start
                while((state.currentTimeMillis() - startT) < 250 &&
                      optimizer != null && optimizer.isAlive()
                ) {
                    try {
                        Thread.sleep(5);
                    } catch (final InterruptedException e) { /* don't care */ }
                }
            }

            return frag;
        } catch (final RecognitionException re) {
            throw SyntaxErrorException.create(state.getParserErrorCapture(), re.getMessage());
        } catch (final NullPointerException npe) {
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
                if (state.unhideExceptions()) {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    npe.printStackTrace(new PrintStream(out));
                    state.errWrite(out.toString());
                }
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
                return parser.initExpr().ae;
            case BLOCK:
                return parser.initBlock().blk;
            default:
                /* this should never be reached if surrounding code is correct */
                return null;
        }
    }

    // private subclass to execute optimization using a different thread
    private static class OptimizerThread extends Thread {
        private final CodeFragment                      fragment;
        private final org.randoom.setlx.utilities.State state;

        /*package*/ OptimizerThread(final CodeFragment fragment, final org.randoom.setlx.utilities.State state) {
            this.fragment = fragment;
            this.state    = state;
        }

        @Override
        public void run() {
            try {
               fragment.optimize();
            } catch (final Exception e) {
                state.errWriteLn(
                    "Internal error during optimization. Please report this error " +
                    "including steps and/or code to reproduce to `setlx@randoom.org'."
                );
                if (state.unhideExceptions()) {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(out));
                    state.errWrite(out.toString());
                }
            }
        }
    }

    public static class SetlErrorListener extends BaseErrorListener {
        private final State state;

        /*package*/ SetlErrorListener(final State state) {
            this.state = state;
        }

        @Override
        public void syntaxError(final Recognizer<?, ?>     recognizer,
                                final Object               offendingSymbol,
                                final int                  line,
                                final int                  charPositionInLine,
                                final String               msg,
                                final RecognitionException e
        ) {
            state.writeParserErrLn("line " + line + ":" + charPositionInLine + " " + msg);
        }
    }


}

