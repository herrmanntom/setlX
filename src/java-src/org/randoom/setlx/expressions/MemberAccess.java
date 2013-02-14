package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rules:
factor
    : [..]
    | ('(' expr ')' | procedureDefinition | variable) (memberAccess | call)* '!'?
    ;
memberAccess
    :                                                  '.' variable
    ;

implemented here as:
       =============================================       ========
                             mLhs                          mMember
*/

public class MemberAccess extends AssignableExpression {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^memberAccess";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 2100;

    private final Expr     lhs;      // left hand side (Variable, Expr, CollectionAccess, etc)
    private final Variable member;   // member to access
    private final String   memberID; // member to access

    public MemberAccess(final Expr lhs, final Variable member) {
        this.lhs      = lhs;
        this.member   = member;
        this.memberID = member.getID();
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        return lhs.eval(state).getObjectMemberUnCloned(state, memberID);
    }

    @Override
    /*package*/ Value evaluateUnCloned(final State state) throws SetlException {
        if (this.lhs instanceof AssignableExpression) {
            final Value lhs = ((AssignableExpression) this.lhs).evaluateUnCloned(state);
            return lhs.getObjectMemberUnCloned(state, memberID);
        } else {
            throw new IncompatibleTypeException(
                "\"" + this + "\" is unusable for list assignment."
            );
        }
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    @Override
    protected void collectVariables (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        lhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    // sets this expression to the given value
    @Override
    public void assignUncloned(final State state, final Value v) throws SetlException {
        if (this.lhs instanceof AssignableExpression) {
            final Value lhs = ((AssignableExpression) this.lhs).evaluateUnCloned(state);
            lhs.setObjectMember(state, memberID, v);
        } else {
            throw new IncompatibleTypeException(
                "Left-hand-side of \"" + this + " := " + v + "\" is unusable for member assignment."
            );
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        lhs.appendString(state, sb, tabs);
        sb.append(".");
        sb.append(memberID);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, lhs.toTerm(state));
        result.addMember(state, member.toTerm(state));
        return result;
    }

    public static MemberAccess termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs    = TermConverter.valueToExpr(term.firstMember());
            final Expr member = TermConverter.valueToExpr(term.lastMember());
            if (member instanceof Variable) {
                return new MemberAccess(lhs, (Variable) member);
            } else {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

