package org.randoom.setlx.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

public class SetlHashMap<V extends Value> extends HashMap<String, V> {

    private static final long serialVersionUID = 2189938618356906560L;

    public SetlHashMap() {
        super();
    }

    public SetlHashMap(final Map<String, ? extends V> m) {
        super(m);
    }

    public void addToTerm(final State state, final Term result) {
        // list of bindings in this object
        final SetlSet   bindings    = new SetlSet();
        for (final Map.Entry<String, V> entry : entrySet()) {
            final SetlList  binding = new SetlList(2);
            binding.addMember(state, new SetlString(entry.getKey()));
            binding.addMember(state, entry.getValue().toTerm(state));

            bindings.addMember(state, binding);
        }
        result.addMember(state, bindings);
    }

    public static SetlHashMap<Value> valueToSetlHashMap(final Value value) throws TermConversionException {
        if (value instanceof SetlSet) {
            final SetlHashMap<Value> result = new SetlHashMap<>();
            for (final Value val : (SetlSet) value) {
                if (val instanceof SetlList) {
                    final SetlList binding = (SetlList) val;
                    if (binding.size() == 2 && binding.firstMember() instanceof SetlString) {
                        result.put(binding.firstMember().getUnquotedString(), TermConverter.valueTermToValue(binding.lastMember()));
                        continue;
                    }
                }
                throw new TermConversionException("Malformed member of Term.");
            }
            return result;
        }
        throw new TermConversionException("Malformed member of Term.");
    }

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     */
    public int compareTo(final SetlHashMap<V> other) {
        if (this == other) {
            return 0;
        }

        final Integer thisSize  = this.size();
        final Integer otherSize = other.size();
        int cmp = thisSize.compareTo(otherSize);
        if (cmp != 0) {
            return cmp;
        }

        for (final Entry<String, V> entry: this.entrySet()) {
            final V otherValue = other.get(entry.getKey());
            if (otherValue != null) {
                cmp = entry.getValue().compareTo(otherValue);
                if (cmp != 0) {
                    return cmp;
                }
            } else {
                return 1;
            }
        }

        for (final Entry<String, V> entry: other.entrySet()) {
            final V thisValue = this.get(entry.getKey());
            if (thisValue != null) {
                cmp = entry.getValue().compareTo(thisValue);
                if (cmp != 0) {
                    return cmp;
                }
            } else {
                return -1;
            }
        }

        return 0;
    }

    public boolean equalTo(final SetlHashMap<V> other) {
        if (this == other) {
            return true;
        }
        return this.size() == other.size() &&
               this.entrySet().containsAll(other.entrySet());
    }

}
