package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.ParserException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapper Expression for SetlX Strings, which parses and expands $$-expressions
 * at runtime.
 */
public class StringConstructor extends AZeroOperator {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(StringConstructor.class);

    private final String originalStr;                        // original String
    private final ArrayList<String> fragments;               // list of string fragments for after and between expressions
    private final ArrayList<OperatorExpression> expressions; // list of $-Expressions

    /**
     * Constructor, which parses $-Expressions in the string to create.
     *
     * @param state       Current state of the running SetlX program.
     * @param originalStr String read by the parser.
     */
    public StringConstructor(final State state, final String originalStr) {
        this(originalStr, new ArrayList<String>(), new ArrayList<OperatorExpression>());

        // Strip out double quotes which the parser left in
        final String orgStr = originalStr.substring(1, originalStr.length() - 1);
        final int    length = orgStr.length();

        final StringBuilder fragment  = new StringBuilder(); // buffer for string fragment
        final StringBuilder expr      = new StringBuilder(); // buffer for inner expr string
              boolean       innerExpr = false;               // currently reading inner expr ?
        for (int i = 0; i < length; ++i) {
            final char c = orgStr.charAt(i);  // current char
            final char n = (i+1 < length)? orgStr.charAt(i+1) : '\0';  // next char
            if (innerExpr) {
                if (c == '$') {
                    // end of inner expr
                    innerExpr = false;
                    // parse inner expr
                    final int errCount = state.getParserErrorCount();
                    try {
                        // SetlString parses escape characters properly
                        final String eStr = SetlString.parseString(expr.toString());
                        final OperatorExpression exp = ParseSetlX.parseStringToExpr(state, eStr);
                        // add inner expr to mExprs
                        this.expressions.add(exp);
                    } catch (final ParserException pe) {
                        /* Doing error handling here is futile, as outer parsing run,
                         * which called this constructor, will notice via the global
                         * error count and (later) halt.
                         * However we can at least provide the user with some feedback.
                         */
                        if (state.getParserErrorCount() > errCount) {
                            state.writeParserErrLn(
                                "Error(s) while parsing string " + originalStr + " {"
                            );
                            if (pe instanceof SyntaxErrorException) {
                                for (final String err : ((SyntaxErrorException) pe).getErrors()) {
                                    state.writeParserErrLn(
                                        "\t" + err
                                    );
                                }
                            } else {
                                state.writeParserErrLn(
                                    pe.getMessage()
                                );
                            }
                            state.writeParserErrLn(
                                "}"
                            );
                        }
                    } catch (final StopExecutionException see) {
                        state.errWriteInternalError(see);
                    }
                    // clear expression
                    expr.setLength(0);
                } else {
                    // continue expr string
                    expr.append(c);
                }
            } else {
                if (c == '\\' && n == '$') {
                    // escaped dollar
                    fragment.append('$');
                    i++; // jump over next char
                } else if (c == '$') {
                    // end outer string
                    this.fragments.add(fragment.toString());
                    fragment.setLength(0);
                    // start inner expression
                    innerExpr = true;
                } else {
                    // continue outer string
                    fragment.append(c);
                }
            }
        }
        if (innerExpr) { // inner expr not complete
            /* Doing error handling here is futile
             * Instead make outer parsing run, which called this constructor,
             * notice this error and (later) halt.
             */
            state.addToParserErrorCount(1);
            // However we can at least provide the user with some feedback.
            state.writeParserErrLn(
                "Error(s) while parsing string " + this.toString(state) + " {\n"
              + "\tclosing '$' missing\n"
              + "}"
            );
        }
        // outer string must always be appended, even if empty
        this.fragments.add(fragment.toString());

        this.fragments.trimToSize();
        this.expressions.trimToSize();
    }

    private StringConstructor(final String originalStr, final ArrayList<String> fragments, final ArrayList<OperatorExpression> expressions) {
        this.originalStr = originalStr;
        this.fragments   = fragments;
        this.expressions = expressions;
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        boolean allowOptimization = true;
        for (final OperatorExpression expr : expressions) {
            allowOptimization = expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
                    && allowOptimization;
        }
        return allowOptimization;
    }

