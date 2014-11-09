package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.*;
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
        return (Block) handleFragmentParsing(state, input, CodeType.BLOCK);
    }

    private static Expr parseExpr(final State state, final ANTLRInputStream input) throws SyntaxErrorException, StopExecutionException {
        return (Expr) handleFragmentParsing(state, input, CodeType.EXPR);
    }

    private static CodeFragment handleFragmentParsing(final State state, final ANTLRInputStream input, final CodeType type) throws SyntaxErrorException, StopExecutionException {
        SetlXgrammarLexer lexer = null;
        SetlXgrammarParser parser = null;
        final LinkedList<String> oldCap = state.getParserErrorCapture();
        try {
            lexer = new SetlXgrammarLexer(input);
            final CommonTokenStream ts = new CommonTokenStream(lexer);
            parser = new SetlXgrammarParser(ts);
            final SetlErrorListener errorL = new SetlErrorListener(state);

            parser.setBuildParseTree(false);

            lexer.removeErrorListeners();
            parser.removeErrorListeners();

            lexer.addErrorListener(errorL);
            parser.addErrorListener(errorL);

//            if (state.isRuntimeDebuggingEnabled()) {
//                parser.addErrorListener(new DiagnosticErrorListener());
//                // use more stringent parser mode
//                // parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
//            }

            // capture parser errors
            state.setParserErrorCapture(new LinkedList<String>());

            // set state object for parser
            parser.setSetlXState(state);

            final ParserRunner parserRunner = new ParserRunner(state, parser, type);
                  CodeFragment fragment     = null;
            try {
                // run ANTLR, which will add its parser errors into our capture ...
                if (Thread.currentThread().getName().endsWith(parserRunner.getThreadName())) {
                    parserRunner.exec(state);
                } else {
                    parserRunner.startAsThread();
                }

                // get parsed fragment
                fragment = parserRunner.getResult();

            } catch (StopExecutionException see) {
                throw see;
            } catch (SetlException e) {
                // impossible
                e.printStackTrace();
            }

            // update error count
            state.addToParserErrorCount(parser.getNumberOfSyntaxErrors());

            if (state.getParserErrorCount() > 0) {
                throw SyntaxErrorException.create(
                        state.getParserErrorCapture(),
                        state.getParserErrorCount() + " syntax error(s) encountered."
                );
            }

            // start optimizing the fragment
            if (fragment != null) {
                try {
                    new OptimizerRunner(state, fragment).startAsThread();
                } catch (final StackOverflowError soe) {
                    state.errWriteLn("Error while optimizing parsed code.");
                    state.errWriteOutOfStack(soe, false);
                } catch (final OutOfMemoryError oome) {
                    state.errWriteLn("Error while optimizing parsed code.");
                    state.errWriteOutOfMemory(false, false);
                } catch (final RuntimeException e) {
                    state.errWriteLn("Error while optimizing parsed code.");
                    state.errWriteInternalError(e);
                } catch (SetlException e) {
                    // don't care
                }
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
    private static class ParserRunner extends BaseRunnable {
        private final SetlXgrammarParser parser;
        private final CodeType           type;
        private       CodeFragment       result;

        /*package*/ ParserRunner(State state, final SetlXgrammarParser parser, final CodeType type) {
            super(state, false);
            this.parser = parser;
            this.type   = type;
            this.result = null;
        }

        @Override
        public void exec(State state) {
            switch (type) {
                case EXPR:
                    result = parser.initExpr().ae;
                    break;
                case BLOCK:
                    result = parser.initBlock().blk;
                    break;
            }
        }

        @Override
        public String getThreadName() {
            return "parser";
        }

        public CodeFragment getResult() {
            return result;
        }
    }

    // private subclass to execute optimization using a different thread
    private static class OptimizerRunner extends BaseRunnable {
        private final CodeFragment fragment;

        /*package*/ OptimizerRunner(final State state, final CodeFragment fragment) {
            super(state, false);
            this.fragment = fragment;
        }

        @Override
        public void exec(State state) {
            fragment.optimize(state);
        }

        @Override
        public String getThreadName() {
            return "optimizer";
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

            if (state.isRuntimeDebuggingEnabled() && recognizer instanceof org.antlr.v4.runtime.Parser) {
                final List<String> stack = ((org.antlr.v4.runtime.Parser)recognizer).getRuleInvocationStack();
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

