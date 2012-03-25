package interpreter.utilities;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.expressions.Variable;
import interpreter.types.Term;
import interpreter.types.Value;

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

public class ParameterDef {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER    = "^parameter";
    private final static String FUNCTIONAL_CHARACTER_RW = "^rwParameter";

    public final static int READ_ONLY   = 0;
    public final static int READ_WRITE  = 1;

    private Variable mVar;
    private int      mType;

    public ParameterDef(Variable var, int type) {
        mVar  = var;
        mType = type;
    }

    public ParameterDef(String id, int type) {
        this(new Variable(id), type);
    }

    public ParameterDef(Variable var) {
        this(var, READ_ONLY);
    }

    public ParameterDef(String id) {
        this(id, READ_ONLY);
    }

    public void assign(Value v) throws SetlException {
        mVar.assign(v);
    }

    public Value getValue() throws SetlException {
        return mVar.eval();
    }

    public int getType() {
        return mType;
    }

    /* string operations */

    public String toString() {
        String result = "";
        if (mType == READ_WRITE) {
            result += "rw ";
        }
        result += mVar;
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result;
        if (mType == READ_WRITE) {
            result = new Term(FUNCTIONAL_CHARACTER_RW);
        } else {
            result = new Term(FUNCTIONAL_CHARACTER);
        }
        result.addMember(mVar.toTerm());
        return result;
    }

    public static ParameterDef valueToParameterDef(Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        }
        Term    term    = (Term) value;
        String  fc      = term.functionalCharacter().getUnquotedString();
        if (fc.equals(FUNCTIONAL_CHARACTER) && term.size() == 1 && term.firstMember() instanceof Term) {
            Variable var = Variable.termToExpr((Term) term.firstMember());
            return new ParameterDef(var, READ_ONLY);
        } else if (fc.equals(FUNCTIONAL_CHARACTER_RW) && term.size() == 1 && term.firstMember() instanceof Term) {
            Variable var = Variable.termToExpr((Term) term.firstMember());
            return new ParameterDef(var, READ_WRITE);
        } else {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        }
    }
}

