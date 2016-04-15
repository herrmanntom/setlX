package org.randoom.setlx.utilities;

import org.randoom.setlx.parameters.ParameterList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base class for all immutable CodeFragments.
 */
public abstract class ImmutableCodeFragment extends CodeFragment {

    private final static HashMap<CodeFragment, CodeFragment> UNIFIED_CODE_FRAGMENTS = new HashMap<CodeFragment, CodeFragment>();

    private Integer hashCode = null;

    public final static void clearFragmentCache() {
        synchronized (UNIFIED_CODE_FRAGMENTS) {
            UNIFIED_CODE_FRAGMENTS.clear();
        }
    }

    /**
     * Unify all occurrences of the same code fragment.
     *
     * @param codeFragment CodeFragment to unify
     * @param <CF>         Type of CodeFragment to unify
     * @return             Unified CodeFragment
     */
    @SuppressWarnings("unchecked")
    public final static <CF extends ImmutableCodeFragment> CF unify(CF codeFragment) {
        if (codeFragment == null) {
            return null;
        }
        try {
            CodeFragment preExistingCodeFragment;
            synchronized (UNIFIED_CODE_FRAGMENTS) {
                preExistingCodeFragment = UNIFIED_CODE_FRAGMENTS.get(codeFragment);
            }
            if (preExistingCodeFragment == null) {
                preExistingCodeFragment = codeFragment;
                if (UNIFIED_CODE_FRAGMENTS.size() < 370) {
                    synchronized (UNIFIED_CODE_FRAGMENTS) {
                        UNIFIED_CODE_FRAGMENTS.put(codeFragment, preExistingCodeFragment);
                    }
                }
            }
            return (CF) preExistingCodeFragment; // unchecked: preExistingCodeFragment always is of required type
        } catch (NullPointerException npe) {
            // This may get caused by syntax errors during parsing.
            // E.g. if some code fragment was injected in to the syntax tree as null.
            // So this should be ignored, so that the parser may continue and find additional errors in the input
            return null;
        }
    }

    /**
     * Unify all occurrences of the same code fragments inside given List.
     *
     * @param codeFragmentList CodeFragments to unify
     * @param <CF>             Type of CodeFragment to unify
     * @return                 Unified CodeFragment
     */
    public final static <CF extends ImmutableCodeFragment> List<CF> unify(List<CF> codeFragmentList) {
        if (codeFragmentList == null) {
            return null;
        }
        for (int i = 0; i < codeFragmentList.size(); i++) {
            codeFragmentList.set(i, unify(codeFragmentList.get(i)));
        }
        return codeFragmentList;
    }

    /**
     * Unify all occurrences of the same code fragments inside given List.
     *
     * @param codeFragmentList CodeFragments to unify
     * @param <CF>             Type of CodeFragment to unify
     * @return                 Unified CodeFragment
     */
    public final static <CF extends ImmutableCodeFragment> ArrayList<CF> unify(ArrayList<CF> codeFragmentList) {
        if (codeFragmentList == null) {
            return null;
        }
        return (ArrayList<CF>) unify((List<CF>) codeFragmentList);
    }

    /**
     * Unify all occurrences of the same code fragments inside given List.
     *
     * @param codeFragmentList CodeFragments to unify
     * @param <CF>             Type of CodeFragment to unify
     * @return                 Unified CodeFragment
     */
    public final static <CF extends ImmutableCodeFragment> FragmentList<CF> unify(FragmentList<CF> codeFragmentList) {
        if (codeFragmentList == null) {
            return null;
        }
        for (int i = 0; i < codeFragmentList.size(); i++) {
            codeFragmentList.set(i, unify(codeFragmentList.get(i)));
        }
        return codeFragmentList;
    }

    /**
     * Unify all occurrences of the same code fragments inside given List.
     *
     * @param codeFragmentList CodeFragments to unify
     * @return                 Unified CodeFragment
     */
    public final static ParameterList unify(ParameterList codeFragmentList) {
        if (codeFragmentList == null) {
            return null;
        }
        for (int i = 0; i < codeFragmentList.size(); i++) {
            codeFragmentList.set(i, unify(codeFragmentList.get(i)));
        }
        return codeFragmentList;
    }

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

