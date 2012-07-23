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

    private final List<Variable> mVars;       // variables which are to be extracted
    private final List<Value>    mVarTerms;   // terms of variables which are to be extracted
    private final Variable       mRest;       // variable for the rest of the list
    private final Value          mRestTerm;   // term of variable for the rest of the list
    private final Block          mStatements; // block to execute after match

    public MatchSplitSetBranch(final List<Variable> vars, final Variable rest, final Block statements){
        mVars       = vars;
        mVarTerms   = new ArrayList<Value>(vars.size());
        for (final Variable var : vars) {
            mVarTerms.add(var.toTerm());
        }
        mRest       = rest;
        mRestTerm   = rest.toTerm();
        mStatements = statements;
    }

    public MatchResult matches(final Value term) throws IncompatibleTypeException {
        if (term instanceof SetlSet) {
            final SetlSet other = (SetlSet) term.clone();
            if (other.size() >= mVars.size()) {
                final MatchResult result = new MatchResult(true);
                for (final Value varTerm : mVarTerms) {
                    final MatchResult subResult = varTerm.matchesTerm(other.firstMember());
                    if (subResult.isMatch()) {
                        result.addBindings(subResult);
                        other.removeFirstMember();
                    } else {
                        return new MatchResult(false);
                    }
                }
                final MatchResult subResult = mRestTerm.matchesTerm(other);
                if (subResult.isMatch()) {
                    result.addBindings(subResult);
                    return result;
                } else {
                    return new MatchResult(false);
                }
            } else {
                return new MatchResult(false);
            }
        } else {
            return new MatchResult(false);
        }
    }

    public Value execute() throws SetlException {
        return mStatements.execute();
    }

    protected Value exec() throws SetlException {
        return execute();
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        sb.append("case {");

        final Iterator<Variable> iter = mVars.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append(" | ");
        mRest.appendString(sb, 0);
        sb.append("} :");
        sb.append(Environment.getEndl());
        mStatements.appendString(sb, tabs + 1);
        sb.append(Environment.getEndl());
    }

    /* term operations */

    public Term toTerm() {
        final Term     result  = new Term(FUNCTIONAL_CHARACTER, 3);

        final SetlList varList = new SetlList(mVars.size());
        for (final Value varTerm: mVarTerms) {
            varList.addMember(varTerm.clone());
        }
        result.addMember(varList);

        result.addMember(mRest.toTerm());
        result.addMember(mStatements.toTerm());

        return result;
    }

    public static MatchSplitListBranch termToBranch(final Term term) throws TermConversionException {
        if (term.size() != 3 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                final SetlList        varList = (SetlList) term.firstMember();
                final List<Variable>  vars    = new ArrayList<Variable>(varList.size());
                for (Value var : varList) {
                    if (var instanceof Term) {
                        vars.add(Variable.termToExpr((Term) var));
                    } else {
                        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
                    }
                }
                      Variable        rest    = null;
                if (term.getMember(2) instanceof Term) {
                    rest = Variable.termToExpr((Term) term.getMember(2));
                } else {
                    throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
                }
                final Block           block   = TermConverter.valueToBlock(term.lastMember());
                return new MatchSplitListBranch(vars, rest, block);
            } catch (SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }
}

