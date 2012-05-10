package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.TermConverter;

/*
grammar rule:
statement
    : [...]
    | 'match' '(' anyExpr ')' '{' ( ... )* ('default' ':' block)? '}'
    ;

implemented here as:
                                                          =====
                                                       mStatements
*/

public class MatchDefaultBranch extends MatchAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^matchDefaultBranch";

    private Block   mStatements;

    public MatchDefaultBranch(Block statements) {
        mStatements = statements;
    }

    public MatchResult matches(Value term) {
        return new MatchResult(true);
    }

    public void execute() throws SetlException {
        mStatements.execute();
    }

    public void exec() throws SetlException {
        execute();
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getLineStart(tabs);
        result += "default:" + Environment.getEndl();
        result += mStatements.toString(tabs + 1) + Environment.getEndl();
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mStatements.toTerm());
        return result;
    }

    public static MatchDefaultBranch termToBranch(Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Block block = TermConverter.valueToBlock(term.firstMember());
            return new MatchDefaultBranch(block);
        }
    }
}

