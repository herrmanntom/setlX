package org.randoom.setlx.utilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.randoom.setlx.exceptions.SetlException;
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

    private static final long            serialVersionUID = 2189938618356906560L;

    private        final TreeSet<String> keys;

    /**
     * Create a new SetlHashMap.
     */
    public SetlHashMap() {
        super();
        keys = new TreeSet<>();
    }

    /**
     * Create a new SetlHashMap which contains all members from the given SetlHashMap.
     *
     * @param m Map to copy all members from.
     */
    public SetlHashMap(final SetlHashMap<? extends V> m) {
        super(m);
        keys = new TreeSet<>(m.keys);
    }

    /**
     * Create a new SetlHashMap which contains all members from the given Map.
     *
     * @param m Map to copy all members from.
     */
    public SetlHashMap(final Map<String, ? extends V> m) {
        super(m);
        keys = new TreeSet<>(m.keySet());
    }

    @Override
    public V put(final String key, final V value) {
        keys.add(key);
        return super.put(key, value);
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     * These mappings will replace any mappings that this map had for
     * any of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map is null
     */
    public void putAll(final SetlHashMap<? extends V> m) {
        keys.addAll(m.keys);
        super.putAll(m);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends V> m) {
        keys.addAll(m.keySet());
        super.putAll(m);
    }

    @Override
    public V remove(final Object key) {
        if (key.getClass() == String.class) {
            keys.remove(key);
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        keys.clear();
        super.clear();
    }

    /**
     * Appends a string representation of the members of this SetlHashMap separated by
     * semicolons to the given StringBuilder object.
     *
     * @param state Current state of the running setlX program.
     * @param sb    StringBuilder to append to.
     * @param tabs  Number of tabs to use as indentation for statements.
     */
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        final Iterator<String> iter = keys.iterator();
        while (iter.hasNext()) {
            final String key = iter.next();
            sb.append(key);
            sb.append(" := ");
            get(key).appendString(state, sb, tabs);
            if (iter.hasNext()) {
                sb.append("; ");
            }
        }
    }

    /**
     * Add contents of this map as SetlX map to given Term.
     *
     * @param state  Current state of the running setlX program.
     * @param result Term to be added to.
     */
    public void addToTerm(final State state, final Term result) throws SetlException {
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
            final SetlHashMap<Value> result = new SetlHashMap<>();
            for (final Value val : (SetlSet) value) {
                if (val instanceof SetlList) {
                    final SetlList binding = (SetlList) val;
                    if (binding.size() == 2 && binding.firstMember() instanceof SetlString) {
                        result.put(binding.firstMember().getUnquotedString(state), Value.createFromTerm(state, binding.lastMember()));
                        continue;
                    }
                }
                throw new TermConversionException("Malformed member of Term.");
            }
            return result;
        }
        throw new TermConversionException("Malformed member of Term.");
    }

    @Override
    public int compareTo(final SetlHashMap<V> other) {
        if (this == other) {
            return 0;
        }

        int cmp = Integer.valueOf(size()).compareTo(other.size());
        if (cmp != 0) {
            return cmp;
        }

        final Iterator<String> iterFirst  = keys.iterator();
        final Iterator<String> iterSecond = other.keys.iterator();
        while (iterFirst.hasNext() && iterSecond.hasNext()) {
            final String key = iterFirst.next();
            cmp = key.compareTo(iterSecond.next());
            if (cmp != 0) {
                return cmp;
            }
            cmp = get(key).compareTo(other.get(key));
            if (cmp != 0) {
                return cmp;
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
