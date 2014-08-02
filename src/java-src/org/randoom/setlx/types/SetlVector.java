package org.randoom.setlx.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.randoom.setlx.exceptions.AbortException;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;

/**
 * @author Patrick Robinson
 */
public class SetlVector extends IndexedCollectionValue {

	private NumberValue[] value;

	/**
	 * Create vector from raw array
	 *
	 * @param value initialization array
	 * @throws IncompatibleTypeException
	 */
	public SetlVector(final NumberValue[] value) throws IncompatibleTypeException {
		// System.err.println("[DEBUG]: new SetlVector begin");
		if(value.length > 0) {
			this.value = value;
			// System.err.println("[DEBUG]: new SetlVector end");
		} else {
			throw new IncompatibleTypeException("Initialization collection empty.");
		}
	}

	/**
	 * Primary constructor, create vector from collection
	 *
	 * @param state
	 * @param init initialization collection
	 * @throws IncompatibleTypeException
	 */
	public SetlVector(final State state, final CollectionValue init) throws IncompatibleTypeException {
		// System.err.println("[DEBUG]: new SetlVector begin");
		final int itemCount = init.size();
		if(itemCount > 0) {
			value = new NumberValue[itemCount];
			int currentItem = 0;
			for(Value item : init) {
				if(item instanceof NumberValue) {
					value[currentItem] = ((NumberValue)(item.clone()));
				} else {
					throw new IncompatibleTypeException("Item " + (currentItem + 1) + " is not a Number.");
				}
				currentItem++;
			}
			// System.err.println("[DEBUG]: new SetlVector end");
		} else {
			throw new IncompatibleTypeException("Initialization collection empty.");
		}
	}

	/**
	 * Convert matrix (if it has only one row or only one column) to vector
	 *
	 * @param state
	 * @param matrix
	 * @throws SetlException
	 */
	public SetlVector(final State state, final SetlMatrix matrix) throws SetlException {
		// System.err.println("[DEBUG]: new SetlVector begin");
		Jama.Matrix base = matrix.getBase();
		if(base.getColumnDimension() == 1) {
			value = new NumberValue[base.getRowDimension()];
			for(int i = 0; i < base.getRowDimension(); i++) {
				value[i] = SetlDouble.valueOf(base.getArray()[i][0]);
			}
			// System.err.println("[DEBUG]: new SetlVector end");
		} else if(base.getRowDimension() == 1) {
			value = new NumberValue[base.getColumnDimension()];
			for(int i = 0; i < base.getColumnDimension(); i++) {
				value[i] = SetlDouble.valueOf(base.getArray()[0][i]);
			}
			// System.err.println("[DEBUG]: new SetlVector end");
		} else {
			throw new IncompatibleTypeException("Matrix could not be converted to a vector, because it doesn't have just one column or just one row.");
		}
	}

    @Override
    public SetlBoolean isVector() {
        return SetlBoolean.TRUE;
    }

	/**
	 * Get value of dimension `index`
	 *
	 * @param index
	 * @return number
	 * @throws SetlException
	 */
	@Override
	public Value getMember(int index) throws SetlException {
		// System.err.println("[DEBUG]: getMember begin");
		if(index > this.value.length || index < 1) {
			throw new IncompatibleTypeException("[getMember]: Index out of bounds: " + index);
		}
		return this.getValue()[index - 1];
	}

	/**
	 * Create a copy of this vector
	 *
	 * @return vector
	 */
	@Override
	public Value clone() {
		// System.err.println("[DEBUG]: clone begin");
		final Value[] result = new NumberValue[getValue().length];
		// System.err.println("[DEBUG]: clone iter start 0 .. " + getValue().length);
		for(int i = 0; i < getValue().length; i++) {
			// System.err.println("[DEBUG]: clone iter " + i);
			result[i] = getValue()[i].clone();
		}
		// System.err.println("[DEBUG]: clone iter end");
		try {
			// System.err.println("[DEBUG]: clone end");
			return new SetlVector((NumberValue[])result);
		} catch(IncompatibleTypeException ex) {
			// This cannot happen!
			// System.err.println("[DEBUG]: clone exc");
			return Om.OM;
		}
	}

	/**
	 *
	 *
	 * @param state
	 * @param sb
	 * @param tabs
	 */
	@Override
	public void appendString(State state, StringBuilder sb, int tabs) {
		// System.err.println("[DEBUG]: appendString begin");
		this.canonical(state, sb);
		// System.err.println("[DEBUG]: appendString end");
	}

