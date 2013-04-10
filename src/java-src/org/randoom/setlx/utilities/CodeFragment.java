package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Base class, which most other classes representing some SetlX-code element
 * inherit.
 */
public abstract class CodeFragment {

    /**
     * Gather all bound and unbound variables in this fragment and its siblings.
     * Optimizes this fragment, if this can be safely done.
     *
     * @param boundVariables   Variables "assigned" in this fragment.
     * @param unboundVariables Variables not present in bound when used.
     * @param usedVariables    Variables present in bound when used.
     */
    protected abstract void collectVariablesAndOptimize (
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
        collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
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
        final State bubble = new StateImplementation();
        return toString(bubble);
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

    /* Java Code generation */

    /**
     * Appends the equivalent Java Code representation of this code fragment
     * to the given StringBuilder object.
     *
     * @param state  Current state of the running setlX program.
     * @param header Java Header to append to.
     * @param code   Java Code to append to.
     * @param tabs   Number of tabs to use as indentation for statements.
     */
    public /*abstract*/ void appendJavaCode(
            final State         state,
            final Set<String>   header,
            final StringBuilder code,
            final int           tabs
    ) {
        state.appendLineStart(code, tabs);
        code.append("/*");
        code.append(state.getEndl());

        appendString(state, code, tabs);
        code.append(state.getEndl());

        state.appendLineStart(code, tabs);
        code.append("*/");
    }

    /**
     * Generate the equivalent Java Code representation of this code fragment.
     *
     * @param state       Current state of the running setlX program.
     * @param className   Name of class to be generated.
     * @param envProvider Class of environment provider to use.
     * @return            Generated Java Class.
     */
    public String toJavaCode(
            final State                                state,
            final String                               className,
            final Class<? extends EnvironmentProvider> envProvider
    ) {
        final StringBuilder result = new StringBuilder();

        final Set<String>   header = new TreeSet<String>();
        final StringBuilder code   = new StringBuilder();

        this.appendJavaCode(state, header, code, 3);

        // header (i.e. imports)
        header.add("import " + State.class.getCanonicalName() + ";");
        header.add("import " + StateImplementation.class.getCanonicalName() + ";");
        header.add("import " + SetlException.class.getCanonicalName() + ";");
        header.add("import " + List.class.getCanonicalName() + ";");

        for (final String line : header) {
            result.append(line);
            result.append(state.getEndl());
        }

        // beginning of class
        result.append("public class ");
        result.append(className);
        result.append(" {");
        result.append(state.getEndl());

        // main function
        state.appendLineStart(result, 1);
        result.append("public static void main(final String[] args) {");
        result.append(state.getEndl());

        // body of main function
        state.appendLineStart(result, 2);
        result.append("final State state = new StateImplementation();");
        result.append(state.getEndl());

        state.appendLineStart(result, 2);
        result.append("state.setEnvironmentProvider(new ");
        result.append(envProvider.getCanonicalName());
        result.append("());");
        result.append(state.getEndl());

        state.appendLineStart(result, 2);
        result.append("try {");
        result.append(state.getEndl());

        // actual code of the user
        result.append(code);
        result.append(state.getEndl());

        state.appendLineStart(result, 2);
        result.append("} catch (");
        result.append(SetlException.class.getSimpleName());
        result.append(" se) {");
        result.append(state.getEndl());
        state.appendLineStart(result, 3);
        result.append("printExceptionsTrace(state, se.getTrace());");
        result.append(state.getEndl());
        state.appendLineStart(result, 2);
        result.append("}");
        result.append(state.getEndl());

        // end of main function
        state.appendLineStart(result, 1);
        result.append("}");
        result.append(state.getEndl());

        // printExceptionsTrace function
        state.appendLineStart(result, 1);result.append("private static void printExceptionsTrace(final State state, final List<String> trace) {");result.append(state.getEndl());
        state.appendLineStart(result, 2);result.append(    "final int end = trace.size();");result.append(state.getEndl());
        state.appendLineStart(result, 2);result.append(    "final int max = 40;");result.append(state.getEndl());
        state.appendLineStart(result, 2);result.append(    "final int m_2 = max / 2;");result.append(state.getEndl());
        state.appendLineStart(result, 2);result.append(    "for (int i = end - 1; i >= 0; --i) {");result.append(state.getEndl());
        state.appendLineStart(result, 3);result.append(        "if (end > max && i > m_2 - 1 && i < end - (m_2 + 1)) {");result.append(state.getEndl());
        state.appendLineStart(result, 4);result.append(            "if (i == m_2) {");result.append(state.getEndl());
        state.appendLineStart(result, 5);result.append(                "state.errWriteLn(\" ... \\n     omitted \" + (end - max) + \" messages\\n ... \");");result.append(state.getEndl());
        state.appendLineStart(result, 4);result.append(            "}");result.append(state.getEndl());
        state.appendLineStart(result, 3);result.append(        "} else {");result.append(state.getEndl());
        state.appendLineStart(result, 4);result.append(            "state.errWriteLn(trace.get(i));");result.append(state.getEndl());
        state.appendLineStart(result, 3);result.append(        "}");result.append(state.getEndl());
        state.appendLineStart(result, 2);result.append(    "}");result.append(state.getEndl());
        state.appendLineStart(result, 1);result.append("}");result.append(state.getEndl());

        // end of class
        result.append("}");
        result.append(state.getEndl());

        result.append(state.getEndl());

        return result.toString();
    }
}

