package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Base class, which most other classes representing some SetlX-code element
 * inherit.
 */
public abstract class CodeFragment implements Comparable<CodeFragment> {
    private static State toStringState = null;

    /**
     * Gather all bound and unbound variables in this fragment and its siblings.
     * Optimizes this fragment, if this can be safely done.
     *
     * @param state            Current state of the running setlX program.
     * @param boundVariables   Variables "assigned" in this fragment.
     * @param unboundVariables Variables not present in bound when used.
     * @param usedVariables    Variables present in bound when used.
     * @return true iff this fragment may be optimized if it is constant.
     */
    public abstract boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    );

    /**
     * Optimize this fragment based upon variable and constant expressions
     * contained inside it.
     *
     * @param state Current state of the running setlX program.
     */
    public final void optimize(final State state) {
        final List<String> boundVariables   = new ArrayList<>();
        final List<String> unboundVariables = new ArrayList<>();
        final List<String> usedVariables    = new ArrayList<>();
        collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    /**
     * Appends a string representation of this code fragment to the given
     * StringBuilder object.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toString(State)
     *
     * @param state Current state of the running setlX program.
     * @param sb    StringBuilder to append to.
     * @param tabs  Number of tabs to use as indentation for statements.
     */
    public abstract void appendString(
        final State state,
        final StringBuilder sb,
        final int tabs
    );

    /**
     * Returns a string representation of this code fragment.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toString()
     *
     * @param state Current state of the running setlX program.
     * @return      String representation.
     */
    public final String toString(final State state) {
        final StringBuilder sb = new StringBuilder();
        appendString(state, sb, 0);
        return sb.toString();
    }

    @Override
    public final String toString() {
        if (toStringState == null) {
            toStringState = new State();
        }
        return toString(toStringState);
    }

    /* term operations */

    /**
     * Generate term representing the code this fragment represents.
     *
     * @param state Current state of the running setlX program.
     * @return      Generated term.
     * @throws SetlException in case of some (user-) error.
     */
    public abstract Value toTerm(final State state) throws SetlException;

    @Override
    public abstract int compareTo(final CodeFragment other);

    /**
     * In order to compare "incomparable" values, e.g. of different subtypes of
     * CodeFragments, the return value of this function is used to establish some
     * semi arbitrary order to be used in compareTo():
     *
     * This ranking is necessary to allow sets and lists of different types.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#generateCompareToOrderConstant(Class)
     *
     * @return Number representing the order of this type in compareTo().
     */
    public abstract long compareToOrdering();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    /**
     * Generate the number representing the order of this type in compareTo().
     *
     * @see org.randoom.setlx.utilities.CodeFragment#compareToOrdering()
     *
     * @param _class Class for which to generate the number.
     * @return       Generated number.
     */
    protected static long generateCompareToOrderConstant(
            final Class<? extends CodeFragment> _class
    ) {
        final int    multiplicand = (int) 'z' - (int) '^';
        final String className    = _class.getCanonicalName().replace("org.randoom.setlx.","").replace('.','^').toLowerCase(Locale.ENGLISH);
        final int    length       = className.length();

              long   result       = 0;
        for (int i = 0; i < length; ++i) {
            char c = className.charAt(i);
            if (c >= '^' && c <= 'z') {
                result = (result * multiplicand) + ((int) c - (int) '^');
            }
        }
        return result;
    }
}