	/**
	 * Compare this to other vector
	 *
	 * @param other vector to compare to
	 * @return 0: equal, 1: this bigger, -1: this smaller
	 */
	@Override
	public int compareTo(Value other) {
		// System.err.println("[DEBUG]: compareTo begin");
		if(other instanceof SetlVector) {
			SetlVector otherVector = (SetlVector)other;
			if(otherVector.size() == this.size()) {
				for(int i = 0; i < this.size(); i++) {
					int itemCompare = this.getValue()[i].compareTo(otherVector.getValue()[i]);
					if(itemCompare != 0) {
						return itemCompare;
					}
				}
				// System.err.println("[DEBUG]: compareTo end");
				return 0;
			} else {
				// System.err.println("[DEBUG]: compareTo end");
				return this.size() > otherVector.size() ? 1 : -1;
			}
		} else {
			// System.err.println("[DEBUG]: compareTo end");
			return 1;
		}
	}

	/**
	 *
	 * @return
	 */
	@Override
    public int compareToOrdering() {
		// System.err.println("[DEBUG]: compareToOrdering begin");
		return 1300;
	}

	/**
	 * Check equality
	 *
	 * @param other vector
	 * @return boolean
	 */
	@Override
	public boolean equalTo(Object other) {
		// System.err.println("[DEBUG]: equalTo begin");
        if (other instanceof Value) {
            return this.compareTo((Value) other) == 0;
        } else {
            return false;
        }
	}

	/**
	 *
	 * @return hash
	 */
	@Override
	public int hashCode() {
		// System.err.println("[DEBUG]: hashCode begin");
		return this.getValue().hashCode();
	}

	/**
	 * Convert vector to list
	 *
	 * @param state
	 * @return list
	 */
	private SetlList toSetlList(final State state) {
		// System.err.println("[DEBUG]: toSetlList begin");
		SetlList container = new SetlList(this.getValue().length);
		for(NumberValue a : this.getValue()) {
			container.addMember(state, a);
		}
		// System.err.println("[DEBUG]: toSetlList end");
		return container;
	}

	/**
	 * Iterator for loops
	 *
	 * @return
	 */
	@Override
	public Iterator<Value> iterator() {
		// System.err.println("[DEBUG]: iterator begin");
		return this.toSetlList(null).iterator();
	}

	/**
	 * Reverse Iterator for loops
	 *
	 * @return
	 */
	@Override
	public Iterator<Value> descendingIterator() {
		// System.err.println("[DEBUG]: descIterator begin");
		return this.toSetlList(null).descendingIterator();
	}

	/*
	 * @Override public Iterator<Value> iterator() { return
	 * Arrays.asList((Value[])value).iterator(); }
	 */
	/*
	 * @Override
	 * public Iterator<Value> descendingIterator() {
	 * final ListIterator<Value> ascendingIterator
	 * = Arrays.asList((Value[])value).listIterator();
	 * return new Iterator<Value>() {
	 * @Override
	 * public boolean hasNext() {
	 * return ascendingIterator.hasPrevious();
	 * }
	 *
	 * @Override
	 * public Value next() {
	 * return ascendingIterator.previous();
	 * }
	 *
	 * @Override
	 * public void remove() {
	 * ascendingIterator.remove();
	 * }
	 * };
	 * }
	 *
	 */
	/**
	 * Add a new dimension to the vector
	 *
	 * @param state
	 * @param element
	 */
	@Override
	public void addMember(State state, Value element) {
		// System.err.println("[DEBUG]: addMember begin");
		if(element instanceof NumberValue) {
			int newLength = this.getValue().length + 1;
			this.value = Arrays.copyOf(this.getValue(), newLength);
			this.value[newLength - 1] = (NumberValue)element;
			// System.err.println("[DEBUG]: addMember end");
		} else {
			// System.err.println("[DEBUG]: addMember exc");
			// throw new IncompatibleTypeException("Element to be added to vector is not a number.");
			// cannot throw Exception, because base class doesn't throw exception
		}
	}

