package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.ParserException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapper Expression for SetlX Strings, which parses and expands $$-expressions
 * at runtime.
 */
public class StringConstructor extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(StringConstructor.class);

    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 9999;

    private final String            originalStr; // original String
    private final ArrayList<String> fragments;   // list of string fragments for after and between expressions
    private final ArrayList<Expr>   exprs;       // list of $-Expressions

    /**
     * Constructor, which parses $-Expressions in the string to create.
     *
     * @param state       Current state of the running SetlX program.
     * @param quoted      Is the created string quoted (via @-char)?
     * @param originalStr String read by the parser.
     */
    public StringConstructor(final State state, final boolean quoted, final String originalStr) {
        this(originalStr, new ArrayList<String>(), new ArrayList<Expr>());

        // Strip out double quotes which the parser left in
        final String orgStr = originalStr.substring(1, originalStr.length() - 1);
        final int    length = orgStr.length();

        if ( ! quoted) {
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
                            final Expr   exp  = ParseSetlX.parseStringToExpr(state, eStr);
                            // add inner expr to mExprs
                            this.exprs.add(exp);
                        } catch (final ParserException pe) {
                            /* Doing error handling here is futile, as outer parsing run,
                             * which called this constructor, will notice via the global
                             * error count and (later) halt.
                             * However we can at least provide the user with some feedback.
                             */
                            if (state.getParserErrorCount() > errCount) {
                                state.writeParserErrLn(
                                    "Error(s) while parsing string " + this.toString(state) + " {"
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
            this.exprs.trimToSize();
        } else {
            this.fragments.add(orgStr);
        }
    }

    private StringConstructor(final String originalStr, final ArrayList<String> fragments, final ArrayList<Expr> exprs) {
        this.originalStr    = originalStr;
        this.fragments      = fragments;
        this.exprs          = exprs;
    }

    @Override
    protected SetlString evaluate(final State state) throws SetlException {
        final Iterator<String>  fIter   = fragments.iterator();
        final Iterator<Expr>    eIter   = exprs.iterator();
        final StringBuilder     data    = new StringBuilder();

        // there always is at least one fragment, even if empty; add it to data
        SetlString.parseString(fIter.next(), data);

        while (eIter.hasNext() && fIter.hasNext()) {
            // eval expression, but fail gracefully
            final Expr    exp = eIter.next();
            try {
                exp.eval(state).appendUnquotedString(state, data, 0);
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

    @Override
    protected void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        for (final Expr expr : exprs) {
            expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(originalStr);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) throws SetlException {
        final Term result  = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList strList = new SetlList(fragments.size());
        for (final String str: fragments) {
            strList.addMember(state, new SetlString(str));
        }
        result.addMember(state, strList);

        final SetlList expList = new SetlList(exprs.size());
        for (final Expr expr: exprs) {
            expList.addMember(state, expr.toTerm(state));
        }
        result.addMember(state, expList);

        return result;
    }

    /**
     * Convert a term representing a StringConstructor into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting StringConstructor Expression.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static Expr termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlList && term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final StringBuilder     originalStr = new StringBuilder();
            final SetlList          frags       = (SetlList) term.firstMember();
            final SetlList          exps        = (SetlList) term.lastMember();

            final ArrayList<String> fragments   = new ArrayList<String>(frags.size());
            final ArrayList<Expr>   exprs       = new ArrayList<Expr>(exps.size());

            final Iterator<Value>   fIter       = frags.iterator();
            final Iterator<Value>   eIter       = exps.iterator();

            originalStr.append("\"");

            while (fIter.hasNext()) {
                final SetlString  sstring = (SetlString) fIter.next();
                final String      string  = sstring.getUnquotedString(state);
                originalStr.append(sstring.getEscapedString());
                fragments.add(string);

                if (eIter.hasNext()) {
                    final Expr expr = TermConverter.valueToExpr(state, eIter.next());
                    exprs.add(expr);
                    originalStr.append("$");
                    originalStr.append(expr.toString(state).replace("$", "\\$"));
                    originalStr.append("$");
                }
            }
            if (eIter.hasNext()) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
            originalStr.append("\"");
            return new StringConstructor(originalStr.toString(), fragments, exprs);
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

    @Override
    public int precedence() {
        return PRECEDENCE;
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

