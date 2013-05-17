package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.EndOfFileException;
import org.randoom.setlx.exceptions.FileNotReadableException;
import org.randoom.setlx.exceptions.ParserException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.grammar.*;
import org.randoom.setlx.statements.Block;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ParseSetlX {

    private final static int EXPR  =  1337;
    private final static int BLOCK = 31337;
    private final static int MAX_OPTIMIZE_MS      =  250;
    private       static int maxOptimizeMsOverall = 1000;

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
            final SetlErrorListener errorL = new SetlErrorListener(state);

            parser.setBuildParseTree(false);

            lexer.removeErrorListeners();
            parser.removeErrorListeners();

            lexer.addErrorListener(errorL);
            parser.addErrorListener(errorL);

            if (state.isRuntimeDebuggingEnabled()) {
                parser.addErrorListener(new DiagnosticErrorListener());
                // use more stringent parser mode
                parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
            }

            // capture parser errors
            state.setParserErrorCapture(new LinkedList<String>());

            // set state object for parser
            parser.setSetlXState(state);

            // result of the parsing run
            final CodeFragment fragment = parseFragment(parser, type);

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
            if (fragment != null) {
                optimizer = new OptimizerThread(fragment, state);
                optimizer.start();

                // wait for optimization of the fragment, but max until 0.25s after start
                while(maxOptimizeMsOverall > 0 &&
                      (state.currentTimeMillis() - startT) < MAX_OPTIMIZE_MS &&
                      optimizer != null && optimizer.isAlive()
                ) {
                    try {
                        Thread.sleep(5);
                        maxOptimizeMsOverall -= 5;
                    } catch (final InterruptedException e) { /* don't care */ }
                }
            }

            return fragment;
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
                throw SyntaxErrorException.create(
                    new LinkedList<String>(),
                    handleInternalError(state, "Internal error while parsing.", npe)
                );
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
    private static String handleInternalError(final State state, final String errorMsg, final Exception e) {
        if (state.isRuntimeDebuggingEnabled()) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(out));
            state.errWrite(out.toString());
        }
        return errorMsg + " Please report this error " +
               "including steps and/or code to reproduce to `setlx@randoom.org'.";
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
                state.errWriteLn(handleInternalError(state, "Internal error during optimization.", e));
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
                                      int                  charPositionInLine,
                                final String               msg,
                                final RecognitionException e
        ) {
            // display position, not index
            ++charPositionInLine;

            final StringBuilder buf = new StringBuilder();

            if (state.isRuntimeDebuggingEnabled() && recognizer instanceof Parser) {
                final List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
                Collections.reverse(stack);

                buf.append("rule stack: ");
                buf.append(stack);
                buf.append("\nline ");
                buf.append(line);
                buf.append(":");
                buf.append(charPositionInLine);
                buf.append(" at ");
                buf.append(offendingSymbol);
                buf.append(": ");
                buf.append(msg);
            } else {
                buf.append("line ");
                buf.append(line);
                buf.append(":");
                buf.append(charPositionInLine);
                buf.append(" ");
                buf.append(msg);
            }

            state.writeParserErrLn(buf.toString());
        }
    }


}