	/**
	 * Search through dimensions for `element`
	 *
	 * @param state
	 * @param element
	 * @return
	 * @throws IncompatibleTypeException
	 */
	@Override
	public SetlBoolean containsMember(State state, Value element) throws IncompatibleTypeException {
		// System.err.println("[DEBUG]: containsMember begin");
		for(NumberValue nv : this.getValue()) {
			if(nv.equalTo(element)) {
				// System.err.println("[DEBUG]: containsMember end true");
				return SetlBoolean.TRUE;
			}
		}
		// System.err.println("[DEBUG]: containsMember end false");
		return SetlBoolean.FALSE;
	}

	/**
	 * Value of first dimension
	 *
	 * @return
	 */
	@Override
	public Value firstMember() {
		// System.err.println("[DEBUG]: firstMember begin");
		return this.getValue()[0];
	}

	/**
	 * Value of `index` dimension
	 *
	 * @param state
	 * @param index
	 * @return
	 * @throws SetlException
	 */
	@Override
	public Value getMember(State state, Value index) throws SetlException {
		// System.err.println("[DEBUG]: getMember begin");
		if(index.jIntConvertable()) {
			// System.err.println("[DEBUG]: getMember end");
			return this.getMember(index.toJIntValue(state));
		} else {
			throw new IncompatibleTypeException("Index is not an integer.");
		}
	}

	/**
	 * Value of last dimension
	 *
	 * @return
	 */
	@Override
	public Value lastMember() {
		// System.err.println("[DEBUG]: lastMember begin");
		return this.getValue()[this.getValue().length];
	}

	/**
	 * Biggest single dimension value
	 *
	 * @param state
	 * @return
	 * @throws SetlException
	 */
	@Override
	public Value maximumMember(State state) throws SetlException {
		// System.err.println("[DEBUG]: maxMember begin");
		NumberValue result = this.getValue()[0];
		for(int i = 1; i < this.getValue().length; i++) {
			if(this.getValue()[i].compareTo(result) > 0) {
				result = this.getValue()[i];
			}
		}
		// System.err.println("[DEBUG]: maxMember end");
		return result;
	}

	/**
	 * Smallest single dimension value
	 *
	 * @param state
	 * @return
	 * @throws SetlException
	 */
	@Override
	public Value minimumMember(State state) throws SetlException {
		// System.err.println("[DEBUG]: minMember begin");
		NumberValue result = this.getValue()[0];
		for(int i = 1; i < this.getValue().length; i++) {
			if(this.getValue()[i].compareTo(result) < 0) {
				result = this.getValue()[i];
			}
		}
		// System.err.println("[DEBUG]: minMember end");
		return result;
	}

	/**
	 * Remove a dimension
	 *
	 * @param index
	 */
	public void removeMember(int index) {
		// System.err.println("[DEBUG]: remMemberIdx begin");
		NumberValue[] newValue = new NumberValue[getValue().length - 1];
		System.arraycopy(getValue(), 0, newValue, 0, index);
		System.arraycopy(getValue(), index + 1, newValue, index, getValue().length - 1 - index);
		value = newValue;
		// System.err.println("[DEBUG]: remMemberIdx end");
	}

	/**
	 * Remove dimension if it equals `element`
	 *
	 * @param element
	 * @throws IncompatibleTypeException
	 */
	@Override
	public void removeMember(State state, Value element) throws IncompatibleTypeException {
		// System.err.println("[DEBUG]: remMemberElem begin");
		if(element instanceof NumberValue) {
			NumberValue elem = (NumberValue)element;
			List<NumberValue> newValue = new ArrayList<NumberValue>(this.getValue().length - 1);
			for(NumberValue i : this.getValue()) {
				if(i != element) {
					newValue.add(i);
				}
			}
			this.value = (NumberValue[])newValue.toArray();
			// System.err.println("[DEBUG]: remMember elem end");
		} else {
			throw new IncompatibleTypeException("Element to be removed from vector is not a number.");
		}
	}

	/**
	 * Remove first dimension
	 *
	 * @return
	 */
	@Override
	public Value removeFirstMember() {
		// System.err.println("[DEBUG]: remFirstMember begin");
		NumberValue[] newValue = new NumberValue[getValue().length - 1];
		System.arraycopy(getValue(), 1, newValue, 0, getValue().length - 1);
		this.value = newValue;
		// System.err.println("[DEBUG]: remFirstMember end");
		return this;
	}

