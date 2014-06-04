package org.randoom.setlx.types;

import Jama.EigenvalueDecomposition;
import Jama.SingularValueDecomposition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;

/**
 * @author Patrick Robinson
 */
public class SetlMatrix extends IndexedCollectionValue {

	private Jama.Matrix value;

	/**
	 *
	 * @return internal matrix library base
	 */
	public Jama.Matrix getBase() {
		return this.value;
	}

	/**
	 * Creates a new SetlMatrix from a raw Jama Matrix
	 *
	 * @param v Jama Matrix base
	 */
	public SetlMatrix(Jama.Matrix v) {
		super();
		this.value = v;
	}

	/**
	 * Primary constructor
	 *
	 * @param state
	 * @param init Collection of Numbers to fill the matrix with
	 * @throws SetlException
	 */
	public SetlMatrix(final State state, final CollectionValue init) throws SetlException {
		super();
		final int rowCount = init.size();
		final int columnCount = init.firstMember().size();
		double[][] base = new double[rowCount][columnCount];
		int currentRow = 0;
		for(Value row : init) {
			if(!(row instanceof CollectionValue)) {
				throw new IncompatibleTypeException("Row " + (currentRow + 1) + " is not of collection type.");
			}
			CollectionValue rowAsCollection = (CollectionValue)row;
			if(rowAsCollection.size() < 1) {
				throw new IncompatibleTypeException("Row " + (currentRow + 1) + "is empty.");
			}
			if(rowAsCollection.size() != columnCount) {
				throw new IncompatibleTypeException("Row " + (currentRow + 1) + " does not have the same length as the first row.");
			}
			int currentColumn = 0;
			for(Value cell : rowAsCollection) {
				if(cell.jDoubleConvertable()) {
					base[currentRow][currentColumn] = cell.toJDoubleValue(state);
					currentColumn++;
				} else {
					throw new IncompatibleTypeException("Cell(row " + (currentRow + 1) + " column " + (currentColumn + 1) + ") is not a number.");
				}

			}
			currentRow++;
		}
		value = new Jama.Matrix(base);
	}

	/**
	 * Converts a SetlVector to a SetlMatrix
	 *
	 * @param state
	 * @param vector SetlVector to convert
	 * @throws SetlException
	 */
	public SetlMatrix(final State state, final SetlVector vector) throws SetlException {
		super();
		double[][] base = new double[vector.size()][1];
		for(int i = 0; i < vector.size(); i++) {
			Value elem = vector.getMember(i + 1);
			if(elem.jDoubleConvertable()) {
				base[i][0] = elem.toJDoubleValue(state);
			} else {
				throw new IncompatibleTypeException("Vector could not be converted to a matrix, because its dimension " + i + " value could not be converted to a double.");
			}
		}
		value = new Jama.Matrix(base);
	}

	/**
	 * Clones the matrix
	 *
	 * @return Copy of this matrix
	 * @see org.randoom.setlx.types.Value#clone()
	 */
	@Override
	public Value clone() {
//		System.err.println("[DEBUG]: clone orig " + this.value);
//		System.err.println("[DEBUG]: clone omat " + new SetlMatrix(this.value));
//		System.err.println("[DEBUG]: clone copy " + this.value.copy());
//		System.err.println("[DEBUG]: clone cmat " + new SetlMatrix(this.value.copy()));
		return new SetlMatrix(this.value.copy());
	}

	/**
	 *
	 * @param state
	 * @param sb
	 * @param tabs
	 * @see
	 * org.randoom.setlx.types.Value#appendString(org.randoom.setlx.utilities.State,
	 * java.lang.StringBuilder, int)
	 */
	@Override
	public void appendString(State state, StringBuilder sb, int tabs) {
		this.canonical(state, sb);
	}

	/**
	 * Compare two matrices
	 *
	 * @param other second matrix
	 * @return
	 * @see
	 * org.randoom.setlx.types.Value#compareTo(org.randoom.setlx.types.Value)
	 */
	@Override
	public int compareTo(Value other) {
		return this.equalTo(other) ? 0 : 1;
	}

