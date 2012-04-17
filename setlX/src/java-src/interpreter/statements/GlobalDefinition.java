package interpreter.statements;

import interpreter.exceptions.TermConversionException;
import interpreter.expressions.Variable;
import interpreter.types.SetlList;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.util.ArrayList;
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

    private List<Variable> mVars;

    public GlobalDefinition(List<Variable> vars) {
        mVars = vars;
    }

    public void exec() {
        for (Variable var : mVars) {
            var.makeGlobal();
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getLineStart(tabs) + "var ";
        for (int i = 0; i < mVars.size(); i++) {
            if (i > 0) {
                result += ", ";
            }
            result += mVars.get(i).toString(tabs);
        }
        return result + ";";
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);

        SetlList varList = new SetlList();
        for (Variable var : mVars) {
            varList.addMember(var.toTerm());
        }
        result.addMember(varList);

        return result;
    }

    public static GlobalDefinition termToStatement(Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            SetlList        vars    = (SetlList) term.firstMember();
            List<Variable>  varList = new ArrayList<Variable>(vars.size());
            for (Value v : vars) {
                if ( ! (v instanceof Term)) {
                    throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
                }
                varList.add(Variable.termToExpr((Term) v));
            }
            return new GlobalDefinition(varList);
        }
    }
}