    @Override
    public SetlString evaluate(State state, ArrayDeque<Value> values, OperatorExpression operatorExpression, int currentStackDepth) throws SetlException {
        final Iterator<String>             fIter = fragments.iterator();
        final Iterator<OperatorExpression> eIter = expressions.iterator();
        final StringBuilder                data  = new StringBuilder();

        // there always is at least one fragment, even if empty; add it to data
        SetlString.parseString(fIter.next(), data);

        while (eIter.hasNext() && fIter.hasNext()) {
            // eval expression, but fail gracefully
            final OperatorExpression exp = eIter.next();
            try {
                exp.evaluate(state).appendUnquotedString(state, data, 0);
            } catch (final SetlException se) {
                data.append("$Error: ");
                data.append(se.getMessage());
                data.append("$");
            }

            // append string fragment following expr
            SetlString.parseString(fIter.next(), data);
        }

        // now expr-list should be empty in all cases
        if (eIter.hasNext()) {
            throw new UndefinedOperationException("Internal error in string construction!");
        }

        // however there might still be some fragments left
        while (fIter.hasNext()) {
            SetlString.parseString(fIter.next(), data);
        }

        return SetlString.newSetlStringFromSB(data);
    }

    /* string operations */

    @Override
    public void appendOperatorSign(final State state, final StringBuilder sb, List<String> expressions) {
        sb.append(originalStr);
    }

    /* term operations */

    @Override
    public Value modifyTerm(State state, Term term, ArrayDeque<Value> termFragments) throws SetlException {
        term.addMember(state, new SetlString(originalStr.substring(1, originalStr.length() -1)));

        final SetlList strList = new SetlList(fragments.size());
        for (final String str: fragments) {
            strList.addMember(state, new SetlString(str));
        }
        term.addMember(state, strList);

        final SetlList expList = new SetlList(expressions.size());
        for (final OperatorExpression expr: expressions) {
            expList.addMember(state, expr.toTerm(state));
        }
        term.addMember(state, expList);

        return term;
    }

    /**
     * Append the operator represented by a term to the supplied operator stack.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @param operatorStack            Operator to append to.
     * @throws TermConversionException If term is malformed.
     */
    public static void appendToOperatorStack(final State state, final Term term, FragmentList<AOperator> operatorStack) throws TermConversionException {
        try {
            if (term.size() != 3 || ! (term.firstMember().getClass() == SetlString.class) || ! (term.getMember(2).getClass() == SetlList.class) || ! (term.lastMember().getClass() == SetlList.class)) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            } else {
                final SetlString originalStr = (SetlString) term.firstMember();
                final SetlList frags = (SetlList) term.getMember(2);
                final SetlList exps = (SetlList) term.lastMember();

                final ArrayList<String> fragments = new ArrayList<>(frags.size());
                final ArrayList<OperatorExpression> expressions = new ArrayList<>(exps.size());

                final Iterator<Value> fIterator = frags.iterator();
                final Iterator<Value> eIterator = exps.iterator();

                while (fIterator.hasNext()) {
                    final String string = fIterator.next().getUnquotedString(state);
                    fragments.add(string);

                    if (eIterator.hasNext()) {
                        final OperatorExpression expr = OperatorExpression.createFromTerm(state, eIterator.next());
                        expressions.add(expr);
                    }
                }
                if (eIterator.hasNext()) {
                    throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
                }
                operatorStack.add(new StringConstructor(originalStr.toString(state), fragments, expressions));
            }
        } catch (SetlException se) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER, se);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == StringConstructor.class) {
            return originalStr.compareTo(((StringConstructor) other).originalStr);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(StringConstructor.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == StringConstructor.class) {
            return originalStr.equals(((StringConstructor) obj).originalStr);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + originalStr.hashCode();
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}