	/**
	 *
	 * @return
	 * @see org.randoom.setlx.types.Value#compareToOrdering()
	 */
	@Override
	protected int compareToOrdering() {
		return 1350;
	}

	/**
	 * Check equality
	 *
	 * @param other second matrix
	 * @return boolean are both equal
	 * @see org.randoom.setlx.types.Value#equalTo(org.randoom.setlx.types.Value)
	 */
	@Override
	public boolean equalTo(Value other) {
		return other instanceof SetlMatrix && Arrays.deepEquals(this.value.getArray(), ((SetlMatrix)other).value.getArray());
	}

	/**
	 *
	 * @return hash
	 * @see org.randoom.setlx.types.Value#hashCode()
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * Calculate the matrix product or the scalar product
	 *
	 * @param state
	 * @param multiplier matrix, vector (will be implicitly converted to a
	 * matrix) or number
	 * @return result of product
	 * @throws SetlException
	 */
	@Override
	public Value product(final State state, final Value multiplier) throws SetlException {
		boolean isMatrix = multiplier instanceof SetlMatrix;
		if(isMatrix || multiplier instanceof SetlVector) {
			SetlMatrix b = isMatrix ? (SetlMatrix)multiplier : new SetlMatrix(state, (SetlVector)multiplier);
			if(this.value.getColumnDimension() == b.value.getRowDimension()) {
				return new SetlMatrix(this.value.times(b.value));
			} else {
				throw new IncompatibleTypeException("Matrix multiplication is only defined if the number of columns of the first matrix equals the number of rows of the second matrix.");
			}
		} else if(multiplier instanceof NumberValue) {
			NumberValue n = (NumberValue)multiplier;
			return new SetlMatrix(this.value.times(n.toJDoubleValue(state)));
		} else if(multiplier instanceof Term) {
			return ((Term)multiplier).productFlipped(state, this);
		} else {
			throw new IncompatibleTypeException("Multiplier is not a matrix.");
		}
	}

	/**
	 * Calculate the matrix product or the scalar product and assign it to this
	 *
	 * @param state
	 * @param multiplier matrix, vector (will be implicitly converted to a
	 * matrix) or number
	 * @return result of product
	 * @throws SetlException
	 */
	@Override
	public Value productAssign(final State state, final Value multiplier) throws SetlException {
		boolean isMatrix = multiplier instanceof SetlMatrix;
		if(isMatrix || multiplier instanceof SetlVector) {
			SetlMatrix b = isMatrix ? (SetlMatrix)multiplier : new SetlMatrix(state, (SetlVector)multiplier);
			if(this.value.getColumnDimension() == b.value.getRowDimension()) {
				this.value = this.value.times(b.value);
				return this;
			} else {
				throw new IncompatibleTypeException("Matrix multiplication is only defined if the number of columns of the first matrix equals the number of rows of the second matrix.");
			}
		} else if(multiplier instanceof NumberValue) {
			NumberValue n = (NumberValue)multiplier;
			this.value.timesEquals(n.toJDoubleValue(state));
			return this;
		} else if(multiplier instanceof Term) {
			return ((Term)multiplier).productAssign(state, this); // TOCHECK
		} else {
			throw new IncompatibleTypeException("Multiplier is not a matrix.");
		}
	}

	/**
	 * Transpose this matrix
	 *
	 * @return transposed matrix
	 */
	public SetlMatrix transpose() {
		return new SetlMatrix(this.value.transpose());
	}

	/**
	 * Power
	 *
	 * @param state
	 * @param exponent integer
	 * @return
	 * @throws SetlException
	 */
	@Override
	public Value power(final State state, final Value exponent) throws SetlException {
		if(!this.isSquare()) {
			throw new IncompatibleTypeException("Power is only defined on square matrices."); // Same in Octave
		}
		if(exponent.jIntConvertable()) {
			int ex = exponent.toJIntValue(state);
			Jama.Matrix base;
			if(ex < 0) {
				base = this.value.inverse();
				ex = -ex;
			} else {
				if(ex == 0) {
					base = new Jama.Matrix(this.value.getRowDimension(), this.value.getRowDimension());
					for(int i = 0; i < this.value.getRowDimension(); i++) {
						base.set(i, i, 0);
					}
					return new SetlMatrix(base);
				} else {
					base = this.value;
				}
			}
			Jama.Matrix result = base;
			// No mistake, should be one
			for(int i = 1; i < ex; i++) {
				result = result.times(base);
			}
			return new SetlMatrix(result);
		} else {
			throw new IncompatibleTypeException("Power on matrices is only defined for integer exponents.");
		}
	}

