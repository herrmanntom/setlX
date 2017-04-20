package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.*;
import org.randoom.setlx.grammar.SetlXgrammarLexer;
import org.randoom.setlx.grammar.SetlXgrammarParser;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.statements.Block;

import org.antlr.v4.runtime.*;
//import org.antlr.v4.runtime.atn.PredictionMode;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
                throw new FileNotReadableException("File '" + fileName + "' could not be read.", null);
            }
        } catch (final IOException ioe) {
            throw new FileNotReadableException("File '" + fileName + "' could not be read.", ioe);
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
                throw new FileNotReadableException("Library '" + name + "' could not be read.", null);
            }
        } catch (final IOException ioe) {
            throw new FileNotReadableException("Library '" + name + "' could not be found.", ioe);
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
            throw new EndOfFileException(ioe);
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
            throw new EndOfFileException(ioe);
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
    public static OperatorExpression parseStringToExpr(final State state, final String input) throws ParserException, StopExecutionException {
        try {
            final InputStream stream = new ByteArrayInputStream(input.getBytes());

            // parse the input
            return parseExpr(state, new ANTLRInputStream(stream));

        } catch (final IOException ioe) {
            throw new EndOfFileException(ioe);
        }
    }

    /* private methods */

    private static Block parseBlock(final State state, final ANTLRInputStream input) throws SyntaxErrorException, StopExecutionException {
        return (Block) handleFragmentParsing(state, input, CodeType.BLOCK);
    }

    private static OperatorExpression parseExpr(final State state, final ANTLRInputStream input) throws SyntaxErrorException, StopExecutionException {
        return (OperatorExpression) handleFragmentParsing(state, input, CodeType.EXPR);
    }

    private static CodeFragment handleFragmentParsing(final State state, final ANTLRInputStream input, final CodeType type) throws SyntaxErrorException, StopExecutionException {
        SetlXgrammarLexer  lexer  = null;
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

            if (state.isRuntimeDebuggingEnabled()) {
                parser.addErrorListener(new DiagnosticErrorListener());
                // use more stringent parser mode
                // parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
            }

            // capture parser errors
            state.setParserErrorCapture(new LinkedList<String>());

            // set state object for parser
            parser.setSetlXState(state);

            final ParserRunner parserRunner = new ParserRunner(state, parser, type);
                  CodeFragment fragment     = null;
            try {
                // run ANTLR, which will add its parser errors into our capture ...
                String threadName = Thread.currentThread().getName();
                if (threadName.endsWith(BaseRunnable.StackSize.LARGE.getThreadNamePrefix()) || threadName.endsWith(BaseRunnable.StackSize.MEDIUM.getThreadNamePrefix())) {
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
    private static final class ParserRunner extends ErrorHandlingRunnable {
        private final SetlXgrammarParser    parser;
        private final CodeType              type;
        private       ImmutableCodeFragment result;

        private ParserRunner(State state, final SetlXgrammarParser parser, final CodeType type) {
            super(state, StackSize.MEDIUM);
            this.parser = parser;
            this.type   = type;
            this.result = null;
        }

        @Override
        public void exec(State state) {
            switch (type) {
                case EXPR:
                    result = parser.initExpr().oe;
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

    private static class SetlErrorListener extends BaseErrorListener {
        private static final String EOF_SIGN = "<EOF>";
        private static final String ALTERNATIVES_KEYWORD = "expecting {";
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

            final StringBuilder buffer = new StringBuilder();

            if (state.isRuntimeDebuggingEnabled() && recognizer instanceof org.antlr.v4.runtime.Parser) {
                final List<String> stack = ((org.antlr.v4.runtime.Parser)recognizer).getRuleInvocationStack();
                Collections.reverse(stack);

                buffer.append("rule stack: ");
                buffer.append(stack);
                buffer.append("\n");
            }
            buffer.append("line ");
            buffer.append(line);
            buffer.append(":");
            buffer.append(charPositionInLine);
            buffer.append(" ");

            if (msg.contains(ALTERNATIVES_KEYWORD) && msg.endsWith("}")) {
                int keywordLength = ALTERNATIVES_KEYWORD.length();
                int keywordEnd = msg.indexOf(ALTERNATIVES_KEYWORD) + keywordLength;
                buffer.append(msg.substring(0, keywordEnd - 1));

                final Iterator<String> alternatives = sortAlternatives(msg.substring(keywordEnd, msg.length() -1)).iterator();
                while (alternatives.hasNext()) {
                    buffer.append(alternatives.next());
                    if (alternatives.hasNext()) {
                        buffer.append(", ");
                    }
                }
            } else {
                buffer.append(msg);
            }

            state.writeParserErrLn(buffer.toString());
        }

        private Collection<String> sortAlternatives(String listOfAlternatives) {
            Collection<String> sortedWords = new TreeSet<>();
            Collection<String> sortedOperators = new TreeSet<>();
            List<String> alternatives = Arrays.asList(listOfAlternatives.split(", "));
            boolean containsEOF = false;
            for (String alternative : alternatives) {
                if (alternative.equals(EOF_SIGN)) {
                    containsEOF = true;
                } else {
                    if (alternative.length() > 1 && alternative.startsWith("'") && alternative.endsWith("'")) {
                        alternative = alternative.substring(1, alternative.length() - 1);
                    }
                    if (alternative.matches("[a-zA-Z]+")) {
                        sortedWords.add(alternative);
                    } else {
                        sortedOperators.add(alternative);
                    }
                }
            }
            ArrayList<String> allAlternatives = new ArrayList<>(sortedWords.size() + sortedOperators.size() + 1);
            allAlternatives.addAll(sortedOperators);
            allAlternatives.addAll(sortedWords);
            if (containsEOF) {
                allAlternatives.add(EOF_SIGN);
            }
            return allAlternatives;
        }
    }


}

