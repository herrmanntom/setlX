package org.randoom.setlx.types;

import java.util.ArrayList;
import java.util.Iterator;

import org.randoom.setlx.exceptions.*;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;

/**
 * @author Patrick Robinson
 */
public class SetlVector extends IndexedCollectionValue {

    private ArrayList<Double> vector;

    /**
     * Create vector from raw array
     *
     * @param vector initialization array
     */
    public SetlVector(final ArrayList<Double> vector) {
        this.vector = new ArrayList<>(vector);
    }

    /**
     * Primary constructor, create vector from collection
     *
     * @param state Current state of the running setlX program.
     * @param init  initialization collection.
     * @throws SetlException in case of (user-) error.
     */
    public SetlVector(final State state, final CollectionValue init) throws SetlException {
        final int itemCount = init.size();
        if(itemCount > 0) {
            vector = new ArrayList<>(itemCount);
            for(Value item : init) {
                if(item.jDoubleConvertible()) {
                    vector.add(item.toJDoubleValue(state));
                } else {
                    throw new IncompatibleTypeException("Item " + item + " is not a Number.");
                }
            }
        } else {
            throw new IncompatibleTypeException("Initialization collection empty.");
        }
    }

    /**
     * Get internal representation of this vector.
     *
     * @return internal representation of this vector.
     */
    /*protected*/ ArrayList<Double> getVectorCopy() {
        return new ArrayList<>(vector);
    }

    @Override
    public Value clone() {
        return new SetlVector(new ArrayList<>(vector));
    }

    private static class SetlVectorIterator implements Iterator<Value> {
        private       ArrayList<Double> content;
        private final boolean           descending;
        private       int               position;

        private SetlVectorIterator(final SetlVector setlVector, boolean descending) {
            this.content    = setlVector.vector;
            this.descending = descending;
            if (descending) {
                this.position = content.size() - 1;
            } else {
                this.position = 0;
            }
        }

        @Override
        public boolean hasNext() {
            if (descending) {
                return 0 <= position;
            } else {
                return position < content.size();
            }
        }

        @Override
        public Value next() {
            Value next;
            try {
                next = SetlDouble.valueOf(content.get(position));
            } catch (UndefinedOperationException e) {
                next = Om.OM;
            }
            if (descending) {
                position--;
            } else {
                position++;
            }
            return next;
        }

        @Override
        public void remove() {
            if (descending) {
                content.remove(position + 1);
            } else {
                content.remove(--position);
            }
        }
    }

    @Override
    public Iterator<Value> iterator() {
        return new SetlVectorIterator(this, false);
    }

    @Override
    public Iterator<Value> descendingIterator() {
        return new SetlVectorIterator(this, true);
    }

    /* type checks (sort of Boolean operation) */

    @Override
    public SetlBoolean isVector() {
        return SetlBoolean.TRUE;
    }

    /* arithmetic operations */

