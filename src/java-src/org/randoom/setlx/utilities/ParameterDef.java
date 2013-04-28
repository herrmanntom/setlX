package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

import java.util.List;

// This class represents a single parameter of a function definition

/*
grammar rule:
procedureParameter
    : 'rw' variable
    |      variable
    ;

implemented here as:
      ==== ========
      mType  mVar
*/

public class ParameterDef extends CodeFragment {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER    = "^parameter";
    private final static String FUNCTIONAL_CHARACTER_RW = "^rwParameter";

    public final static int READ_ONLY   = 0;
    public final static int READ_WRITE  = 1;

    private final Variable mVar;
    private final int      mType;

    public ParameterDef(final Variable var, final int type) {
        mVar  = var;
        mType = type;
    }

    public ParameterDef(final String id, final int type) {
        this(new Variable(id), type);
    }

    public ParameterDef(final Variable var) {
        this(var, READ_ONLY);
    }

    public ParameterDef(final String id) {
        this(id, READ_ONLY);
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        mVar.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    public void assign(final State state, final Value v, final String context) throws SetlException {
        mVar.assign(state, v, context);
    }

    public Value getValue(final State state) throws SetlException {
        return mVar.eval(state);
    }

    public int getType() {
        return mType;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        if (mType == READ_WRITE) {
            sb.append("rw ");
        }
        mVar.appendString(state, sb, 0);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result;
        if (mType == READ_WRITE) {
            result = new Term(FUNCTIONAL_CHARACTER_RW);
        } else {
            result = new Term(FUNCTIONAL_CHARACTER);
        }
        result.addMember(state, mVar.toTerm(state));
        return result;
    }

    public static ParameterDef valueToParameterDef(final Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        }
        final Term   term   = (Term) value;
        final String fc     = term.functionalCharacter().getUnquotedString();
        if (fc.equals(FUNCTIONAL_CHARACTER) && term.size() == 1 && term.firstMember() instanceof Term) {
            final Variable var = Variable.termToExpr((Term) term.firstMember());
            return new ParameterDef(var, READ_ONLY);
        } else if (fc.equals(FUNCTIONAL_CHARACTER_RW) && term.size() == 1 && term.firstMember() instanceof Term) {
            final Variable var = Variable.termToExpr((Term) term.firstMember());
            return new ParameterDef(var, READ_WRITE);
        } else {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        }
    }
}