	/**
	 * Remove last dimension
	 *
	 * @return
	 */
	@Override
	public Value removeLastMember() {
		// System.err.println("[DEBUG]: remLastMember begin");
		NumberValue[] newValue = new NumberValue[getValue().length - 1];
		System.arraycopy(getValue(), 0, newValue, 0, getValue().length - 1);
		this.value = newValue;
		// System.err.println("[DEBUG]: remLastMember end");
		return this;
	}

	/**
	 * Gets number of dimensions
	 *
	 * @return
	 */
	@Override
	public int size() {
		// // System.err.println("[DEBUG]: size begin");
		return this.getValue().length;
	}

	/**
	 * Pretty print
	 *
	 * @param state
	 * @param sb
	 */
	@Override
	public void canonical(State state, StringBuilder sb) {
		// System.err.println("[DEBUG]: canonical begin");
		sb.append('<');
		for(NumberValue a : this.getValue()) {
			try {
				sb.append(' ').append(a.toJDoubleValue(state)).append(' ');
			} catch(SetlException ex) {
				// System.err.println("[DEBUG]: canonical exc");
			}
		}
		sb.append('>');
		// System.err.println("[DEBUG]: canonical end");
	}

	/**
	 *
	 *
	 * @param state
	 * @param other
	 * @return
	 * @throws SetlException
	 */
	@Override
	public MatchResult matchesTerm(State state, Value other) throws SetlException {
		if(other == IgnoreDummy.ID) {
			return new MatchResult(true);
		} else if(!(other instanceof SetlVector || other instanceof SetlString)) {
			return new MatchResult(false);
		} else {
			if(other instanceof SetlVector) {
				return new MatchResult(this.equalTo(other));
			} else {
				return new MatchResult(this.toString().equals(other.toString()));
			}
		}
	}

	/**
	 * Scalar product
	 *
	 * @param state
	 * @param multiplier other vector, matrix (will be implicitly converted) or
	 * number
	 * @return
	 * @throws SetlException
	 */
	@Override
	public Value product(final State state, final Value multiplier) throws SetlException {
		// System.err.println("[DEBUG]: product begin");
		if(multiplier instanceof SetlVector) {
			// System.err.println("[DEBUG]: product end Vector");
			return this.scalarProduct(state, (SetlVector)multiplier);
		} else if(multiplier instanceof SetlMatrix) {
			// System.err.println("[DEBUG]: product end Matrix");
			return this.scalarProduct(state, new SetlVector(state, (SetlMatrix)multiplier));
		} else if(multiplier.isNumber() == SetlBoolean.TRUE) {
			NumberValue[] result = new NumberValue[this.size()];
			for(int i = 0; i < this.size(); i++) {
				result[i] = (NumberValue)this.getValue()[i].product(state, multiplier);
			}
			// System.err.println("[DEBUG]: product end Number");
			return new SetlVector(result);
		} else if(multiplier.jDoubleConvertable()) {
			NumberValue[] result = new NumberValue[this.size()];
			for(int i = 0; i < this.size(); i++) {
				result[i] = (NumberValue)this.getValue()[i].product(state, SetlDouble.valueOf(multiplier.toJDoubleValue(state)));
			}
			// System.err.println("[DEBUG]: product end double");
			return new SetlVector(result);
		} else if(multiplier instanceof Term) {
			return ((Term)multiplier).productFlipped(state, this);
		} else {
			// System.err.println("[DEBUG]: product exc");
			throw new IncompatibleTypeException("Given parameter is not of supported type.");
		}
	}

	/**
	 * Sum
	 *
	 * @param state
	 * @param summand other vector, matrix (will be implicitly converted) or
	 * number
	 * @return
	 * @throws SetlException
	 */
	@Override
	public Value sum(final State state, final Value summand) throws SetlException {
		// System.err.println("[DEBUG]: sum begin");
		boolean isVector = summand instanceof SetlVector;
		if(isVector || summand instanceof SetlMatrix) {
			SetlVector sumd = isVector ? (SetlVector)summand : new SetlVector(state, (SetlMatrix)summand);
			if(this.size() != sumd.size()) {
				// System.err.println("[DEBUG]: sum exc vector dim");
				throw new IncompatibleTypeException("Vectors with different number of dimensions cannot be added to one another.");
			}
			NumberValue[] result = new NumberValue[this.size()];
			for(int i = 0; i < this.size(); i++) {
				Value tmp = this.getValue()[i].sum(state, sumd.getValue()[i]);
				if(tmp instanceof NumberValue) {
					result[i] = (NumberValue)tmp;
				} else {
					// System.err.println("[DEBUG]: sum exc vector contains nonnumber");
					throw new AbortException("Sum doesn't return Number!");
				}
			}
			// System.err.println("[DEBUG]: sum end");
			return new SetlVector(result);
		} else if(summand instanceof Term) {
			return ((Term)summand).sumFlipped(state, this);
		} else {
			// System.err.println("[DEBUG]: sum exc");
			throw new IncompatibleTypeException("A sum cannot have a vector parameter and a parameter of another type.");
		}
	}

