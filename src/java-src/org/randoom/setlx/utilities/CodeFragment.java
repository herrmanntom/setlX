package org.randoom.setlx.utilities;

import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class, which most other classes representing some SetlX-code element
 * inherit.
 */
public abstract class CodeFragment {

    /**
     * Gather all bound and unbound variables in this fragment and its siblings.
     * Optimizes this fragment, if this can be safely done.
     *
     * @param state            Current state of the running setlX program.
     * @param boundVariables   Variables "assigned" in this fragment.
     * @param unboundVariables Variables not present in bound when used.
     * @param usedVariables    Variables present in bound when used.
     */
    public abstract void collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    );

    /**
     * Optimize this fragment based upon variable and constant expressions
     * contained inside it.
     */
    public final void optimize() {
        final List<String> boundVariables   = new ArrayList<String>();
        final List<String> unboundVariables = new ArrayList<String>();
        final List<String> usedVariables    = new ArrayList<String>();
        collectVariablesAndOptimize(new State(), boundVariables, unboundVariables, usedVariables);
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
        return toString(new State());
    }

    /* term operations */

    /**
     * Generate term representing the code this fragment represents.
     *
     * @param state Current state of the running setlX program.
     * @return      Generated term.
     */
    public abstract Value toTerm(final State state);

    /**
     * Generate the functional character used in toTerm() based upon the
     * simple name of the given class.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toTerm(State)
     *
     * @param _class Class from which to take the name.
     * @return       Generated functional character.
     */
    protected final static String generateFunctionalCharacter(
        final Class<? extends CodeFragment> _class
    ) {
        final String className = _class.getSimpleName();
        return "^" + Character.toLowerCase(className.charAt(0)) + className.substring(1);
    };

}

