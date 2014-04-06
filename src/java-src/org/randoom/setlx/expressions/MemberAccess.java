package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * Expression that accesses a specific member of an object or class.
 *
 * grammar rules:
 * factor
 *     : [..]
 *     | ('(' expr ')' | procedureDefinition | variable) (memberAccess | call)* '!'?
 *     ;
 * memberAccess
 *     :                                                  '.' variable
 *     ;
 *
 * implemented here as:
 *        =============================================       ========
 *                              lhs                            member
 */
public class MemberAccess extends AssignableExpression {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(MemberAccess.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 2100;

    private final Expr     lhs;      // left hand side (Variable, Expr, CollectionAccess, etc)
    private final Variable member;   // member to access
    private final String   memberID; // member to access

    /**
     * Create new MemberAccess expression.
     *
     * @param lhs    Left hand side (Variable, Expr, CollectionAccess, etc)
     * @param member Member to access.
     */
    public MemberAccess(final Expr lhs, final Variable member) {
        this.lhs      = lhs;
        this.member   = member;
        this.memberID = member.getID();
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        final Value lhs = this.lhs.eval(state);
        try {
            return lhs.getObjectMemberUnCloned(state, memberID);
        } catch (final SetlException se) {
            final StringBuilder error = new StringBuilder();
            error.append("Error in \"");
            lhs.appendString(state, error, 0);
            error.append(".");
            error.append(memberID);
            error.append("\":");
            se.addToTrace(error.toString());
            throw se;
        }
    }

    @Override
    /*package*/ Value evaluateUnCloned(final State state) throws SetlException {
        if (lhs instanceof AssignableExpression) {
            final Value lhs = ((AssignableExpression) this.lhs).evaluateUnCloned(state);
            try {
                return lhs.getObjectMemberUnCloned(state, memberID);
            } catch (final SetlException se) {
                final StringBuilder error = new StringBuilder();
                error.append("Error in \"");
                lhs.appendString(state, error, 0);
                error.append(".");
                error.append(memberID);
                error.append("\":");
                se.addToTrace(error.toString());
                throw se;
            }
        } else {
            throw new IncompatibleTypeException(
                "\"" + this + "\" is unusable for list assignment."
            );
        }
    }

    @Override
    protected void collectVariables (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        lhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    @Override
    public void collectVariablesWhenAssigned (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        if (lhs instanceof AssignableExpression) {
            // lhs is read, not bound, so use collectVariablesAndOptimize()
            lhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
    }

    // sets this expression to the given value
    @Override
    public void assignUncloned(final State state, final Value v, final String context) throws SetlException {
        if (this.lhs instanceof AssignableExpression) {
            final Value lhs = ((AssignableExpression) this.lhs).evaluateUnCloned(state);
            lhs.setObjectMember(state, memberID, v, context);
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

    /**
     * Convert a term representing a MemberAccess into such an expression.
     *
     * @param term                     Term to convert.
     * @return                         Resulting MemberAccess expression.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
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