	/**
	 * Adds two matrices together
	 *
	 * @param state
	 * @param summand matrix or vector (will be implicitly converted to a
	 * matrix) to add to this
	 * @return
	 * @throws SetlException
	 */
	@Override
	public Value sum(final State state, final Value summand) throws SetlException {
		boolean isMatrix = summand instanceof SetlMatrix;
		if(isMatrix || summand instanceof SetlVector) {
			SetlMatrix b = isMatrix ? (SetlMatrix)summand : new SetlMatrix(state, (SetlVector)summand);
			if(this.value.getColumnDimension() == b.value.getColumnDimension()) {
				if(this.value.getRowDimension() == b.value.getRowDimension()) {
					return new SetlMatrix(this.value.plus(b.value));
				} else {
					throw new IncompatibleTypeException("Summands have different number of rows.");
				}
			} else {
				throw new IncompatibleTypeException("Summands have different number of columns.");
			}
		} else if(summand instanceof Term) {
			return ((Term)summand).sumFlipped(state, this);
		} else {
			throw new IncompatibleTypeException("Summand is not of type Matrix.");
		}
	}

	/**
	 * Adds two matrices together and assigns them to this
	 *
	 * @param state
	 * @param summand matrix or vector (will be implicitly converted to a
	 * matrix) to add to this
	 * @return
	 * @throws SetlException
	 */
	@Override
	public Value sumAssign(final State state, final Value summand) throws SetlException {
		boolean isMatrix = summand instanceof SetlMatrix;
		if(isMatrix || summand instanceof SetlVector) {
			SetlMatrix b = isMatrix ? (SetlMatrix)summand : new SetlMatrix(state, (SetlVector)summand);
			if(this.value.getColumnDimension() == b.value.getColumnDimension()) {
				if(this.value.getRowDimension() == b.value.getRowDimension()) {
					this.value.plusEquals(b.value);
					return this;
				} else {
					throw new IncompatibleTypeException("Summands have different number of rows.");
				}
			} else {
				throw new IncompatibleTypeException("Summands have different number of columns.");
			}
		} else if(summand instanceof Term) {
			Value v = ((Term)summand).sumFlipped(state, this);
			if(v instanceof SetlMatrix) {
				SetlMatrix nv = (SetlMatrix)v;
				this.value = nv.value;
				return this;
			} else {
				throw new IncompatibleTypeException(("Could not assign non matrix value to matrix variable."));
			}
		} else {
			throw new IncompatibleTypeException("Summand is not of type Matrix.");
		}
	}

	/**
	 * Difference
	 *
	 * @param state
	 * @param subtrahend matrix or vector (will be implicitly converted to a
	 * matrix)
	 * @return
	 * @throws SetlException
	 */
	@Override
	public Value difference(final State state, final Value subtrahend) throws SetlException {
		boolean isMatrix = subtrahend instanceof SetlMatrix;
		if(isMatrix || subtrahend instanceof SetlVector) {
			SetlMatrix b = isMatrix ? (SetlMatrix)subtrahend : new SetlMatrix(state, (SetlVector)subtrahend);
			if(this.value.getColumnDimension() == b.value.getColumnDimension()) {
				if(this.value.getRowDimension() == b.value.getRowDimension()) {
					return new SetlMatrix(this.value.minus(b.value));
				} else {
					throw new IncompatibleTypeException("Subtrahend have different number of rows.");
				}
			} else {
				throw new IncompatibleTypeException("Subtrahend have different number of columns.");
			}
		} else if(subtrahend instanceof Term) {
			return ((Term)subtrahend).differenceFlipped(state, this);
		} else {
			throw new IncompatibleTypeException("Subtrahend is not of type Matrix.");
		}
	}

