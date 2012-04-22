package org.randoom.setlx.utilities;

import org.randoom.setlx.types.Value;

import java.util.HashMap;
import java.util.Map;

public class MatchResult {

    private boolean             mMatches;     // does the term match?
    private Map<String, Value>  mVarBindings; // variables to set when term matches

    public MatchResult(boolean matches) {
        mMatches        = matches;
        mVarBindings    = new HashMap<String, Value>();
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

