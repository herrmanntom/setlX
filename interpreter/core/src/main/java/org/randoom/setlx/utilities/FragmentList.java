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

    public int size() {
        return fragmentList.size();
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