	/**
	 * Cross product
	 *
	 * @param state
	 * @param exponent other vector or matrix (will be implicitly converted)
	 * @return
	 * @throws SetlException
	 */
	@Override
	public Value power(final State state, final Value exponent) throws SetlException {
		// System.err.println("[DEBUG]: power begin");
		if(exponent instanceof SetlVector) {
			// System.err.println("[DEBUG]: power end vector");
			return this.vectorProduct(state, (SetlVector)exponent);
		} else if(exponent instanceof SetlMatrix) {
			// System.err.println("[DEBUG]: power end matrix");
			return this.vectorProduct(state, new SetlVector(state, (SetlMatrix)exponent));
		} else if(exponent instanceof Term) {
			return ((Term)exponent).powerFlipped(state, this);
		} else {
			// System.err.println("[DEBUG]: power exc");
			throw new IncompatibleTypeException("Incompatible exponent type.");
		}
	}

	/**
	 * Calculates scalar product of two vectors
	 *
	 * @param state
	 * @param B second vector
	 * @return number
	 * @throws SetlException
	 */
	public NumberValue scalarProduct(final State state, final SetlVector B) throws SetlException {
		// System.err.println("[DEBUG]: scalar begin");
		if(this.size() == B.size()) {
			NumberValue result = SetlDouble.valueOf(0);
			for(int i = 0; i < this.size(); i++) {
				result = (NumberValue)result.sum(state, this.getValue()[i].product(state, B.getValue()[i]));
			}
			// System.err.println("[DEBUG]: scalar end");
			return result;
		} else {
			// System.err.println("[DEBUG]: scalar exc");
			throw new IncompatibleTypeException("Scalar produkt cannot be called with vectors with different number of dimensions.");
		}
	}

	/**
	 * Calculates cross/vector product of two vectors
	 *
	 * A x B:
	 * i	j	k	l
	 * ax	ay	az	at
	 * bx	by	bz	bt
	 *
	 * a x b = (ay*bz - at*bz)*i + (az*bt - ax*bt)*j + (at*bx - ay*bx)*k +
	 * (ax*by - az*by)*t
	 *
	 * @param state
	 * @param B second vector
	 * @return vector
	 * @throws SetlException
	 */
	public SetlVector vectorProduct(final State state, final SetlVector B) throws SetlException {
		// System.err.println("[DEBUG]: vecproduct begin");
		if(this.size() == B.size()) {
			NumberValue[] result = new NumberValue[this.size()];
			// System.err.println("[DEBUG]: vecproduct iter begin 0 .. " + this.size());
			for(int i = 0; i < this.size(); i++) {
				// System.err.println("[DEBUG]: vecproduct iter " + i);
				result[i] = (NumberValue)(this.getValue()[loopingIndex(1, i, this.size())].product(state, B.getValue()[loopingIndex(2, i, this.size())]));
				// System.err.println("[DEBUG]: vecproduct iter zrs " + result[i]);
				// System.err.println("[DEBUG]: vecproduct iter add " + (NumberValue)(this.getValue()[loopingIndex(-1, i, this.size())].product(state, B.getValue()[loopingIndex(-2, i, this.size())])).minus(state));
				result[i] = (NumberValue)result[i].sum(state, (NumberValue)(this.getValue()[loopingIndex(-1, i, this.size())].product(state, B.getValue()[loopingIndex(-2, i, this.size())])).minus(state));
				// System.err.println("[DEBUG]: vecproduct iter res " + result[i]);
			}
			// System.err.println("[DEBUG]: vecproduct end");
			return new SetlVector(result);
		} else {
			// System.err.println("[DEBUG]: vecproduct exc");
			throw new IncompatibleTypeException("Vector produkt cannot be called with vectors with different number of dimensions.");
		}
	}