	/**
	 *
	 * @param state
	 * @param subtrahend matrix or vector (will be implicitly converted to a
	 * matrix)
	 * @return
	 * @throws SetlException
	 */
	@Override
	public Value differenceAssign(final State state, final Value subtrahend) throws SetlException {
		boolean isMatrix = subtrahend instanceof SetlMatrix;
		if(isMatrix || subtrahend instanceof SetlVector) {
			SetlMatrix b = isMatrix ? (SetlMatrix)subtrahend : new SetlMatrix(state, (SetlVector)subtrahend);
			if(this.value.getColumnDimension() == b.value.getColumnDimension()) {
				if(this.value.getRowDimension() == b.value.getRowDimension()) {
					this.value.minusEquals(b.value);
					return this;
				} else {
					throw new IncompatibleTypeException("Subtrahend have different number of rows.");
				}
			} else {
				throw new IncompatibleTypeException("Subtrahend have different number of columns.");
			}
		} else if(subtrahend instanceof Term) {
			Value v = ((Term)subtrahend).differenceFlipped(state, this);
			if(v instanceof SetlMatrix) {
				SetlMatrix nv = (SetlMatrix)v;
				this.value = nv.value;
				return this;
			} else {
				throw new IncompatibleTypeException(("Could not assign non matrix value to matrix variable."));
			}
		} else {
			throw new IncompatibleTypeException("Subtrahend is not of type Matrix.");
		}
	}

	/**
	 * Pretty print
	 *
	 * @param state
	 * @param sb
	 */
	@Override
	public void canonical(final State state, final StringBuilder sb) {
		double[][] a = value.getArray();
		sb.append("<");
		for(double[] a1 : a) {
			sb.append(" [");
			for(double a2 : a1) {
				sb.append(" ").append(a2).append(" ");
			}
			sb.append("] ");
		}
		sb.append(">");
	}

	/**
	 * Returns row at index
	 *
	 * @param index which row to return
	 * @return row
	 * @throws SetlException
	 */
	@Override
	public Value getMember(int index) throws SetlException {
		if(index > this.value.getRowDimension() || index < 1) {
			throw new IncompatibleTypeException("Index out of bounds: " + index);
		}
		SetlList container = new SetlList(this.value.getColumnDimension());
		for(double d : this.value.getArray()[index - 1]) {
			container.addMember(null, SetlDouble.valueOf(d));
		}
		return container;
	}

	/**
	 * Convert this matrix into a pure SetlList
	 *
	 * @param state
	 * @return SetlList
	 */
	private SetlList toSetlList(final State state) {
		SetlList container = new SetlList(this.value.getRowDimension());
		for(double[] a : this.value.getArray()) {
			SetlList row = new SetlList(this.value.getColumnDimension());
			for(double b : a) {
				try {
					row.addMember(state, SetlDouble.valueOf(b));
				} catch(UndefinedOperationException ex) {
				}
			}
			container.addMember(state, row);
		}
		return container;
	}

	/**
	 * Iterator for loops
	 *
	 * @return Iterator
	 */
	@Override
	public Iterator<Value> iterator() {
		return this.toSetlList(null).iterator();
	}

	/**
	 * Reverse Iterator for loops
	 *
	 * @return Iterator
	 */
	@Override
	public Iterator<Value> descendingIterator() {
		return this.toSetlList(null).descendingIterator();
	}

