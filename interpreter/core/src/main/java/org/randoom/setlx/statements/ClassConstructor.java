package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlClass;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.List;

/**
 * A wrapper statement for SetlX class definitions.
 *
 * grammar rule:
 * statement
 *     : 'class' ID '(' procedureParameters ')' '{' block ('static' '{' block '}')? '}'
 *     | [..]
 *     ;
 *
 * implemented here as:
 *               ==  ==================================================================
 *              name                            classDefinition
 */
public class ClassConstructor extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(ClassConstructor.class);

    private final String    name;
    private final SetlClass classDefinition;

    /**
     * Create new ClassConstructor.
     *
     * @param name            Name of the class to construct.
     * @param classDefinition Definition of the class to create.
     */
    public ClassConstructor(final String name, final SetlClass classDefinition) {
        this.name            = name;
        this.classDefinition = classDefinition;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        state.putClassDefinition(name, classDefinition, FUNCTIONAL_CHARACTER);
        return null;
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        boundVariables.add(name);
        return classDefinition.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        classDefinition.appendString(state, name, sb, tabs);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(state, new SetlString(name));
        result.addMember(state, classDefinition.toTerm(state));

        return result;
    }

    /**
     * Re-generate a ClassConstructor statement from a term.
     *
     * @param  state                   Current state of the running setlX program.
     * @param  term                    Term to regenerate from.
     * @return                         ClassConstructor statement.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static ClassConstructor termToStatement(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlString)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final String name            = term.firstMember().getUnquotedString(state);
            final Value  classDefinition = Value.createFromTerm(state, term.lastMember());
            if (classDefinition instanceof SetlClass) {
                return new ClassConstructor(name, (SetlClass) classDefinition);
            } else {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == ClassConstructor.class) {
            ClassConstructor otr = (ClassConstructor) other;
            final int cmp = name.compareTo(otr.name);
            if (cmp != 0) {
                return cmp;
            }
            return classDefinition.compareTo(otr.classDefinition);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(ClassConstructor.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == ClassConstructor.class) {
            ClassConstructor otr = (ClassConstructor) obj;
            return name.equals(otr.name) && classDefinition.equals(otr.classDefinition);
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + name.hashCode();
        hash = hash * 31 + classDefinition.hashCode();
        return hash;
    }
}

