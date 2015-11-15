package org.randoom.setlx.utilities;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A list of CodeFragments.
 *
 * @param <B> Type included in this list.
 */
public class FragmentList<B extends CodeFragment> implements Iterable<B>, Comparable<FragmentList<B>> {

    /** list of fragments */
    protected ArrayList<B> fragmentList;

    public FragmentList() {
        fragmentList = new ArrayList<B>();
    }

    public FragmentList(int initialCapacity) {
        fragmentList = new ArrayList<B>(initialCapacity);
    }

    public FragmentList(B singleElement) {
        fragmentList = new ArrayList<B>(1);
        fragmentList.add(singleElement);
    }

    public <T extends B> FragmentList(FragmentList<T> elements) {
        fragmentList = new ArrayList<B>(elements.fragmentList);
    }

    public <T extends B> FragmentList(FragmentList<T> firstElements, T element) {
        fragmentList = new ArrayList<B>(firstElements.size() + 1);
        fragmentList.addAll(firstElements.fragmentList);
        fragmentList.add(element);
    }

    public <T extends B> FragmentList(FragmentList<T> firstElements, FragmentList<T> secondElements) {
        fragmentList = new ArrayList<B>(firstElements.size() + secondElements.size());
        fragmentList.addAll(firstElements.fragmentList);
        fragmentList.addAll(secondElements.fragmentList);
    }

    public <T extends B> FragmentList(FragmentList<T> firstElements, FragmentList<T> secondElements, T element) {
        fragmentList = new ArrayList<B>(firstElements.size() + secondElements.size() + 1);
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
        final int size = fragmentList.size();
        int cmp = Integer.valueOf(size).compareTo(other.fragmentList.size());
        if (cmp != 0) {
            return cmp;
        }
        for (int index = 0; index < size; ++index) {
            cmp = fragmentList.get(index).compareTo(other.fragmentList.get(index));
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (this.getClass() == obj.getClass()) {
            final ArrayList<B> otherList = ((FragmentList<B>) obj).fragmentList;
            if (fragmentList.size() == otherList.size()) {
                final Iterator<B> iterFirst  = fragmentList.iterator();
                final Iterator<B> iterSecond = otherList.iterator();
                while (iterFirst.hasNext() && iterSecond.hasNext()) {
                    if ( ! iterFirst.next().equals(iterSecond.next())) {
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
        final int size = fragmentList.size();
        int hash = size;
        if (size > 0) {
            hash = hash * 31 + fragmentList.get(0).hashCode();
            if (size > 1) {
                hash = hash * 31 + fragmentList.get(size -1).hashCode();
            }
        }
        return hash;
    }
}