	/**
	 * Add a new row or column
	 *
	 * @param state
	 * @param element vector or collection representing new items
	 */
	@Override
	public void addMember(State state, Value element) {
		double[] dElems;
		if(element instanceof SetlVector) {
			NumberValue[] elems = ((SetlVector)element).getValue();
			dElems = new double[elems.length];
			for(int i = 0; i < elems.length; i++) {
				if(elems[i].jDoubleConvertable()) {
					try {
						dElems[i] = elems[i].toJDoubleValue(state);
					} catch(SetlException ex) {
						return;
					}
				} else {
					return;
				}
			}
		} else if(element instanceof CollectionValue) {
			CollectionValue col = (CollectionValue)element;
			dElems = new double[col.size()];
			int idx = 0;
			for(Value v : col) {
				if(v.jDoubleConvertable()) {
					try {
						dElems[idx] = v.toJDoubleValue(state);
					} catch(SetlException ex) {
						return;
					}
				} else {
					return;
				}
				idx++;
			}
		} else {
			return;
		}
		if(dElems.length == this.value.getColumnDimension()) {
			// Vector will be added as a row
			double[][] result = new double[this.value.getRowDimension() + 1][this.value.getColumnDimension()];
			System.arraycopy(this.value.getArray(), 0, result, 0, this.value.getRowDimension());
			result[this.value.getRowDimension()] = dElems;
			this.value = new Jama.Matrix(result);
		} else if(dElems.length == this.value.getRowDimension()) {
			// Vector will be added as a column
			double[][] result = new double[this.value.getRowDimension()][this.value.getColumnDimension() + 1];
			for(int i = 0; i < result.length; i++) {
				System.arraycopy(this.value.getArray()[i], 0, result[i], 0, this.value.getColumnDimension());
				result[i][this.value.getColumnDimension()] = dElems[i];
			}
			this.value = new Jama.Matrix(result);
		}
	}

	private static double epsilon = 1E-5;

	/**
	 * Searches for element
	 *
	 * @param state
	 * @param element
	 * @return Does this matrix contain element?
	 * @throws IncompatibleTypeException
	 */
	@Override
	public SetlBoolean containsMember(State state, Value element) throws IncompatibleTypeException {
		if(element instanceof CollectionValue) {
			CollectionValue c = (CollectionValue)element;
			double[] v = new double[c.size()];
			int i = 0;
			for(Value subelem : c) {
				try {
					v[i] = subelem.toJDoubleValue(state);
				} catch(SetlException ex) {
					throw new IncompatibleTypeException(ex.getMessage());
				}
				i++;
			}
			for(double[] a : this.value.getArray()) {
				if(Arrays.equals(a, v)) {
					return SetlBoolean.TRUE;
				}
			}
			return SetlBoolean.FALSE;
		} else if(element instanceof NumberValue) {
			try {
				double v = ((NumberValue)element).toJDoubleValue(state);
				for(double[] a : this.value.getArray()) {
					for(double b : a) {
						if(Math.abs(b - v) < epsilon) {
							return SetlBoolean.TRUE;
						}
					}
				}
			} catch(SetlException ex) {
				throw new IncompatibleTypeException(ex.getMessage());
			}
		}
		return SetlBoolean.FALSE;
	}

	/**
	 * Get first row
	 *
	 * @return
	 */
	@Override
	public Value firstMember() {
		try {
			return this.getMember(1);
		} catch(SetlException ex) {
			return Om.OM;
		}
	}

	/**
	 * Get row with index
	 *
	 * @param state
	 * @param index
	 * @return list of numbers in row
	 * @throws SetlException
	 */
	@Override
	public Value getMember(State state, Value index) throws SetlException {
		if(index.jIntConvertable()) {
			return this.getMember(index.toJIntValue(state));
		} else {
			throw new IncompatibleTypeException("Given index is not a number.");
		}
	}

	/**
	 * Get last row
	 *
	 * @return list of numbers in last row
	 */
	@Override
	public Value lastMember() {
		try {
			return this.getMember(this.value.getRowDimension());
		} catch(SetlException ex) {
			return Om.OM;
		}
	}

	/**
	 * Finds biggest number in any cell
	 *
	 * @param state
	 * @return number
	 * @throws SetlException
	 */
	@Override
	public Value maximumMember(State state) throws SetlException {
		double momentaryMax = Double.NEGATIVE_INFINITY;
		for(double[] a : this.value.getArray()) {
			for(double b : a) {
				if(b > momentaryMax) {
					momentaryMax = b;
				}
			}
		}
		return SetlDouble.valueOf(momentaryMax);
	}

