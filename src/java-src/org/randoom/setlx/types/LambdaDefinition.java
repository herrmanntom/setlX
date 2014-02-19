package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.Return;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a lambda definition.
 *
 * grammar rule:
 * lambdaDefinition
 *     : lambdaParameters    '|->' sum
 *     ;
 *
 * implemented here as:
 *       ----------------          ===
 *    parameters (inherited)       expr
 */
public class LambdaDefinition extends Procedure {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(LambdaDefinition.class);

    private final Expr expr; // expression in the body of the definition; used directly only for toString() and toTerm()

    /**
     * Create new lambda definition.
     *
     * @param parameters List of parameters.
     * @param expr       lambda-expression.
     */
    public LambdaDefinition(final List<ParameterDef> parameters, final Expr expr) {
        super(parameters, new Block(1));
        this.expr = expr;
        statements.add(new Return(expr));
    }
    private LambdaDefinition(
        final List<ParameterDef>     parameters,
        final Block                  statements,
        final HashMap<String, Value> closure,
        final Expr                   expr
    ) {
        super(parameters, statements, closure);
        this.expr = expr;
    }

    @Override
    public LambdaDefinition createCopy() {
        return new LambdaDefinition(parameters, expr);
    }

    @Override
    public LambdaDefinition clone() {
        if (closure != null || object != null) {
            return new LambdaDefinition(parameters, statements, closure, expr);
        } else {
            return this;
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        object = null;
        if (parameters.size() == 1) {
            parameters.get(0).appendString(state, sb, 0);
        } else {
            sb.append("[");
            final Iterator<ParameterDef> iter = parameters.iterator();
            while (iter.hasNext()) {
                iter.next().appendString(state, sb, 0);
                if (iter.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }
        sb.append(" |-> ");
        expr.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        object = null;
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList paramList = new SetlList(parameters.size());
        for (final ParameterDef param: parameters) {
            paramList.addMember(state, param.toTerm(state));
        }
        result.addMember(state, paramList);

        result.addMember(state, expr.toTerm(state));

        return result;
    }

    public static LambdaDefinition termToValue(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList            paramList   = (SetlList) term.firstMember();
            final List<ParameterDef>  parameters  = new ArrayList<>(paramList.size());
            for (final Value v : paramList) {
                parameters.add(ParameterDef.valueToParameterDef(v));
            }
            final Expr                expr        = TermConverter.valueToExpr(term.lastMember());
            return new LambdaDefinition(parameters, expr);
        }
    }

    private final static int initHashCode = LambdaDefinition.class.hashCode();

    @Override
    public int hashCode() {
        object = null;
        return initHashCode + parameters.size();
    }

    /**
     * Get the functional character of this value type used in terms.
     *
     * @return Functional character of this value type.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}

