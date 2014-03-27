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

	private SetlVector(final NumberValue[] value) throws IncompatibleTypeException {
		if(value.length > 0) {
			this.value = value;
		} else {
			throw new IncompatibleTypeException("Initialization collection empty.");
		}
	}

	public SetlVector(final State state, final CollectionValue init) throws IncompatibleTypeException {
		// System.err.println("[DEBUG]: new SetlVector begin");
		final int itemCount = init.size();
		if(itemCount > 0) {
			value = new NumberValue[itemCount];
			int currentItem = 0;
			for(Value item : init) {
				if(item instanceof NumberValue) {
					value[currentItem] = ((NumberValue)(item.clone()));
				} else if(item instanceof Term) {
					// TODO implement Term handling
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

	@Override
	public Value getMember(int index) throws SetlException {
		return value[index];
	}

	@Override
	public Value clone() {
		final Value[] result = new NumberValue[value.length];
		for(int i = 0; i <= value.length; i++) {
			result[i] = value[i].clone();
		}
		try {
			return new SetlVector((NumberValue[])result);
		} catch(IncompatibleTypeException ex) {
			// TODO This cannot happen!
			return Om.OM;
		}
	}

	@Override
	public void appendString(State state, StringBuilder sb, int tabs) {
		this.canonical(state, sb);
	}

	@Override
	public int compareTo(Value other) {
		if(other instanceof SetlVector) {
			SetlVector otherVector = (SetlVector)other;
			if(otherVector.size() == this.size()) {
				for(int i = 0; i < this.size(); i++) {
					int itemCompare = this.value[i].compareTo(otherVector.value[i]);
					if(itemCompare != 0) {
						return itemCompare;
					}
				}
				return 0;
			} else {
				return this.size() > otherVector.size() ? 1 : -1;
			}
		} else {
			return 1;
		}
	}

	@Override
	protected int compareToOrdering() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean equalTo(Value other) {
		return this.compareTo(other) == 0;
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
	}

	private SetlList toSetlList(final State state) {
		SetlList container = new SetlList(this.value.length);
		for(NumberValue a : this.value) {
			container.addMember(state, a);
		}
		return container;
	}

	@Override
	public Iterator<Value> iterator() {
		return this.toSetlList(null).iterator();
	}

	@Override
	public Iterator<Value> descendingIterator() {
		return this.toSetlList(null).descendingIterator();
	}

	/*
	 * @Override public Iterator<Value> iterator() { return
	 * Arrays.asList((Value[])value).iterator(); }
	 *
	 * @Override public Iterator<Value> descendingIterator() { final
	 * ListIterator<Value> ascendingIterator =
	 * Arrays.asList((Value[])value).listIterator(); return new
	 * Iterator<Value>() { @Override public boolean hasNext() { return
	 * ascendingIterator.hasPrevious(); }
	 *
	 * @Override public Value next() { return ascendingIterator.previous(); }
	 *
	 * @Override public void remove() { ascendingIterator.remove(); } }; }
	 */
	/**
	 *
	 * @param state
	 * @param element
	 */
	@Override
	public void addMember(State state, Value element) {
		if(element instanceof NumberValue) { // TODO Term
			int newLength = this.value.length + 1;
			this.value = Arrays.copyOf(this.value, newLength);
			this.value[newLength - 1] = (NumberValue)element;
		} else {
			// throw new IncompatibleTypeException("Element to be added to vector is not a number.");
			// TODO cannot throw Exception, because base class doesn't throw exception
		}
	}

	@Override
	public SetlBoolean containsMember(State state, Value element) throws IncompatibleTypeException {
		for(NumberValue nv : this.value) {
			if(nv.equalTo(element)) {
				return SetlBoolean.TRUE;
			}
		}
		return SetlBoolean.FALSE;
	}

	@Override
	public Value firstMember() {
		return this.value[0];
	}

	@Override
	public Value getMember(State state, Value index) throws SetlException {
		if(index.jIntConvertable()) {
			return this.value[index.toJIntValue(state)];
		} else {
			throw new IncompatibleTypeException("Index is not an integer.");
		}
	}

	@Override
	public Value lastMember() {
		return this.value[this.value.length];
	}

	@Override
	public Value maximumMember(State state) throws SetlException {
		NumberValue result = this.value[0];
		for(int i = 1; i < this.value.length; i++) {
			if(this.value[i].compareTo(result) > 0) {
				result = this.value[i];
			}
		}
		return result;
	}

	@Override
	public Value minimumMember(State state) throws SetlException {
		NumberValue result = this.value[0];
		for(int i = 1; i < this.value.length; i++) {
			if(this.value[i].compareTo(result) < 0) {
				result = this.value[i];
			}
		}
		return result;
	}

	public void removeMember(int index) {
		NumberValue[] newValue = new NumberValue[value.length - 1];
		System.arraycopy(value, 0, newValue, 0, index);
		System.arraycopy(value, index + 1, newValue, index, value.length - 1 - index);
		value = newValue;
	}

	@Override
	public void removeMember(Value element) throws IncompatibleTypeException {
		if(element instanceof NumberValue) { // TODO Term
			NumberValue elem = (NumberValue)element;
			List<NumberValue> newValue = new ArrayList<NumberValue>(this.value.length - 1);
			for(NumberValue i : this.value) {
				if(i != element) {
					newValue.add(i);
				}
			}
			// TODO Should it be checked, whether something was removed?
			this.value = (NumberValue[])newValue.toArray();
		} else {
			throw new IncompatibleTypeException("Element to be removed from vector is not a number.");
		}
	}

	@Override
	public Value removeFirstMember() {
		NumberValue[] newValue = new NumberValue[value.length - 1];
		System.arraycopy(value, 1, newValue, 0, value.length - 1);
		this.value = newValue;
		return this;
	}

	@Override
	public Value removeLastMember() {
		NumberValue[] newValue = new NumberValue[value.length - 1];
		System.arraycopy(value, 0, newValue, 0, value.length - 1);
		this.value = newValue;
		return this;
	}

	@Override
	public int size() {
		return this.value.length;
	}

	@Override
	public void canonical(State state, StringBuilder sb) {
		sb.append('<');
		for(NumberValue a : this.value) {
			try {
				sb.append(' ').append(a.toJDoubleValue(state)).append(' ');
			} catch(SetlException ex) {
			}
		}
	}

	@Override
	public MatchResult matchesTerm(State state, Value other) throws SetlException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value product(final State state, final Value multiplier) throws SetlException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value productAssign(final State state, final Value multiplier) throws SetlException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value sum(final State state, final Value summand) throws SetlException {
		if(summand instanceof SetlVector) {
			SetlVector sumd = (SetlVector)summand;
			if(this.size() != sumd.size()) {
				throw new IncompatibleTypeException("Vectors with different number of dimensions cannot be added to one another.");
			}
			NumberValue[] result = new NumberValue[this.size()];
			for(int i = 0; i < this.size(); i++) {
				Value tmp = this.value[i].sum(state, sumd.value[i]);
				if(tmp instanceof NumberValue) { // TODO do I need instanceof Term?
					result[i] = (NumberValue)tmp;
				} else {
					throw new AbortException("Sum doesn't return Number!"); // TODO DEBUG if this happens
				}
			}
			return new SetlVector(result);
		} else {
			throw new IncompatibleTypeException("A sum cannot have a vector parameter and a parameter of another type.");
		}
	}

	@Override
	public Value sumAssign(final State state, final Value summand) throws SetlException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public NumberValue scalarProduct(final State state, final SetlVector B) throws SetlException {
		if(this.size() == B.size()) {
			NumberValue result = SetlDouble.valueOf(0);
			for(int i = 0; i < this.size(); i++) {
				result.sumAssign(state, this.value[i].product(state, B.value[i]));
			}
			return result;
		} else {
			throw new IncompatibleTypeException("Scalar produkt cannot be called with vectors with different number of dimensions.");
		}
	}

	/**
	 * ¿Is this algorithm correct?:
	 *
	 * i j k l
	 * ax ay az at bx by bz bt
	 *
	 * a × b = (ay·bz - at·bz)·i + (az·bt - ax·bt)·j + (at·bx - ay·bx)·k +
	 * (ax·by - az·by)·t
	 *
	 * @param state
	 * @param B
	 * @return
	 * @throws SetlException
	 */
	public SetlVector vectorProduct(final State state, final SetlVector B) throws SetlException {
		if(this.size() == B.size()) {
			NumberValue[] result = new NumberValue[this.size()];

			return new SetlVector(result);
		} else {
			throw new IncompatibleTypeException("Vector produkt cannot be called with vectors with different number of dimensions.");
		}
	}
}