	/**
	 * Finds smallest number in any cell
	 *
	 * @param state
	 * @return number
	 * @throws SetlException
	 */
	@Override
	public Value minimumMember(State state) throws SetlException {
		double momentaryMin = Double.POSITIVE_INFINITY;
		for(double[] a : this.value.getArray()) {
			for(double b : a) {
				if(b < momentaryMin) {
					momentaryMin = b;
				}
			}
		}
		return SetlDouble.valueOf(momentaryMin);
	}

	/**
	 *
	 *
	 * @param element
	 * @throws IncompatibleTypeException
	 */
	@Override
	public void removeMember(Value element) throws IncompatibleTypeException {
		int index = -1;
		int tmp = 0;
		for(Value row : this.toSetlList(null)) {
			if(row instanceof SetlList && ((SetlList)row).equalTo(element)) {
				index = tmp;
				break;
			}
			tmp++;
		}
		if(index > -1) {
			double[][] newArr = new double[this.value.getRowDimension() - 1][this.value.getColumnDimension()];
			double[][] oldArr = this.value.getArray();
			if(index == 0) {
				System.arraycopy(oldArr, 1, newArr, 0, newArr.length);
			} else if(index == oldArr.length - 1) {
				System.arraycopy(oldArr, 0, newArr, 0, newArr.length);
			} else if(index >= oldArr.length) {
				throw new IncompatibleTypeException("Internal Error in SetlMatrix.removeMemeber");
			} else {
				System.arraycopy(oldArr, 0, newArr, 0, index);
				System.arraycopy(oldArr, index + 1, newArr, index, newArr.length - index);
			}
			this.value = new Jama.Matrix(newArr);
		} else {
			throw new IncompatibleTypeException("Element " + element + " that should be removed isn't part of this matrix: " + this);
		}
	}

	/**
	 * Create copy of this matrix excluding first row
	 *
	 * @return new SetlMatrix
	 */
	@Override
	public Value removeFirstMember() {
		double[][] result = new double[this.value.getRowDimension() - 1][this.value.getColumnDimension()];
		System.arraycopy(this.value.getArray(), 1, result, 0, this.value.getColumnDimension() - 1);
		return new SetlMatrix(new Jama.Matrix(result));
	}

	/**
	 * Create copy of this matrix excluding last row
	 *
	 * @return new SetlMatrix
	 */
	@Override
	public Value removeLastMember() {
		double[][] result = new double[this.value.getRowDimension() - 1][this.value.getColumnDimension()];
		System.arraycopy(this.value.getArray(), 0, result, 0, this.value.getRowDimension() - 1);
		return new SetlMatrix(new Jama.Matrix(result));
	}

