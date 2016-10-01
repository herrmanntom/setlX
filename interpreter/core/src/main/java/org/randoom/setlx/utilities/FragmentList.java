package org.randoom.setlx.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * A list of CodeFragments.
 *
 * @param <B> Type included in this list.
 */
public class FragmentList<B extends CodeFragment> implements Iterable<B>, Comparable<FragmentList<B>> {

    /** list of fragments */
    protected ArrayList<B> fragmentList;

    public FragmentList() {
        fragmentList = new ArrayList<>();
    }

    public FragmentList(int initialCapacity) {
        fragmentList = new ArrayList<>(initialCapacity);
    }

    public FragmentList(B singleElement) {
        fragmentList = new ArrayList<>(1);
        fragmentList.add(singleElement);
    }

    public <T extends B> FragmentList(FragmentList<T> elements) {
        fragmentList = new ArrayList<B>(elements.fragmentList);
    }

    public <T extends B> FragmentList(FragmentList<T> firstElements, T element) {
        fragmentList = new ArrayList<>(firstElements.size() + 1);
        fragmentList.addAll(firstElements.fragmentList);
        fragmentList.add(element);
    }

    public <T extends B> FragmentList(FragmentList<T> firstElements, FragmentList<T> secondElements) {
        fragmentList = new ArrayList<>(firstElements.size() + secondElements.size());
        fragmentList.addAll(firstElements.fragmentList);
        fragmentList.addAll(secondElements.fragmentList);
    }

    public <T extends B> FragmentList(FragmentList<T> firstElements, FragmentList<T> secondElements, T element) {
        fragmentList = new ArrayList<>(firstElements.size() + secondElements.size() + 1);
        fragmentList.addAll(firstElements.fragmentList);
        fragmentList.addAll(secondElements.fragmentList);
        fragmentList.add(element);
    }

    /**
     * @return The number of elements in this list.
     */
    public int size() {
        return fragmentList.size();
    }

    /**
     * @return True if this list contains no elements.
     */
    public boolean isEmpty() {
        return fragmentList.isEmpty();
    }

    @Override
    public Iterator<B> iterator() {
        return fragmentList.iterator();
    }

    public void add(B element) {
        fragmentList.add(element);
    }

    public void addAll(FragmentList<B> fragmentList) {
        for (B b : fragmentList) {
            add(b);
        }
    }

    public B get(int index) {
        return fragmentList.get(index);
    }

    public void set(int index, B fragment) {
        fragmentList.set(index, fragment);
    }

    /**
     * Returns true> if this list contains the specified fragment.
     *
     * @param fragment element whose presence in this list is to be tested
     * @return true if this list contains the specified fragment
     */
    public boolean contains(B fragment) {
        return fragmentList.contains(fragment);
    }

    /**
     * Appends a string representation of this fragment list to the given
     * StringBuilder object. Individual fragements are separated by comma and space
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toString(State)
     *
     * @param state Current state of the running setlX program.
     * @param sb    StringBuilder to append to.
     */
    public void appendString(
            final State state,
            final StringBuilder sb
    ) {
        final Iterator<B> iterator = iterator();
        while (iterator.hasNext()) {
            iterator.next().appendString(state, sb, 0);
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
    }

    @Override
    public final int compareTo(FragmentList<B> other) {
        if (this == other) {
            return 0;
        }
        return compareTo(other, true);
    }

    public final int compareTo(FragmentList<B> other, boolean ordered) {
        if (this == other) {
            return 0;
        }

        final int size = fragmentList.size();
        int cmp = Integer.valueOf(size).compareTo(other.fragmentList.size());
        if (cmp != 0) {
            return cmp;
        }

        final Iterator<B> thisIterator;
        final Iterator<B> otherIterator;
        if (ordered) {
            thisIterator  = fragmentList.iterator();
            otherIterator = other.fragmentList.iterator();
        } else {
            thisIterator = new TreeSet<>(fragmentList).iterator();
            otherIterator = new TreeSet<>(other.fragmentList).iterator();
        }
        while (thisIterator.hasNext() && otherIterator.hasNext()) {
            B first = thisIterator.next();
            B second = otherIterator.next();
            cmp = first.compareTo(second);
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public final boolean equals(final Object obj) {
        return equals(obj, true);
    }

    @SuppressWarnings("unchecked")
    public final boolean equals(final Object obj, boolean ordered) {
        if (this == obj) {
            return true;
        } else if (this.getClass() == obj.getClass()) {
            final ArrayList<B> otherList = ((FragmentList<B>) obj).fragmentList;
            if (fragmentList.size() == otherList.size()) {
                final Iterator<B> thisIterator;
                final Iterator<B> otherIterator;
                if (ordered) {
                    thisIterator  = fragmentList.iterator();
                    otherIterator = otherList.iterator();
                } else {
                    thisIterator = new TreeSet<>(fragmentList).iterator();
                    otherIterator = new TreeSet<>(otherList).iterator();
                }
                while (thisIterator.hasNext() && otherIterator.hasNext()) {
                    B first = thisIterator.next();
                    B second = otherIterator.next();
                    if ( ! first.equals(second)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return hashCode(true);
    }

    public final int hashCode(boolean ordered) {
        final int size = fragmentList.size();
        int hash = size;
        if (size > 0) {
            if (ordered) {
                hash = hash * 31 + fragmentList.get(0).hashCode();
                if (size > 1) {
                    hash = hash * 31 + fragmentList.get(size - 1).hashCode();
                }
            } else {
                TreeSet<B> set = new TreeSet<>(fragmentList);
                hash = hash * 31 + set.first().hashCode();
                if (size > 1) {
                    hash = hash * 31 + set.last().hashCode();
                }
            }
        }
        return hash;
    }
}
