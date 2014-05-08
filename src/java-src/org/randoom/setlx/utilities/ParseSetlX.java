package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.EndOfFileException;
import org.randoom.setlx.exceptions.FileNotReadableException;
import org.randoom.setlx.exceptions.ParserException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.grammar.*;
import org.randoom.setlx.statements.Block;

import org.antlr.v4.runtime.*;
//import org.antlr.v4.runtime.atn.PredictionMode;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class interfaces with the antlr parser and provides some error handling.
 */
public class ParseSetlX {

    private enum CodeType {
        EXPR,
        BLOCK
    }

    /**
     * Parse a code block from a file.
     *
     * @param state                   Current state of the running setlX program.
     * @param fileName                Path and name of the file to read.
     * @return                        Parsed block of setlX-Code.
     * @throws ParserException        Thrown in case of parser errors.
     * @throws StopExecutionException Thrown when an InterruptedException got caught.
     */
    public static Block parseFile(final State state, String fileName) throws ParserException, StopExecutionException {
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

    /**
     * Parse a code block from a library file.
     * Note:
     *   The default ".stlx" extension is added to the name.
     *   The resulting name is expected to be relative to the library path.
     *   Libraries are only loaded once - an empty block is returned if it was loaded before.
     *
     * @param state                   Current state of the running setlX program.
     * @param name                    Name of the library to read.
     * @return                        Parsed block of setlX-Code.
     * @throws ParserException        Thrown in case of parser errors.
     * @throws StopExecutionException Thrown when an InterruptedException got caught.
     */
    public static Block parseLibrary(final State state, String name) throws ParserException, StopExecutionException {
        try {
            // allow modification of name by environment provider
            name = state.filterLibraryName(name + ".stlx");
            if (new File(name).isFile()) {
                if (state.isLibraryLoaded(name)) {
                    return new Block(state);
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

    /**
     * Parse a code block while reading from standard-in.
     *
     * @param state                   Current state of the running setlX program.
     * @return                        Parsed block of setlX-Code.
     * @throws ParserException        Thrown in case of parser errors.
     * @throws StopExecutionException Thrown when an InterruptedException got caught.
     */
    public static Block parseInteractive(final State state) throws ParserException, StopExecutionException {
        try {
            final InputStream stream = InputReader.getStream(state, state.getEndl(), state.isMultiLineEnabled());

            // parse the input
            return parseBlock(state, new ANTLRInputStream(stream));

        } catch (final IOException ioe) {
            throw new EndOfFileException();
        }
    }

    /**
     * Parse a code block from a string.
     *
     * @param state                   Current state of the running setlX program.
     * @param input                   String to read.
     * @return                        Parsed block of setlX-Code.
     * @throws ParserException        Thrown in case of parser errors.
     * @throws StopExecutionException Thrown when an InterruptedException got caught.
     */
    public static Block parseStringToBlock(final State state, final String input) throws ParserException, StopExecutionException {
        try {
            final InputStream stream = new ByteArrayInputStream(input.getBytes());

            // parse the input
            return parseBlock(state, new ANTLRInputStream(stream));

        } catch (final IOException ioe) {
            throw new EndOfFileException();
        }
    }

    /**
     * Parse an expression from a string.
     *
     * @param state                   Current state of the running setlX program.
     * @param input                   String to read.
     * @return                        Parsed setlX expression.
     * @throws ParserException        Thrown in case of parser errors.
     * @throws StopExecutionException Thrown when an InterruptedException got caught.
     */
    public static Expr parseStringToExpr(final State state, final String input) throws ParserException, StopExecutionException {
        try {
            final InputStream stream = new ByteArrayInputStream(input.getBytes());

            // parse the input
            return parseExpr(state, new ANTLRInputStream(stream));

        } catch (final IOException ioe) {
            throw new EndOfFileException();
        }
    }

    /* private methods */

    private static Block parseBlock(final State state, final ANTLRInputStream input) throws SyntaxErrorException, StopExecutionException {
        return (Block) handleFragmentParsing(state, input, CodeType.BLOCK, false);
    }

    private static Expr parseExpr(final State state, final ANTLRInputStream input) throws SyntaxErrorException, StopExecutionException {
        return (Expr) handleFragmentParsing(state, input, CodeType.EXPR, true);
    }

    private static CodeFragment handleFragmentParsing(final State state, final ANTLRInputStream input, final CodeType type, final boolean executeInThread) throws SyntaxErrorException, StopExecutionException {
              SetlXgrammarLexer  lexer  = null;
              SetlXgrammarParser parser = null;
        final LinkedList<String> oldCap = state.getParserErrorCapture();
        try {
                                    lexer  = new SetlXgrammarLexer(input);
            final CommonTokenStream ts     = new CommonTokenStream(lexer);
                                    parser = new SetlXgrammarParser(ts);
            final SetlErrorListener errorL = new SetlErrorListener(state);

            parser.setBuildParseTree(false);

            lexer.removeErrorListeners();
            parser.removeErrorListeners();

            lexer.addErrorListener(errorL);
            parser.addErrorListener(errorL);

            if (state.isRuntimeDebuggingEnabled()) {
                parser.addErrorListener(new DiagnosticErrorListener());
                // use more stringent parser mode
                // parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
            }

            // capture parser errors
            state.setParserErrorCapture(new LinkedList<String>());

            // set state object for parser
            parser.setSetlXState(state);

            final ParserThread parserThread = new ParserThread(parser, type);
                  CodeFragment fragment     = null;

            try {
                parserThread.setName(Thread.currentThread().getName() + "::parser");
                if (executeInThread) {
                    parserThread.run();
                } else {
                    parserThread.start();
                    parserThread.join();
                }
                fragment = parserThread.result;
            } catch (final InterruptedException e) {
                throw new StopExecutionException();
            }

            // handle exceptions thrown in thread
            if (parserThread.error != null) {
                if (parserThread.error instanceof RecognitionException) {
                    throw (RecognitionException) parserThread.error;
                } else if (parserThread.error instanceof StackOverflowError) {
                    throw (StackOverflowError) parserThread.error;
                } else if (parserThread.error instanceof OutOfMemoryError) {
                    try {
                        // free some memory
                        state.resetState();
                        // give hint to the garbage collector
                        System.gc();
                        // sleep a while
                        Thread.sleep(50);
                    } catch (final InterruptedException e) {
                        /* don't care */
                    }
                    throw (OutOfMemoryError) parserThread.error;
                } else if (parserThread.error instanceof RuntimeException) {
                    throw (RuntimeException) parserThread.error;
                }
            }

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
                optimizer.setName(Thread.currentThread().getName() + "::optimizer");
                optimizer.start();
            }

            return fragment;
        } catch (final RecognitionException re) {
            throw SyntaxErrorException.create(
                state.getParserErrorCapture(),
                re.getMessage()
            );
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
                state.errWriteLn("Error while parsing code.");
                state.errWriteInternalError(npe);
                throw SyntaxErrorException.create(
                    new LinkedList<String>(),
                    "Error while parsing code."
                );
            }
        } finally {
            // restore error capture
            state.setParserErrorCapture(oldCap);
        }
    }

    // private subclass to execute the parser in a different thread
    private static class ParserThread extends Thread {
        private final SetlXgrammarParser parser;
        private final CodeType           type;
        private       CodeFragment       result;
        private       Throwable          error;


        /*package*/ ParserThread(final SetlXgrammarParser parser, final CodeType type) {
            this.parser = parser;
            this.type   = type;
            this.result = null;
            this.error  = null;
        }

        @Override
        public void run() {
            try {
                switch (type) {
                    case EXPR:
                        result = parser.initExpr().ae;
                        break;
                    case BLOCK:
                        result = parser.initBlock().blk;
                        break;
                }
            } catch (final RecognitionException re) {
                result = null;
                error  = re;
            } catch (final StackOverflowError soe) {
                result = null;
                error  = soe;
            } catch (final OutOfMemoryError oome) {
                result = null;
                error  = oome;
            } catch (final RuntimeException e) {
                result = null;
                error  = e;
            }
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
                fragment.optimize(state);
            } catch (final StackOverflowError soe) {
                state.errWriteLn("Error while optimizing parsed code.");
                state.errWriteOutOfStack(soe, false);
            } catch (final OutOfMemoryError oome) {
                state.errWriteLn("Error while optimizing parsed code.");
                state.errWriteOutOfMemory(false, false);
            } catch (final RuntimeException e) {
                state.errWriteLn("Error while optimizing parsed code.");
                state.errWriteInternalError(e);
            }
        }
    }

    private static class SetlErrorListener extends BaseErrorListener {
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