	/**
	 * Convert linear index into ring index, to make sure it fits into its
	 * bounds
	 *
	 * @param diff offset to currentIndex to calculate
	 * @param currentIndex starting point
	 * @param length 0 .. length - 1: allowed index range
	 * @return linear index within bounds
	 */
	private int loopingIndex(int diff, int currentIndex, int length) {
		// // System.err.println("[DEBUG]: loopIdx begin");
		/*
		 * int a = currentIndex + diff;
		 * while(a < 0 || a >= length) {
		 * if(a >= length) {
		 * a -= length;
		 * }
		 * if(a < 0) {
		 * a = length + a;
		 * }
		 * }
		 * // System.err.println("[DEBUG]: loopIdx " + a);
		 * return a;
		 */
		int a = (currentIndex + diff) % length;
		if(a < 0) {
			a = length + a;
		}
		// System.err.println("[DEBUG]: loopIdx " + a);
		return a;
	}

	/**
	 * Get raw base array
	 *
	 * @return the value
	 */
	public NumberValue[] getValue() {
		// // System.err.println("[DEBUG]: getValue begin");
		return this.value;
	}

	/**
	 * Difference of two vectors
	 *
	 * @param state
	 * @param subtrahend second vector or matrix (will be implicitly converted)
	 * @return vector
	 * @throws SetlException
	 */
	@Override
	public Value difference(final State state, final Value subtrahend) throws SetlException {
		// System.err.println("[DEBUG]: difference begin");
		boolean isVector = subtrahend instanceof SetlVector;
		if(isVector || subtrahend instanceof SetlMatrix) {
			SetlVector subd = isVector ? (SetlVector)subtrahend : new SetlVector(state, (SetlMatrix)subtrahend);
			if(this.size() != subd.size()) {
				// System.err.println("[DEBUG]: difference exc vector dim");
				throw new IncompatibleTypeException("Vectors with different number of dimensions cannot be added to one another.");
			}
			NumberValue[] result = new NumberValue[this.size()];
			for(int i = 0; i < this.size(); i++) {
				Value tmp = this.getValue()[i].difference(state, subd.getValue()[i]);
				if(tmp instanceof NumberValue) {
					result[i] = (NumberValue)tmp;
				} else {
					// System.err.println("[DEBUG]: difference exc vector contains nonnumber");
					throw new AbortException("Difference doesn't return Number!");
				}
			}
			// System.err.println("[DEBUG]: difference end");
			return new SetlVector(result);
		} else if(subtrahend instanceof Term) {
			return ((Term)subtrahend).differenceFlipped(state, this);
		} else {
			// System.err.println("[DEBUG]: difference exc");
			throw new IncompatibleTypeException("A difference cannot have a vector parameter and a parameter of another type.");
		}
	}

	/**
	 * Handles indexed access
	 *
	 * @param state
	 * @param args [ number ] : index to access
	 * @return number at index args[0]
	 * @throws SetlException
	 */
	@Override
	public Value collectionAccess(final State state, final List<Value> args) throws SetlException {
		return this.getMember(state, args.get(0)).clone();
	}

	/**
	 * Handles indexed access
	 *
	 * @param state
	 * @param args [ number ] : index to access
	 * @return number at index args[0]
	 * @throws SetlException
	 */
	@Override
	public Value collectionAccessUnCloned(final State state, final List<Value> args) throws SetlException {
		return this.getMember(state, args.get(0));
	}

	/**
	 * Set element at `index` to `v`
	 *
	 * @param state
	 * @param index
	 * @param v
	 * @throws SetlException
	 */
	@Override
	public void setMember(final State state, final Value index, final Value v) throws SetlException {
		if(index.jIntConvertable()) {
			int idx = index.jIntValue();
			if(v.isNumber() == SetlBoolean.TRUE) {
				if(idx > this.value.length || idx < 1) {
					throw new IncompatibleTypeException("[setMember]: Index out of bounds: " + idx);
				}
				this.value[idx - 1] = (NumberValue)v;
			} else {
				throw new IncompatibleTypeException("Argument " + v + " to replace vector dimension " + idx + " is not a number.");
			}
		} else {
			throw new IncompatibleTypeException("Vector field access index must be an integer.");
		}
	}
}
