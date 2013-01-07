package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
grammar rule:
statement
    : 'var' listOfVariables ';'
    | [...]
    ;

listOfVariables
    : variable (',' variable)*
    ;
*/

public class GlobalDefinition extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^globalDefinition";

    private final List<Variable> mVars;

    public GlobalDefinition(final List<Variable> vars) {
        mVars = vars;
    }

    @Override
    protected ReturnMessage execute(final State state) {
        for (final Variable var : mVars) {
            var.makeGlobal(state);
        }
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
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        // add dummy variable to prevent optimization
        unboundVariables.add(Variable.PREVENT_OPTIMIZATION_DUMMY);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.getLineStart(sb, tabs);
        sb.append("var ");

        final Iterator<Variable> iter = mVars.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append(";");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);

        final SetlList varList = new SetlList(mVars.size());
        for (final Variable var : mVars) {
            varList.addMember(state, var.toTerm(state));
        }
        result.addMember(state, varList);

        return result;
    }

    public static GlobalDefinition termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList          vars    = (SetlList) term.firstMember();
            final List<Variable>    varList = new ArrayList<Variable>(vars.size());
            for (final Value v : vars) {
                if ( ! (v instanceof Term)) {
                    throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
                }
                varList.add(Variable.termToExpr((Term) v));
            }
            return new GlobalDefinition(varList);
        }
    }
}

