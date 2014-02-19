package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.types.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Result of a term-match, usually from inside the match-statement.
 */
public class MatchResult {
    private        boolean              matches;       // does the term match?
    private final  Map<String, Value>   varBindings;   // variables to set when term matches

    /**
     * Create a new match result, without any matching variables.
     *
     * @param matches General result of the match.
     */
    public MatchResult(final boolean matches) {
        this.matches        = matches;
        this.varBindings    = new HashMap<>();
    }

    /**
     * Did the operation result in a match?
     *
     * @return General result of the match.
     */
    public boolean isMatch() {
        return matches;
    }

    /**
     * Does this result contain variables to set?
     *
     * @return General result of the match.
     */
    public boolean hasBindings() {
        return varBindings.size() > 0;
    }

    /**
     * Add another matched variable.
     *
     * @param id    Name of the variable that matched.
     * @param value Value to bind to that variable.
     */
    public void addBinding(final String id, final Value value) {
        final Value pre = varBindings.put(id, value);
        if (pre != null && ! pre.equalTo(value)) {
            matches = false;
        }
    }

    /**
     * Add all variables bindings from another match result.
     *
     * @param otherResult Other result to read the variable bindings from.
     */
    public void addBindings(final MatchResult otherResult) {
        for (final Map.Entry<String, Value> entry : otherResult.varBindings.entrySet()) {
            addBinding(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Set all variable bindings into the current scope.
     * @param state                         Current state of the running setlX program.
     * @param context                       Context description of the assignment for trace.
     * @throws IllegalRedefinitionException Thrown in case of redefining a class.
     */
    public void setAllBindings(final State state, final String context) throws IllegalRedefinitionException {
        for (final Map.Entry<String, Value> entry : varBindings.entrySet()) {
            state.putValue(entry.getKey(), entry.getValue().clone(), context);
        }
    }
}

