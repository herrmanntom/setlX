package org.randoom.setlx.utilities;

/**
 * Base class for all immutable CodeFragments.
 */
public abstract class ImmutableCodeFragment extends CodeFragment {

    private Integer hashCode = null;

    /* comparisons */

    /**
     * Returns a hash code value for the CodeFragment.
     *
     * @return a hash code value for this CodeFragment.
     * @see    java.lang.Object#hashCode()
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

