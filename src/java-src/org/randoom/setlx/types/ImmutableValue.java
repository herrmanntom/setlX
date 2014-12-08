package org.randoom.setlx.types;

/**
 * Base class for all immutable Values.
 */
public abstract class ImmutableValue extends Value {

    private Integer hashCode = null;

    @Override
    public boolean isImmutable() {
        return true;
    }

    /* comparisons */

    /**
     * Returns a hash code value for the Value.
     *
     * @return a hash code value for this Value.
     * @see    Object#hashCode()
     */
    public abstract int computeHashCode();

    @Override
    public final int hashCode() {
        if (hashCode == null) {
            hashCode = computeHashCode();
        }
        return hashCode;
    }
}