	/**
	 * Returns the number of rows
	 *
	 * @return Integer
	 */
	@Override
	public int size() {
		return this.value.getRowDimension();
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
		} else if(!(other instanceof SetlMatrix || other instanceof SetlString)) {
			return new MatchResult(false);
		} else {
			if(other instanceof SetlMatrix) {
				return new MatchResult(this.equalTo(other));
			} else {
				return new MatchResult(this.canonical().equals(other.toString()));
			}
		}
	}

	/**
	 * Calculates all eigen values
	 *
	 * @param state
	 * @return list of numbers
	 * @throws UndefinedOperationException
	 */
	public SetlList eigenValues(State state) throws SetlException {
		if(!this.isSquare()) {
			throw new IncompatibleTypeException("Not a square matrix.");
		}
		EigenvalueDecomposition result = this.value.eig();
		double[][] values = result.getD().getArray();
		SetlList composition = new SetlList(values.length);
		for(int i = 0; i < values.length; i++) {
			composition.addMember(state, SetlDouble.valueOf(values[i][i]));
		}
		return composition;
	}

	/**
	 * Calculates singular value decomposition
	 *
	 * @param state
	 * @return 3-tupel of matrices [U, S, V]
	 */
	public SetlList singularValueDecomposition(State state) {
//		System.err.println("[DEBUG]: svd invoke");
		SingularValueDecomposition result = this.value.svd();
//		System.err.println("[DEBUG]: svd lib call done");
		SetlList container = new SetlList();
//		System.err.println("[DEBUG]: svd list create done: " + container);
//		System.err.println("[DEBUG]: svd u " + new SetlMatrix(result.getU()));
//		System.err.println("[DEBUG]: svd u " + new SetlMatrix(result.getU()).clone());
//		System.err.println("[DEBUG]: svd s " + new SetlMatrix(result.getS()).clone());
//		System.err.println("[DEBUG]: svd v " + new SetlMatrix(result.getV()).clone());
//		container.addMember(state, this);
//		System.err.println("[DEBUG]: svd list add test done: " + container);
		container.addMember(state, new SetlMatrix(result.getU()));
//		System.err.println("[DEBUG]: svd u done: " + container);
		container.addMember(state, new SetlMatrix(result.getS()));
//		System.err.println("[DEBUG]: svd s done: " + container);
		container.addMember(state, new SetlMatrix(result.getV()));
//		System.err.println("[DEBUG]: svd v done: " + container);
		return container;
	}

	/**
	 * Calculate eigen vector matrix
	 *
	 * @return matrix
	 */
	public SetlMatrix eigenVectors() throws IncompatibleTypeException {
		if(!this.isSquare()) {
			throw new IncompatibleTypeException("Not a square matrix.");
		}
		return new SetlMatrix(this.value.eig().getV());
	}

	/**
	 * Is the number of rows equal to the number of columns
	 *
	 * @return boolean
	 */
	public boolean isSquare() {
		return this.value.getColumnDimension() == this.value.getRowDimension();
	}

	/**
	 * Calculate the determinant of this matrix
	 *
	 * @return number
	 * @throws SetlException
	 */
	public SetlDouble determinant() throws SetlException {
		if(this.isSquare()) {
			return SetlDouble.valueOf(this.value.det());
		} else {
			throw new IncompatibleTypeException("Matrix needs to be square.");
		}
	}

	/**
	 * Solve this * X = B
	 *
	 * @param B
	 * @return X
	 */
	public SetlMatrix solve(SetlMatrix B) throws IncompatibleTypeException {
		if(this.value.getRowDimension() != B.value.getRowDimension()) {
			throw new IncompatibleTypeException("Row numbers must be equal to solve A * X = B.");
		}
		return new SetlMatrix(this.value.solve(B.value));
	}

	/**
	 * Maps transpose to the x! operator
	 *
	 * @param state
	 * @return transposed matrix
	 * @throws SetlException
	 */
	@Override
	public Value factorial(final State state) throws SetlException {
		return this.transpose();
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
		if(args.get(0).jIntConvertable()) {
			return this.getMember(args.get(0).toJIntValue(state)).clone();
		} else {
			throw new IncompatibleTypeException("Matrix row access index must be an integer.");
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
	public Value collectionAccessUnCloned(final State state, final List<Value> args) throws SetlException {
		if(args.get(0).jIntConvertable()) {
			return this.getMember(args.get(0).toJIntValue(state));
		} else {
			throw new IncompatibleTypeException("Matrix row access index must be an integer.");
		}
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
			if(idx > this.value.getRowDimension() || idx < 1) {
				throw new IncompatibleTypeException("Index out of bounds: " + idx);
			}
			if(v instanceof CollectionValue) {
				CollectionValue col = (CollectionValue)v;
				if(col.size() != this.value.getColumnDimension()) {
					throw new IncompatibleTypeException("The collection and a row of this matrix have different numbers of elements.");
				}
				List<Double> newRow = new ArrayList<Double>(col.size());
				for(Value elem : col) {
					if(elem.jDoubleConvertable()) {
						newRow.add(elem.jDoubleValue());
					} else {
						throw new IncompatibleTypeException("Matrix row assign: Element " + elem + " of the collection is not a number.");
					}
				}
				for(int i = 0; i < newRow.size(); i++) {
					this.value.set(idx, i, newRow.get(i));
				}
			} else {
				throw new IncompatibleTypeException("Argument " + v + " to replace matrix row " + idx + " is not a collection.");
			}
		} else {
			throw new IncompatibleTypeException("Matrix row access index must be an integer.");
		}
	}
}
