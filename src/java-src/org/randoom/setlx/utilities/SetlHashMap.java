package org.randoom.setlx.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

/**
 * Hash map, that maps a string to a setlX Value type.
 *
 * Various often used utility methods are provided in this class.
 *
 * @param <V> Value type that is mapped to.
 */
public class SetlHashMap<V extends Value> extends HashMap<String, V> implements Comparable<SetlHashMap<V>> {

    private static final long serialVersionUID = 2189938618356906560L;

    /**
     * Create a new SetlHashMap.
     */
    public SetlHashMap() {
        super();
    }

    /**
     * Create a new SetlHashMap which contains all members from the given Map.
     *
     * @param m Map to copy all members from.
     */
    public SetlHashMap(final Map<String, ? extends V> m) {
        super(m);
    }

    /**
     * Add contents of this map as SetlX map to given Term.
     *
     * @param state  Current state of the running setlX program.
     * @param result Term to be added to.
     */
    public void addToTerm(final State state, final Term result) {
        // list of bindings in this object
        final SetlSet bindings = new SetlSet();
        for (final Map.Entry<String, V> entry : entrySet()) {
            final Value value = entry.getValue();
            if (value != Om.OM) {
                final SetlList binding = new SetlList(2);
                binding.addMember(state, new SetlString(entry.getKey()));
                binding.addMember(state, value.toTerm(state));

                bindings.addMember(state, binding);
            }
        }
        result.addMember(state, bindings);
    }

    /**
     * Convert a SetlX map representing a SetlHashMap into such a map.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    SetlHashMap to convert.
     * @return                         Resulting SetlHashMap.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static SetlHashMap<Value> valueToSetlHashMap(final State state, final Value value) throws TermConversionException {
        if (value instanceof SetlSet) {
            final SetlHashMap<Value> result = new SetlHashMap<Value>();
            for (final Value val : (SetlSet) value) {
                if (val instanceof SetlList) {
                    final SetlList binding = (SetlList) val;
                    if (binding.size() == 2 && binding.firstMember() instanceof SetlString) {
                        result.put(binding.firstMember().getUnquotedString(state), TermConverter.valueTermToValue(state, binding.lastMember()));
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
    @Override
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

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof SetlHashMap<?>) {
            return this.equalTo((SetlHashMap<?>) o);
        } else {
            return false;
        }
    }

    /**
     * Test if two Values are equal.
     * This operation can be much faster as ( compareTo(other) == 0 ).
     *
     * @param other Other value to compare to `this'
     * @return      True if `this' equals `other', false otherwise.
     */
    public boolean equalTo(final SetlHashMap<?> other) {
        if (this == other) {
            return true;
        }
        return this.size() == other.size() &&
               this.entrySet().containsAll(other.entrySet());
    }
}