    @Override
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        boolean isVector = subtrahend instanceof SetlVector;
        if(isVector || subtrahend instanceof SetlMatrix) {
            SetlVector subd = isVector ? (SetlVector)subtrahend : ((SetlMatrix)subtrahend).toVector();
            if(this.size() != subd.size()) {
                throw new IncompatibleTypeException("Vectors with different number of dimensions cannot be added to one another.");
            }
            ArrayList<Double> result = new ArrayList<>(this.size());
            for(int i = 0; i < this.size(); i++) {
                result.add(vector.get(i) - subd.vector.get(i));
            }
            return new SetlVector(result);
        } else if(subtrahend instanceof Term) {
            return ((Term)subtrahend).differenceFlipped(state, this);
        } else {
            throw new IncompatibleTypeException("A difference cannot have a vector parameter and a parameter of another type.");
        }
    }

    @Override
    public Value product(final State state, final Value multiplier) throws SetlException {
        if (multiplier instanceof SetlVector) {
            return this.scalarProduct((SetlVector) multiplier);
        } else if (multiplier instanceof SetlMatrix) {
            return this.scalarProduct(((SetlMatrix) multiplier).toVector());
        } else if (multiplier.jDoubleConvertible()) {
            double doubleValue = multiplier.toJDoubleValue(state);
            ArrayList<Double> result = new ArrayList<>(this.vector.size());
            for (int i = 0; i < this.size(); i++) {
                result.add(vector.get(i) * doubleValue);
            }
            return new SetlVector(result);
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException("Given parameter is not of supported type.");
        }
    }

    @Override
    public Value quotient(final State state, final Value multiplier) throws SetlException {
        if (multiplier.jDoubleConvertible()) {
            double doubleValue = multiplier.toJDoubleValue(state);
            ArrayList<Double> result = new ArrayList<>(this.vector.size());
            for (int i = 0; i < this.size(); i++) {
                result.add(vector.get(i) / doubleValue);
            }
            return new SetlVector(result);
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).quotientFlipped(state, this);
        } else {
            throw new IncompatibleTypeException("Given parameter is not of supported type.");
        }
    }

    @Override
    public Value sum(final State state, final Value summand) throws SetlException {
        boolean isVector = summand instanceof SetlVector;
        if(isVector || summand instanceof SetlMatrix) {
            SetlVector sumd = isVector ? (SetlVector) summand : ((SetlMatrix)summand).toVector();
            if(this.size() != sumd.size()) {
                throw new IncompatibleTypeException("Vectors with different number of dimensions cannot be added to one another.");
            }
            ArrayList<Double> result = new ArrayList<>(this.size());
            for(int i = 0; i < this.size(); i++) {
                result.add(vector.get(i) + sumd.vector.get(i));
            }
            return new SetlVector(result);
        } else if(summand instanceof Term) {
            return ((Term)summand).sumFlipped(state, this);
        } else {
            throw new IncompatibleTypeException("A sum cannot have a vector parameter and a parameter of another type.");
        }
    }

    @Override
    public Value cartesianProduct(State state, Value other) throws SetlException {
        SetlVector otherVector = null;
        if(other instanceof SetlMatrix) {
            otherVector = new SetlVector(state, (SetlMatrix) other);
        } else if (other instanceof SetlVector) {
            otherVector = (SetlVector) other;
        } else if(other instanceof Term) {
            return ((Term)other).powerFlipped(state, this);
        }
        if (otherVector != null) {
            return this.vectorProduct(otherVector);
        } else {
            throw new IncompatibleTypeException("Incompatible exponent type.");
        }
    }

    /* operations on collection values (Lists/Tuples, Sets [, Strings]) */

    @Override
    public void addMember(State state, Value element) throws SetlException {
        if(element.jDoubleConvertible()) {
            this.vector.add(element.toJDoubleValue(state));
        } else {
            throw new IncompatibleTypeException("Element to be added to vector is not a number.");
        }
    }

    @Override
    public SetlBoolean containsMember(State state, Value element) {
        if (element.jDoubleConvertible()) {
            try {
                double needle = element.toJDoubleValue(state);
                for (Double v : vector) {
                    if (Double.compare(v, needle) == 0) {
                        return SetlBoolean.TRUE;
                    }
                }
            } catch (SetlException se) {
                // impossible
            }
        }
        return SetlBoolean.FALSE;
    }

    @Override
    public Value firstMember() {
        if (vector.size() > 0) {
            return getMember(1);
        }
        return Om.OM;
    }

    @Override
    public Value getMember(int index) {
        if(index > 0 && index <= vector.size()) {
            try {
                return SetlDouble.valueOf(vector.get(index - 1));
            } catch (UndefinedOperationException e) {
                // fall trough
            }
        }
        return Om.OM;
    }

    @Override
    public Value getMember(State state, Value index) throws SetlException {
        if(index.jIntConvertible()) {
            return getMember(index.toJIntValue(state));
        } else {
            throw new IncompatibleTypeException("Index is not an integer.");
        }
    }

    @Override
    public Value getMembers(final State state, final Value low, final Value high) throws SetlException {
        // TODO unify with SetlList
        final int vectorSize = vector.size();
        final int lowFromStart;
        final int highFromStart;
        if (low.isInteger() == SetlBoolean.TRUE) {
            final int lowInt = low.jIntValue();
            if (lowInt == 0) {
                throw new NumberToLargeException(
                        "Lower bound '" + low.toString(state) + "' is invalid."
                );
            } else if (lowInt > 0) {
                lowFromStart = lowInt;
            } else /* if (lowInt < 0) */ {
                // negative index counts from end of the vector - convert it to actual index
                lowFromStart = vectorSize + lowInt + 1;
            }
        } else {
            throw new IncompatibleTypeException(
                    "Lower bound '" + low.toString(state) + "' is not a integer."
            );
        }
        if (high.isInteger() == SetlBoolean.TRUE) {
            final int highInt = high.jIntValue();
            if (highInt >= 0) {
                highFromStart = highInt;
            } else /* if (highInt < 0) */ {
                // negative index counts from end of the string - convert it to actual index
                highFromStart = vectorSize + highInt + 1;
            }
        } else {
            throw new IncompatibleTypeException(
                    "Upper bound '" + high.toString(state) + "' is not a integer."
            );
        }

        int size = highFromStart - (lowFromStart - 1);
        if (size < 0 || lowFromStart < 1 || highFromStart < 1 || lowFromStart > vectorSize) {
            size = 0;
        }
        final ArrayList<Double> result = new ArrayList<>(size);
        // in java the index is one lower
        for (int i = lowFromStart - 1; size > 0 && i < highFromStart && i < vectorSize; ++i) {
            result.add(vector.get(i));
        }
        return new SetlVector(result);
    }

    @Override
    public Value lastMember() {
        if (vector.size() > 0) {
            return getMember(vector.size());
        }
        return Om.OM;
    }

    @Override
    public Value maximumMember(State state) throws UndefinedOperationException {
        if (vector.size() > 0) {
            double result = vector.get(0);
            for (double v : vector) {
                if (v > result) {
                    result = v;
                }
            }
            return SetlDouble.valueOf(result);
        }
        return Om.OM;
    }

    @Override
    public Value minimumMember(State state) throws UndefinedOperationException {
        if (vector.size() > 0) {
            double result = vector.get(0);
            for (double v : vector) {
                if (v < result) {
                    result = v;
                }
            }
            return SetlDouble.valueOf(result);
        }
        return Om.OM;
    }

    @Override
    public Value removeFirstMember() throws UndefinedOperationException {
        if (vector.size() > 0) {
            return SetlDouble.valueOf(removeMember(1));
        }
        return Om.OM;
    }

    @Override
    public Value removeLastMember() throws UndefinedOperationException {
        if (vector.size() > 0) {
            return SetlDouble.valueOf(removeMember(vector.size()));
        }
        return Om.OM;
    }

    private double removeMember(int index) {
        if (index > 0 && index < vector.size()) {
            return vector.remove(index - 1);
        } else {
            return Double.NaN;
        }
    }

    @Override
    public void removeMember(State state, Value element) throws IncompatibleTypeException {
        if (element.jDoubleConvertible()) {
            try {
                double needle = element.toJDoubleValue(state);
                for (int i = 0; i < vector.size(); i++) {
                    if (Double.compare(vector.get(i), needle) == 0) {
                        removeMember(i + 1);
                        break;
                    }
                }
            } catch (SetlException se) {
                // impossible
            }
        } else {
            throw new IncompatibleTypeException("Element to be removed from vector is not a number.");
        }
    }

    @Override
    public void setMember(final State state, final Value index, final Value value) throws SetlException {
        if(index.jIntConvertible()) {
            setMember(state, index.jIntValue(), value);
        } else {
            throw new IncompatibleTypeException("Vector field access index must be an integer.");
        }
    }

    @Override
    public void setMember(final State state, int index, final Value value) throws SetlException {
        if(value.jDoubleConvertible()) {
            if(index > vector.size() || index < 1) {
                throw new IncompatibleTypeException("[setMember]: Index out of bounds: " + index);
            }
            vector.set(index - 1, value.toJDoubleValue(state));
        } else {
            throw new IncompatibleTypeException("Argument " + value + " to replace vector dimension " + index + " is not a number.");
        }
    }

    @Override
    public int size() {
        return vector.size();
    }

    /* special vector functions */

    private SetlDouble scalarProduct(final SetlVector other) throws UndefinedOperationException {
        if(this.size() == other.size()) {
            double result = 0.0;
            for(int i = 0; i < this.size(); i++) {
                result += vector.get(i) * other.vector.get(i);
            }
            return SetlDouble.valueOf(result);
        } else {
            throw new UndefinedOperationException("Scalar product cannot be called with vectors with different number of dimensions.");
        }
    }

    private SetlVector vectorProduct(final SetlVector other) throws UndefinedOperationException {
        if(this.size() != 3 || other.size() != 3) {
            throw new UndefinedOperationException("Vector product is only defined for 3 dimensional vectors.");
        }
        ArrayList<Double> result = new ArrayList<>(3);
        result.add(vector.get(1) * other.vector.get(2) - vector.get(2) * other.vector.get(1));
        result.add(vector.get(2) * other.vector.get(0) - vector.get(0) * other.vector.get(2));
        result.add(vector.get(0) * other.vector.get(1) - vector.get(1) * other.vector.get(0));
        return new SetlVector(result);
    }

    /* string and char operations */


    @Override
    public void appendString(State state, StringBuilder sb, int tabs) {
        this.canonical(state, sb);
    }

    @Override
    public void canonical(State state, StringBuilder sb) {
        sb.append("<<");
        Iterator<Double> iter = vector.iterator();
        while (iter.hasNext()) {
            SetlDouble.printDouble(state, sb, iter.next());
            if (iter.hasNext()) {
                sb.append(" ");
            }
        }
        sb.append(">>");
    }

    /* term operations */

    @Override
    public MatchResult matchesTerm(State state, Value other) throws SetlException {
        if (other == IgnoreDummy.ID || this.equalTo(other)) {
            return new MatchResult(true);
        } else {
            return new MatchResult(false);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == SetlVector.class) {
            final ArrayList<Double> otherVector = ((SetlVector) other).vector;
            if (vector == otherVector) {
                return 0; // clone
            }
            final Iterator<Double> iterFirst  = vector.iterator();
            final Iterator<Double> iterSecond = otherVector.iterator();
            while (iterFirst.hasNext() && iterSecond.hasNext()) {
                final int cmp = iterFirst.next().compareTo(iterSecond.next());
                if (cmp != 0) {
                    return cmp;
                }
            }
            if (iterFirst.hasNext()) {
                return 1;
            }
            if (iterSecond.hasNext()) {
                return -1;
            }
            return 0;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(SetlVector.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(Object other) {
        if (this == other) {
            return true;
        } else if (other.getClass() == SetlVector.class) {
            final ArrayList<Double> otherVector = ((SetlVector) other).vector;
            if (vector == otherVector) {
                return true; // clone
            } else if (vector.size() == otherVector.size()) {
                final Iterator<Double> iterFirst  = vector.iterator();
                final Iterator<Double> iterSecond = otherVector.iterator();
                while (iterFirst.hasNext() && iterSecond.hasNext()) {
                    if ( ! iterFirst.next().equals(iterSecond.next())) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int size = size();
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + size;
        if (size >= 1) {
            hash = hash * 31 + vector.get(0).hashCode();
            if (size >= 2) {
                hash = hash * 31 + vector.get(size-1).hashCode();
            }
        }
        return hash;
    }
}

