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

/*
grammar rule:
classDefinition
    : 'class' ID '(' procedureParameters ')' '{' block ('static' '{' block '}')? '}'
    ;

implemented here as:
              ==  ==================================================================
             name                            classDefinition
*/

public class ClassDefiner extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String   FUNCTIONAL_CHARACTER = "^classDefiner";

    private final String          name;
    private final SetlClass classDefinition;

    public ClassDefiner(final String name, final SetlClass classDefinition) {
        this.name            = name;
        this.classDefinition = classDefinition;
    }

    @Override
    protected ReturnMessage execute(final State state) throws SetlException {
        state.putClassDefinition(name, classDefinition);
        return null;
    }

    /* Gather all bound and unbound variables in this statement and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       Optimize sub-expressions during this process by calling optimizeAndCollectVariables()
       when adding variables from them.
    */
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
        state.getLineStart(sb, tabs);
        classDefinition.appendString(name, state, sb, tabs);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(state, classDefinition.toTerm(state));

        return result;
    }

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

