package interpreter.utilities;

import interpreter.types.Value;

import java.util.HashMap;
import java.util.Map;

public class MatchResult {

    private Map<String, Value>  mVarBindings; // variables to when term matches
    private boolean             mMatches;     // does the term match?

    public MatchResult(boolean matches) {
        mVarBindings    = new HashMap<String, Value>();
        mMatches        = matches;
    }

    public void setMatches(boolean matches) {
        mMatches = matches;
    }

    public boolean isMatch() {
        return mMatches;
    }

    public void addBinding(String id, Value value) {
        mVarBindings.put(id, value);
    }

    public void addBindings(MatchResult otherResult) {
        if (otherResult.mMatches) {
            mVarBindings.putAll(otherResult.mVarBindings);
        }
    }

    public void setAllBindings() {
        for (Map.Entry<String, Value> entry : mVarBindings.entrySet()) {
            VariableScope.putValue(entry.getKey(), entry.getValue().clone());
        }
    }
}

