package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'match' '(' anyExpr ')' '{' ('case' exprList ':' block | 'case' '[' listOfVariables '|' variable ']' ':' block | 'case' '{' listOfVariables '|' variable '}' ':' block)* ('default' ':' block)? '}'
    ;

implemented here as:
                                                                          ===============     ========         =====
                                                                               mVars           mRest        mStatements
*/

public class MatchSplitSetBranch extends MatchAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^matchSplitSetBranch";

    private List<Variable> mVars;       // variables which are to be extracted
    private Variable       mRest;       // variable for the rest of the list
    private Block          mStatements; // block to execute after match

    public MatchSplitSetBranch(List<Variable> vars, Variable rest, Block statements){
        mVars       = vars;
        mRest       = rest;
        mStatements = statements;
    }

    public MatchResult matches(Value term) throws IncompatibleTypeException {
        if (term instanceof SetlSet) {
            SetlSet other = (SetlSet) term.clone();
            if (other.size() >= mVars.size()) {
                MatchResult result = new MatchResult(true);
                for (Variable var : mVars) {
                    result.addBinding(var.toString(), other.firstMember());
                    other.removeFirstMember();
                }
                result.addBinding(mRest.toString(), other);
                return result;
            } else {
                return new MatchResult(false);
            }
        } else {
            return new MatchResult(false);
        }
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
        result += "case {";

        Iterator<Variable> iter = mVars.iterator();
        while (iter.hasNext()) {
            Variable var = iter.next();
            result += var.toString(tabs);
            if (iter.hasNext()) {
                result += ", ";
            }
        }

        result += " | " + mRest;
        result += "} :" + Environment.getEndl();
        result += mStatements.toString(tabs + 1) + Environment.getEndl();
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term     result   = new Term(FUNCTIONAL_CHARACTER);

        SetlList varList = new SetlList();
        for (Variable var: mVars) {
            varList.addMember(var.toTerm());
        }
        result.addMember(varList);

        result.addMember(mRest.toTerm());
        result.addMember(mStatements.toTerm());

        return result;
    }

    public static MatchSplitListBranch termToBranch(Term term) throws TermConversionException {
        if (term.size() != 3 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                SetlList        varList = (SetlList) term.firstMember();
                List<Variable>  vars    = new ArrayList<Variable>(varList.size());
                for (Value var : varList) {
                    if (var instanceof Term) {
                        vars.add(Variable.termToExpr((Term) var));
                    } else {
                        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
                    }
                }
                Variable        rest    = null;
                if (term.getMember(new Rational(2)) instanceof Term) {
                    rest = Variable.termToExpr((Term) term.getMember(new Rational(2)));
                } else {
                    throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
                }
                Block           block   = TermConverter.valueToBlock(term.lastMember());
                return new MatchSplitListBranch(vars, rest, block);
            } catch (SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }
}

