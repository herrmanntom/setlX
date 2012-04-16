package interpreter.statements;

import interpreter.exceptions.TermConversionException;
import interpreter.expressions.Variable;
import interpreter.types.Term;
import interpreter.utilities.Environment;

/*
grammar rule:
statement
    : [...]
    | 'var' variable ';'
    ;

implemented here as:
            ========
              mVar
*/

public class GlobalDefinition extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^globalDefinition";

    private Variable mVar;

    public GlobalDefinition(Variable var) {
        mVar = var;
    }

    public void exec() {
        mVar.makeGlobal();
    }

    /* string operations */

    public String toString(int tabs) {
        return Environment.getLineStart(tabs) + "var " + mVar.toString(tabs) + ";";
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mVar.toTerm());
        return result;
    }

    public static GlobalDefinition termToStatement(Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof Term)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Variable var = Variable.termToExpr((Term) term.firstMember());
            return new GlobalDefinition(var);
        }
    }
}

