package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StateImplementation;
import org.randoom.setlx.utilities.VariableScope;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base class for all expressions.
 */
public abstract class Expr extends CodeFragment {

    // collection of reusable replacement values
    private final static HashMap<String, SoftReference<Value>> REPLACEMENTS = new HashMap<String, SoftReference<Value>>();

    // value which is the result of this expression, iff expression is `static' (does not contain variables)
    protected Value replacement = null;

    public Value eval(final State state) throws SetlException {
        try {
            if (state.isDebugStepNextExpr && state.isDebugModeActive && ! state.isDebugPromptActive()) {
                state.setDebugStepNextExpr(false);
                DebugPrompt.prompt(state, this);
            } else if (replacement != null) {
                return replacement.clone();
            }
            return this.evaluate(state);
        } catch (final SetlException se) {
            se.addToTrace("Error in \"" + this + "\":");
            throw se;
        }
    }

    protected abstract Value evaluate(final State state) throws SetlException;

    protected void calculateReplacement(final List<String> unboundVariables) {
        if (replacement == null) {
            try {
                // bubble state which is not connected to anything useful
                final State bubble = new StateImplementation();

                // string representation of this expression
                final String _this = this.toString(bubble);

                synchronized (REPLACEMENTS) {
                    // look up if same expression was already evaluated
                    final SoftReference<Value> result = REPLACEMENTS.get(_this);
                    if (result != null) {
                        replacement = result.get();

                        if (replacement == null) { // reference was cleared up
                            REPLACEMENTS.remove(_this);
                        }
                    }

                    if (replacement == null) { // not found
                        // evaluate in bubble state
                        replacement = evaluate(bubble);
                        REPLACEMENTS.put(_this, new SoftReference<Value>(replacement));
                    }
                }
            } catch (final SetlException se) {
                // ignore error
                replacement = null;

                // add dummy variable to prevent optimization at later point
                unboundVariables.add(Variable.PREVENT_OPTIMIZATION_DUMMY);
            }
        }
    }

    public Value getReplacement() {
        if (replacement != null) {
            return replacement.clone();
        } else {
            return null;
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
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    );

    /* Gather variables and optimize this expression by setting replacement value
       for this expression, if this can be safely done */
    @Override
    public final void collectVariablesAndOptimize(
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        if (replacement != null) {
            // already optimized, no variables are needed during execution
            return;
        }

        final int preBoundSize   = boundVariables.size();
        final int preUnboundSize = unboundVariables.size();
        final int preUsedSize    = usedVariables.size();

        // collect variables in this expression
        collectVariables(boundVariables, unboundVariables, usedVariables);

        // prerequisite for optimization is that not variables are provided for later
        // expressions and that no used variables are unbound in this expression
        if (boundVariables.size() == preBoundSize && unboundVariables.size() == preUnboundSize) {
            // optimize when there where also no variables used at all
            if (usedVariables.size() == preUsedSize) {
                calculateReplacement(unboundVariables);
            }
            // or if all used variables are not prebound
            else {
                final List<String> prebound     = boundVariables.subList(0, preBoundSize);
                final List<String> usedHere     = new ArrayList<String>(usedVariables.subList(preUsedSize, usedVariables.size()));
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
    public final Value assign(final State state, final Value v) throws SetlException {
        assignUncloned(state, v);
        return v.clone();
    }

    /* Sets this expression to the given value
       (only makes sense for variables and id-lists)
       Does not clone v and does not return value for chained assignment */
    public void assignUncloned(final State state, final Value v) throws SetlException {
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
        final State         state,
        final Value         v,
        final VariableScope outerScope
    ) throws SetlException {
        throw new UndefinedOperationException(
            "Error in \"" + this + "\":\n" +
            "This expression can not be used as target for assignments."
        );
    }

    /* string operations */

    @Override
    public abstract void appendString(final State state, final StringBuilder sb, final int tabs);

    /* term operations */

    @Override
    public abstract Value toTerm(final State state);

    /**
     * Generate term representing the code this expression represents, when
     * this expression is quoted ('@').
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toTerm(State)
     *
     * @param state          Current state of the running setlX program.
     * @return               Generated term.
     * @throws SetlException When some error happens.
     */
    public          Value toTermQuoted(final State state) throws SetlException  {
        return toTerm(state);
    }

    /**
     * Precedence level in SetlX-grammar. Manly used for automatic bracket insertion
     * when converting terms to expressions.
     *
     * (See src/java-src/org/randoom/setlx/expressions/termConversionPrecedences.txt)
     *
     * @return Precedence level.
     */
    public abstract int   precedence();
}

