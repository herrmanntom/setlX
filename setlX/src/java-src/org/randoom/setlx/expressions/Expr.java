package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.VariableScope;

import java.util.ArrayList;
import java.util.List;

public abstract class Expr extends CodeFragment {
    // step execution of this expr. MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public static boolean sStepNext = false;

    // value which is the result of this expression, iff expression is `static' (does not contain variables)
    protected Value mReplacement = null;

    public Value eval() throws SetlException {
        try {
            if (sStepNext && Environment.isDebugModeActive() && ! Environment.isDebugPromptActive()) {
                Environment.setDebugStepNextExpr(false);
                DebugPrompt.prompt(this);
            } else if (mReplacement != null) {
                return mReplacement.clone();
            }
            return this.evaluate();
        } catch (SetlException se) {
            se.addToTrace("Error in \"" + this + "\":");
            throw se;
        }
    }

    protected abstract Value evaluate() throws SetlException;

    protected void calculateReplacement(final List<Variable> unboundVariables) {
        if (mReplacement == null) {
            final VariableScope outer = VariableScope.getScope();
            try {
                VariableScope.setBubbleScope();
                mReplacement = evaluate();
            } catch (SetlException se) {
                // ignore error
                mReplacement = null;

                // add dummy variable to prevent optimization at later point
                unboundVariables.add(Variable.PREVENT_OPTIMIZATION_DUMMY);
            } finally {
                VariableScope.setScope(outer);
            }
        }
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    protected abstract void collectVariables (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    );

    /* Gather variables and optimize this expression by setting replacement value
       for this expression, if this can be safely done */
    public final void collectVariablesAndOptimize(
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        final int preBoundSize   = boundVariables.size();
        final int preUnboundSize = unboundVariables.size();
        final int preUsedSize    = usedVariables.size();

        // collect variables in this expression
        collectVariables(boundVariables, unboundVariables, usedVariables);

        if (unboundVariables.size() == preUnboundSize) { // no new unbound vars
            // optimize when there where also no variables used at all
            if (usedVariables.size() == preUsedSize) {
                calculateReplacement(unboundVariables);
            }
            // or if all used variables where also bound in this expression
            else {
                final List<Variable> prebound     = boundVariables.subList(0, preBoundSize);
                final List<Variable> usedHere     = new ArrayList<Variable>(usedVariables.subList(preUsedSize, usedVariables.size()));
                final int            usedHereSize = usedHere.size();

                // check if any prebound variables could have been used
                usedHere.removeAll(prebound);
                if (usedHere.size() == usedHereSize) {
                    // definitely not, therefore safe to optimize
                    calculateReplacement(unboundVariables);
                }
            }
        }
    }

    /* sets this expression to the given value
       (only makes sense for variables and id-lists) */
    public final Value assign(final Value v) throws SetlException {
        assignUncloned(v);
        return v.clone();
    }

    /* Sets this expression to the given value
       (only makes sense for variables and id-lists)
       Does not clone v and does not return value for chained assignment */
    public void assignUncloned(final Value v) throws SetlException {
        throw new UndefinedOperationException(
            "Error in \"" + this + "\":\n" +
            "This expression can not be used as target for assignments."
        );
    }

    /* Similar to assignUncloned(),
       However, also checks if the variable is already defined in scopes up to
       (but EXCLUDING) `outerScope'.
       Returns true and sets `v' if variable is undefined or already equal to `v'.
       Returns false, if variable is defined and different from `v'. */
    public boolean assignUnclonedCheckUpTo(
        final Value v,
        final VariableScope outerScope
    ) throws SetlException {
        throw new UndefinedOperationException(
            "Error in \"" + this + "\":\n" +
            "This expression can not be used as target for assignments."
        );
    }

    /* string operations */

    public abstract void appendString(final StringBuilder sb, final int tabs);

    /* term operations */

    public abstract Value toTerm();

    // toTerm when quoted ('@') expression is evaluated
    public          Value toTermQuoted() throws SetlException  {
        return toTerm();
    }

    // precedence level in SetlX-grammar
    public abstract int   precedence();
}

