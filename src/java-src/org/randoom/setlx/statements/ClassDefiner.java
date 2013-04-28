package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlClass;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * A wrapper statement for SetlX class definitions.
 *
 * grammar rule:
 * classDefinition
 *     : 'class' ID '(' procedureParameters ')' '{' block ('static' '{' block '}')? '}'
 *     ;
 *
 * implemented here as:
 *               ==  ==================================================================
 *              name                            classDefinition
 */
public class ClassDefiner extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(ClassDefiner.class);

    private final String    name;
    private final SetlClass classDefinition;

    public ClassDefiner(final String name, final SetlClass classDefinition) {
        this.name            = name;
        this.classDefinition = classDefinition;
    }

    @Override
    protected ReturnMessage execute(final State state) throws SetlException {
        state.putClassDefinition(name, classDefinition, FUNCTIONAL_CHARACTER);
        return null;
    }

    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        boundVariables.add(name);
        classDefinition.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        classDefinition.appendString(name, state, sb, tabs);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(state, new SetlString(name));
        result.addMember(state, classDefinition.toTerm(state));

        return result;
    }

    /**
     * Re-generate a ClassDefiner statement from a term.
     *
     * @param  term                    Term to regenerate from.
     * @return                         ClassDefiner statement.
     * @throws TermConversionException In case of error
     */
    public static ClassDefiner termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlString)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final String name            = term.firstMember().getUnquotedString();
            final Value  classDefinition = TermConverter.valueTermToValue(term.lastMember());
            if (classDefinition instanceof SetlClass) {
                return new ClassDefiner(name, (SetlClass) classDefinition);
            } else {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }
}

