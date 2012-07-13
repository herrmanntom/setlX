package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlList;
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
                                          ========     =====
                                           mTerms   mStatements
*/

public class MatchCaseBranch extends MatchAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^matchCaseBranch";

    private final List<Expr>  mExprs;      // expressions which creates terms to match
    private final List<Value> mTerms;      // terms to match
    private final Block       mStatements; // block to execute after match

    public MatchCaseBranch(final List<Expr> exprs, final Block statements){
        this(exprs, new ArrayList<Value>(exprs.size()), statements);
        for (final Expr expr: exprs) {
            mTerms.add(expr.toTerm());
        }
    }

    private MatchCaseBranch(final List<Expr> exprs, final List<Value> terms, final Block statements){
        mExprs      = exprs;
        mTerms      = terms;
        mStatements = statements;
    }

    public MatchResult matches(final Value term) throws IncompatibleTypeException {
        MatchResult last = new MatchResult(false);
        for (final Value v : mTerms) {
            last = v.matchesTerm(term);
            if (last.isMatch()) {
                return last;
            }
        }
        return last;
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
        sb.append("case ");

        final Iterator<Expr> iter = mExprs.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(sb, tabs);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append(":");
        sb.append(Environment.getEndl());
        mStatements.appendString(sb, tabs + 1);
        sb.append(Environment.getEndl());
    }

    /* term operations */

    public Term toTerm() {
        final Term     result   = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList termList = new SetlList(mTerms.size());
        for (final Value v: mTerms) {
            termList.addMember(v.clone());
        }
        result.addMember(termList);

        result.addMember(mStatements.toTerm());

        return result;
    }

    public static MatchCaseBranch termToBranch(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList    termList  = (SetlList) term.firstMember();
            final List<Expr>  exprs     = new ArrayList<Expr>(termList.size());
            final List<Value> terms     = new ArrayList<Value>(termList.size());
            for (final Value v : termList) {
                exprs.add(TermConverter.valueToExpr(v));
                terms.add(v);
            }
            final Block       block     = TermConverter.valueToBlock(term.lastMember());
            return new MatchCaseBranch(exprs, terms, block);
        }
    }
}

