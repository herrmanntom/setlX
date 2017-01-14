package org.randoom.setlx.types;

import Jama.EigenvalueDecomposition;
import Jama.LUDecomposition;
import Jama.Matrix;
import Jama.QRDecomposition;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FixedSingularValueDecomposition;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Patrick Robinson
 */
public class SetlMatrix extends IndexedCollectionValue {

    private Jama.Matrix matrix;

    /**
     * Creates a new SetlMatrix from a raw Jama Matrix
     *
     * @param v Jama Matrix base
     */
    /*package*/ SetlMatrix(Jama.Matrix v) {
        super();
        this.matrix = v;
    }

    /**
     * Create a new matrix.
     *
     * @param state   Current state of the running setlX program.
     * @param vectors List of vectors to fill the matrix with.
     */
    public SetlMatrix(final State state, final List<SetlVector> vectors) {
              String error = null;
        final int    rowCount = vectors.size();
        final int    columnCount = vectors.get(0).size();
        double[][] base = new double[rowCount][columnCount];
        for (int currentRow = 0; currentRow < rowCount; currentRow++) {
            ArrayList<Double> row = vectors.get(currentRow).getVectorCopy();
            if(row.size() < 1) {
                error = "Row " + (currentRow + 1) + "is empty.";
                break;
            }
            if(row.size() != columnCount) {
                error = "Row " + (currentRow + 1) + " does not have the same length as the first row.";
                break;
            }
            for (int currentColumn = 0; currentColumn < columnCount; currentColumn++) {
                base[currentRow][currentColumn] = row.get(currentColumn);
            }
        }

        if (error != null) {
            /* Doing error handling here is futile
             * Instead make outer parsing run, which called this constructor,
             * notice this error and (later) halt.
             */
            state.addToParserErrorCount(1);
            // However we can at least provide the user with some feedback.
            state.writeParserErrLn(
                    "Error(s) while creating matrix from vectors " + vectors + " {\n"
                            + "\t" + error + "\n"
                            + "}"
            );
        }
        matrix = new Jama.Matrix(base);
    }

    /**
     * Create a new matrix.
     *
     * @param state          Current state of the running setlX program.
     * @param init           Collection of Numbers to fill the matrix with
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public SetlMatrix(final State state, final CollectionValue init) throws SetlException {
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
                if(cell.jDoubleConvertible()) {
                    base[currentRow][currentColumn] = cell.toJDoubleValue(state);
                    currentColumn++;
                } else {
                    throw new IncompatibleTypeException("Cell(row " + (currentRow + 1) + " column " + (currentColumn + 1) + ") is not a number.");
                }

            }
            currentRow++;
        }
        matrix = new Jama.Matrix(base);
    }

    /**
     * Converts a SetlVector to a SetlMatrix
     *
     * @param state          Current state of the running setlX program.
     * @param vector         SetlVector to convert
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public SetlMatrix(final State state, final SetlVector vector) throws SetlException {
        super();
        double[][] base = new double[vector.size()][1];
        for(int i = 0; i < vector.size(); i++) {
            Value elem = vector.getMember(i + 1);
            if(elem.jDoubleConvertible()) {
                base[i][0] = elem.toJDoubleValue(state);
            } else {
                throw new IncompatibleTypeException("Vector could not be converted to a matrix, because its dimension " + i + " could not be converted to a double.");
            }
        }
        matrix = new Jama.Matrix(base);
    }

    @Override
    public Value clone() {
        return new SetlMatrix(this.matrix.copy());
    }

    @Override
    public Iterator<Value> iterator() {
        return this.toVectorList().iterator();
    }

    @Override
    public Iterator<Value> descendingIterator() {
        return this.toVectorList().descendingIterator();
    }

    /* type checks (sort of Boolean operation) */

    @Override
    public SetlBoolean isMatrix() {
        return SetlBoolean.TRUE;
    }

    /* type conversions */

    /**
     * Convert matrix (if it has only one row or only one column) to vector
     *
     * @throws IncompatibleTypeException if matrix is malformed.
     * @throws UndefinedOperationException if matrix contains NaN doubles.
     *
     * @return Vector equivalent of this matrix
     */
    public SetlVector toVector() throws IncompatibleTypeException, UndefinedOperationException {
        ArrayList<Double> values;
        if(matrix.getColumnDimension() == 1) {
            values = new ArrayList<>(matrix.getRowDimension());
            for(int i = 0; i < matrix.getRowDimension(); i++) {
                values.add(matrix.getArray()[i][0]);
            }
        } else if(matrix.getRowDimension() == 1) {
            values = new ArrayList<>(matrix.getColumnDimension());
            for(int i = 0; i < matrix.getColumnDimension(); i++) {
                values.add(matrix.getArray()[0][i]);
            }
        } else {
            throw new IncompatibleTypeException("Matrix could not be converted to a vector, because it doesn't have just one column or just one row.");
        }
        return new SetlVector(values);
    }

    /**
     * Convert this matrix into a SetlList of SetlVectors
     *
     * @return SetlList of SetlVectors representing this matrix
     */
    private SetlList toVectorList() {
        SetlList container = new SetlList(this.matrix.getRowDimension());
        for(double[] row : this.matrix.getArray()) {
            ArrayList<Double> vector = new ArrayList<>();
            for (double v : row) {
                vector.add(v);
            }
            container.addMember(null, new SetlVector(vector));
        }
        return container;
    }

    /* arithmetic operations */

    @Override
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        boolean isMatrix = subtrahend instanceof SetlMatrix;
        if(isMatrix || subtrahend instanceof SetlVector) {
            SetlMatrix b = isMatrix ? (SetlMatrix)subtrahend : new SetlMatrix(state, (SetlVector)subtrahend);
            if(this.matrix.getColumnDimension() == b.matrix.getColumnDimension()) {
                if(this.matrix.getRowDimension() == b.matrix.getRowDimension()) {
                    return new SetlMatrix(this.matrix.minus(b.matrix));
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

    @Override
    public Value differenceAssign(final State state, final Value subtrahend) throws SetlException {
        boolean isMatrix = subtrahend instanceof SetlMatrix;
        if(isMatrix || subtrahend instanceof SetlVector) {
            SetlMatrix b = isMatrix ? (SetlMatrix)subtrahend : new SetlMatrix(state, (SetlVector)subtrahend);
            if(this.matrix.getColumnDimension() == b.matrix.getColumnDimension()) {
                if(this.matrix.getRowDimension() == b.matrix.getRowDimension()) {
                    this.matrix.minusEquals(b.matrix);
                    return this;
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

    @Override
    public Value factorial(final State state) throws SetlException {
        return this.transpose();
    }

    @Override
    public Value power(final State state, final Value exponent) throws SetlException {
        if(!this.isSquare()) {
            throw new IncompatibleTypeException("Power is only defined on square matrices."); // Same in Octave
        }
        if(exponent.jIntConvertible()) {
            int ex = exponent.toJIntValue(state);

            if(ex == 0) {
                Jama.Matrix base = new Jama.Matrix(this.matrix.getRowDimension(), this.matrix.getRowDimension());
                for(int i = 0; i < this.matrix.getRowDimension(); i++) {
                    base.set(i, i, 1);
                }
                return new SetlMatrix(base);
            }

            Jama.Matrix base;
            if (ex < 0) {
                base = this.inverse().matrix;
                ex = -ex;
            } else {
                base = this.matrix;
            }
            Jama.Matrix result = base;
            for (int i = 1; i < ex; i++) {
                result = result.times(base);
            }
            return new SetlMatrix(result);
        } else {
            throw new IncompatibleTypeException("Power on matrices is only defined for integer exponents.");
        }
    }

    /*
     * Calculate the matrix product or the scalar product
     */
    @Override
    public Value product(final State state, final Value multiplier) throws SetlException {
        if(multiplier instanceof SetlMatrix) {
            SetlMatrix b = (SetlMatrix) multiplier;
            if(this.matrix.getColumnDimension() == b.matrix.getRowDimension()) {
                return new SetlMatrix(this.matrix.times(b.matrix));
            } else {
                throw new IncompatibleTypeException("Matrix multiplication is only defined if the number of columns of the first matrix equals the number of rows of the second matrix.");
            }
        } else if(multiplier instanceof SetlVector) {
            SetlMatrix vectorMatrix = new SetlMatrix(state, (SetlVector) multiplier);
            if(this.matrix.getColumnDimension() == vectorMatrix.matrix.getRowDimension()) {
                Matrix result = this.matrix.times(vectorMatrix.matrix);
                if (result.getColumnDimension() == 1) {
                    ArrayList<Double> vector = new ArrayList<>();
                    for (int i = 0; i < result.getRowDimension(); i++) {
                        vector.add(result.get(i, 0));
                    }
                    return new SetlVector(vector);
                }
                throw new UndefinedOperationException("Multiplication result has unexpected dimensions: " + new SetlMatrix(result).toString(state));
            } else {
                throw new IncompatibleTypeException("Multiplication is only defined if the number of columns of the matrix equals the number of dimensions of the vector.");
            }
        } else if(multiplier instanceof NumberValue) {
            return new SetlMatrix(this.matrix.times(multiplier.toJDoubleValue(state)));
        } else if(multiplier instanceof Term) {
            return ((Term)multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException("Multiplier is not a matrix.");
        }
    }

    /*
     * Calculate the matrix product or the scalar product and assign it to this
     */
    @Override
    public Value productAssign(final State state, final Value multiplier) throws SetlException {
        boolean isMatrix = multiplier instanceof SetlMatrix;
        if(isMatrix || multiplier instanceof SetlVector) {
            SetlMatrix b = isMatrix ? (SetlMatrix)multiplier : new SetlMatrix(state, (SetlVector)multiplier);
            if(this.matrix.getColumnDimension() == b.matrix.getRowDimension()) {
                this.matrix = this.matrix.times(b.matrix);
                return this;
            } else {
                throw new IncompatibleTypeException("Matrix multiplication is only defined if the number of columns of the first matrix equals the number of rows of the second matrix.");
            }
        } else if(multiplier instanceof NumberValue) {
            this.matrix.timesEquals(multiplier.toJDoubleValue(state));
            return this;
        } else if(multiplier instanceof Term) {
            return ((Term)multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException("Multiplier is not a matrix.");
        }
    }

    @Override
    public Value sum(final State state, final Value summand) throws SetlException {
        boolean isMatrix = summand instanceof SetlMatrix;
        if(isMatrix || summand instanceof SetlVector) {
            SetlMatrix b = isMatrix ? (SetlMatrix)summand : new SetlMatrix(state, (SetlVector)summand);
            if(this.matrix.getColumnDimension() == b.matrix.getColumnDimension()) {
                if(this.matrix.getRowDimension() == b.matrix.getRowDimension()) {
                    return new SetlMatrix(this.matrix.plus(b.matrix));
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

    @Override
    public Value sumAssign(final State state, final Value summand) throws SetlException {
        boolean isMatrix = summand instanceof SetlMatrix;
        if(isMatrix || summand instanceof SetlVector) {
            SetlMatrix b = isMatrix ? (SetlMatrix)summand : new SetlMatrix(state, (SetlVector)summand);
            if(this.matrix.getColumnDimension() == b.matrix.getColumnDimension()) {
                if(this.matrix.getRowDimension() == b.matrix.getRowDimension()) {
                    this.matrix.plusEquals(b.matrix);
                    return this;
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

    /* operations on collection values (Lists/Tuples, Sets [, Strings]) */

    @Override
    public void addMember(State state, Value element) throws SetlException {
        double[] dElems;
        if(element instanceof SetlVector) {
            ArrayList<Double> elems = ((SetlVector)element).getVectorCopy();
            dElems = new double[elems.size()];
            for(int i = 0; i < dElems.length; i++) {
                dElems[i] = elems.get(i);
            }
        } else if(element instanceof CollectionValue) {
            CollectionValue col = (CollectionValue)element;
            dElems = new double[col.size()];
            int idx = 0;
            for(Value v : col) {
                if(v.jDoubleConvertible()) {
                    dElems[idx] = v.toJDoubleValue(state);
                } else {
                    return;
                }
                idx++;
            }
        } else {
            return;
        }
        if(dElems.length == this.matrix.getColumnDimension()) {
            // Vector will be added as a row
            double[][] result = new double[this.matrix.getRowDimension() + 1][this.matrix.getColumnDimension()];
            System.arraycopy(this.matrix.getArray(), 0, result, 0, this.matrix.getRowDimension());
            result[this.matrix.getRowDimension()] = dElems;
            this.matrix = new Jama.Matrix(result);
        } else if(dElems.length == this.matrix.getRowDimension()) {
            // Vector will be added as a column
            double[][] result = new double[this.matrix.getRowDimension()][this.matrix.getColumnDimension() + 1];
            for(int i = 0; i < result.length; i++) {
                System.arraycopy(this.matrix.getArray()[i], 0, result[i], 0, this.matrix.getColumnDimension());
                result[i][this.matrix.getColumnDimension()] = dElems[i];
            }
            this.matrix = new Jama.Matrix(result);
        }
    }

    @Override
    public SetlBoolean containsMember(State state, Value element) throws SetlException {
        if(element instanceof CollectionValue) {
            CollectionValue c = (CollectionValue)element;
            double[] v = new double[c.size()];
            int i = 0;
            for(Value subelem : c) {
                v[i] = subelem.toJDoubleValue(state);
                i++;
            }
            for(double[] a : this.matrix.getArray()) {
                if(Arrays.equals(a, v)) {
                    return SetlBoolean.TRUE;
                }
            }
            return SetlBoolean.FALSE;
        } else if(element instanceof NumberValue) {
                double v = element.toJDoubleValue(state);
                for(double[] a : this.matrix.getArray()) {
                    for(double b : a) {
                        if(Double.compare(b, v) == 0) {
                            return SetlBoolean.TRUE;
                        }
                    }
                }
        }
        return SetlBoolean.FALSE;
    }

    @Override
    public Value firstMember() {
        try {
            return this.getMember(1);
        } catch(SetlException ex) {
            return Om.OM;
        }
    }

    @Override
    public Value getMember(int index) throws SetlException {
        if(index > this.matrix.getRowDimension() || index < 1) {
            throw new IncompatibleTypeException("Index out of bounds: " + index);
        }
        ArrayList<Double> container = new ArrayList<>(this.matrix.getColumnDimension());
        for(double d : this.matrix.getArray()[index - 1]) {
            container.add(d);
        }
        return new SetlVector(container);
    }

    @Override
    public Value getMember(State state, Value index) throws SetlException {
        if(index.jIntConvertible()) {
            return this.getMember(index.toJIntValue(state));
        } else {
            throw new IncompatibleTypeException("Given index is not a number.");
        }
    }

    @Override
    public Value getMembers(final State state, final int expectedNumberOfMembers, final int lowFromStart, final int highFromStart) throws SetlException {
        double[][] base = new double[expectedNumberOfMembers][1];
        // in java the index is one lower
        for (int pos = 0, i = lowFromStart - 1; expectedNumberOfMembers > 0 && i < highFromStart && i < matrix.getRowDimension(); ++pos, ++i) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                base[pos][j] = matrix.get(i, j);
            }
        }
        return new SetlMatrix(new Jama.Matrix(base));
    }


    @Override
    public Value lastMember() {
        try {
            return this.getMember(this.matrix.getRowDimension());
        } catch(SetlException ex) {
            return Om.OM;
        }
    }

    @Override
    public Value maximumMember(State state) throws SetlException {
        double momentaryMax = Double.NEGATIVE_INFINITY;
        for(double[] a : this.matrix.getArray()) {
            for(double b : a) {
                if(b > momentaryMax) {
                    momentaryMax = b;
                }
            }
        }
        return SetlDouble.valueOf(momentaryMax);
    }

    @Override
    public Value minimumMember(State state) throws SetlException {
        double momentaryMin = Double.POSITIVE_INFINITY;
        for(double[] a : this.matrix.getArray()) {
            for(double b : a) {
                if(b < momentaryMin) {
                    momentaryMin = b;
                }
            }
        }
        return SetlDouble.valueOf(momentaryMin);
    }

    @Override
    public Value removeFirstMember() {
        double[][] result = new double[this.matrix.getRowDimension() - 1][this.matrix.getColumnDimension()];
        System.arraycopy(this.matrix.getArray(), 1, result, 0, this.matrix.getColumnDimension() - 1);
        return new SetlMatrix(new Jama.Matrix(result));
    }

    @Override
    public Value removeLastMember() {
        double[][] result = new double[this.matrix.getRowDimension() - 1][this.matrix.getColumnDimension()];
        System.arraycopy(this.matrix.getArray(), 0, result, 0, this.matrix.getRowDimension() - 1);
        return new SetlMatrix(new Jama.Matrix(result));
    }

    @Override
    public void removeMember(State state, Value element) throws IncompatibleTypeException {
        if (! (element instanceof SetlVector)) {
            throw new IncompatibleTypeException("Element " + element + " that should be removed isn't part of this matrix: " + this);
        }
        int index = -1;
        int tmp = 0;
        for(Value row : this.toVectorList()) {
            if(row.equalTo(element)) {
                index = tmp;
                break;
            }
            tmp++;
        }
        if(index > -1) {
            double[][] newArr = new double[this.matrix.getRowDimension() - 1][this.matrix.getColumnDimension()];
            double[][] oldArr = this.matrix.getArray();
            if(index == 0) {
                System.arraycopy(oldArr, 1, newArr, 0, newArr.length);
            } else if(index == oldArr.length - 1) {
                System.arraycopy(oldArr, 0, newArr, 0, newArr.length);
            } else if(index >= oldArr.length) {
                throw new IncompatibleTypeException("Internal Error in SetlMatrix.removeMember");
            } else {
                System.arraycopy(oldArr, 0, newArr, 0, index);
                System.arraycopy(oldArr, index + 1, newArr, index, newArr.length - index);
            }
            this.matrix = new Jama.Matrix(newArr);
        } else {
            throw new IncompatibleTypeException("Element " + element + " that should be removed isn't part of this matrix: " + this);
        }
    }

    @Override
    public void setMember(final State state, final Value index, final Value value) throws SetlException {
        if(index.jIntConvertible()) {
            setMember(state, index.jIntValue(), value);
        } else {
            throw new IncompatibleTypeException("Matrix row access index must be an integer.");
        }
    }

    @Override
    public void setMember(final State state, int index, final Value value) throws SetlException {
        if(index > this.matrix.getRowDimension() || index < 1) {
            throw new IncompatibleTypeException("Index out of bounds: " + index);
        }
        if(value instanceof CollectionValue) {
            CollectionValue col = (CollectionValue) value;
            if(col.size() != this.matrix.getColumnDimension()) {
                throw new IncompatibleTypeException("The collection and a row of this matrix have different numbers of elements.");
            }
            List<Double> newRow = new ArrayList<>(col.size());
            for(Value elem : col) {
                if(elem.jDoubleConvertible()) {
                    newRow.add(elem.jDoubleValue());
                } else {
                    throw new IncompatibleTypeException("Matrix row assign: Element " + elem + " of the collection is not a number.");
                }
            }
            for(int i = 0; i < newRow.size(); i++) {
                this.matrix.set(index, i, newRow.get(i));
            }
        } else {
            throw new IncompatibleTypeException("Argument " + value + " to replace matrix row " + index + " is not a collection.");
        }
    }

    @Override
    public int size() {
        return this.matrix.getRowDimension();
    }

    /* special matrix functions */

    /**
     * Calculate the condition of the matrix.
     *
     * @return Ratio of largest to smallest singular value.
     * @throws UndefinedOperationException thrown condition is NaN.
     */
    public SetlDouble condition() throws UndefinedOperationException {
        return SetlDouble.valueOf(matrix.cond());
    }

    /**
     * Calculate the determinant of this matrix
     *
     * @return number
     * @throws UndefinedOperationException thrown if not square.
     */
    public SetlDouble determinant() throws UndefinedOperationException {
        if(this.isSquare()) {
            return SetlDouble.valueOf(this.matrix.det());
        } else {
            throw new UndefinedOperationException("Matrix needs to be square.");
        }
    }

    /**
     * Calculate eigen vector matrix
     *
     * @param state Current state of the running setlX program.
     * @return matrix
     * @throws UndefinedOperationException thrown if not square.
     */
    public SetlList eigenVectors(State state) throws UndefinedOperationException {
        double[][] matrix = getEigenvalueDecomposition().getV().getArray();
        int n = matrix.length;
        SetlList vectors = new SetlList(n);
        for (int column = 0; column < n; column++) {
            ArrayList<Double> vector = new ArrayList<>(n);
            for (double[] aMatrix : matrix) {
                vector.add(aMatrix[column]);
            }
            vectors.addMember(state, new SetlVector(vector));
        }
        return vectors;
    }

    /**
     * Hadamard product of two matrices, which is calculated like this:
     *
     * hadamard := procedure(A, B) {
     *      n := #A;
     *      m := #A[1];
     *      return la_matrix([ [A[i][j] * B[i][j] : j in [1..m]] : i in [1..n] ]);
     * };
     *
     * @param state Current state of the running setlX program.
     * @param other other matrix
     * @return list of numbers
     * @throws IncompatibleTypeException thrown if 'other' is not a matrix.
     * @throws UndefinedOperationException thrown if both matrices are not of the same size.
     */
    public SetlMatrix hadamardProduct(State state, Value other) throws IncompatibleTypeException, UndefinedOperationException {
        if (other.getClass() == SetlMatrix.class) {
            final double[][] thisMatrix = matrix.getArray();
            final int n = thisMatrix.length;
            final int m = thisMatrix[0].length;

            final double[][] otherMatrix = ((SetlMatrix) other).matrix.getArray();
            if (n != otherMatrix.length || m != otherMatrix[0].length) {
                throw new UndefinedOperationException("Both matrices must be of equal dimensions.");
            }

            double[][] result = new double[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    result[i][j] =  thisMatrix[i][j] * otherMatrix[i][j];
                }
            }

            return new SetlMatrix(new Jama.Matrix(result, n, m));

        } else {
            throw new IncompatibleTypeException("Operand '" + other.toString(state) + "' is not a matrix.");
        }
    }

    /**
     * Calculates all eigen values
     *
     * @param state Current state of the running setlX program.
     * @return list of numbers
     * @throws UndefinedOperationException thrown if not square.
     */
    public SetlList eigenValues(State state) throws UndefinedOperationException {
        EigenvalueDecomposition result = getEigenvalueDecomposition();
        double[][] values = result.getD().getArray();

        SetlList composition = new SetlList(values.length);
        for(int i = 0; i < values.length; i++) {
            composition.addMember(state, SetlDouble.valueOf(values[i][i]));
        }
        return composition;
    }

    private EigenvalueDecomposition getEigenvalueDecomposition() throws UndefinedOperationException {
        if ( ! this.isSquare()) {
            throw new UndefinedOperationException("Not a square matrix.");
        }

        EigenvalueDecomposition result = this.matrix.eig();
        double[][] values = result.getD().getArray();

        // test if result is usable
        double epsilon = getEpsilon(values);
        for (int i = 0; i < values.length - 1; i++) {
            if (Math.abs(values[i][i+1]) > epsilon) {
                throw new UndefinedOperationException("Matrix is not diagonalizable.");
            }
        }
        return result;
    }

    private SetlMatrix inverse() throws UndefinedOperationException {
        if (! isSquare()) {
            throw new UndefinedOperationException(
                    "Matrix must be square to compute inverse."
            );
        }
        LUDecomposition luDecomposition = new LUDecomposition(this.matrix);
        if (! luDecomposition.isNonsingular()) {
            throw new UndefinedOperationException(
                    "Matrix must not be singular to compute inverse."
            );
        }
        Matrix identity = Matrix.identity(this.matrix.getRowDimension(), this.matrix.getRowDimension());
        try {
            return new SetlMatrix(luDecomposition.solve(identity));
        } catch (RuntimeException re) {
            throw new UndefinedOperationException("Error during computation of inverse matrix: " + re.getMessage(), re);
        }
    }

    /**
     * Compute pseudo inverse of this matrix.
     *
     * @return Pseudo inverse of this matrix.
     * @throws UndefinedOperationException in case pseudo inverse cannot be computed.
     */
    public SetlMatrix pseudoInverse() throws UndefinedOperationException {
        Matrix identity = Matrix.identity(this.matrix.getRowDimension(), this.matrix.getRowDimension());
        try {
            return new SetlMatrix((new QRDecomposition(this.matrix)).solve(identity));
        } catch (RuntimeException re) {
            throw new UndefinedOperationException("Error during computation of pseudo inverse matrix: " + re.getMessage(), re);
        }
    }

    private boolean isSquare() {
        return this.matrix.getColumnDimension() == this.matrix.getRowDimension();
    }

    /**
     * Calculates singular matrix decomposition
     *
     * @param state Current state of the running setlX program.
     * @return 3-tuple of matrices [U, S, V]
     */
    public SetlList singularValueDecomposition(State state) {
        FixedSingularValueDecomposition result = new FixedSingularValueDecomposition(this.matrix.svd());
        SetlList container = new SetlList();
        container.addMember(state, new SetlMatrix(result.getU()));
        container.addMember(state, new SetlMatrix(result.getS()));
        container.addMember(state, new SetlMatrix(result.getV()));
        return container;
    }

    /**
     * Solve this * x = other
     *
     * @param other other matrix
     * @return x
     * @throws UndefinedOperationException thrown if number of rows differs.
     */
    public Value solve(SetlMatrix other) throws UndefinedOperationException {
        if(matrix.getRowDimension() != other.matrix.getRowDimension()) {
            throw new UndefinedOperationException("Row numbers must be equal to solve A * X = other.");
        }
        try {
            SetlMatrix result = new SetlMatrix(matrix.solve(other.matrix));
            if (result.matrix.getRowDimension() == 1 || result.matrix.getColumnDimension() == 1) {
                return result.toVector();
            } else {
                return result;
            }
        } catch (RuntimeException | IncompatibleTypeException re) {
            throw new UndefinedOperationException("Error during solve: " + re.getMessage(), re);
        }
    }

    /**
     * Transpose this matrix
     *
     * @return transposed matrix
     */
    /*package*/ SetlMatrix transpose() {
        return new SetlMatrix(this.matrix.transpose());
    }

    private double getEpsilon(double[][] values) {
        double max = 0.0;
        for (int i = 0; i < values.length; i++) {
            double value = Math.abs(values[i][i]);
            if (value > max) {
                max = value;
            }
        }
        if (max != 0.0) {
            return Math.pow(2,-53) * values.length * max;
        }
        return Math.pow(2,-53) * values.length;
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        canonical(state, sb);
    }

    @Override
    public void canonical(final State state, final StringBuilder sb) {
        double[][] matrixArray = matrix.getArray();
        sb.append("<< ");
        for (double[] row : matrixArray) {
            sb.append("<<");
            for (int j = 0; j < row.length; j++) {
                double d = row[j];
                SetlDouble.printDouble(state, sb, d);
                if (j < row.length - 1) {
                    sb.append(" ");
                }
            }
            sb.append(">> ");
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
        } else if (other.getClass() == SetlMatrix.class) {
            final Jama.Matrix otherMatrix = ((SetlMatrix) other).matrix;
            if (matrix == otherMatrix) {
                return 0; // clone
            }
            if (matrix.getRowDimension() < otherMatrix.getRowDimension() || matrix.getColumnDimension() < otherMatrix.getColumnDimension()) {
                return -1;
            } else if (matrix.getRowDimension() > otherMatrix.getRowDimension() || matrix.getColumnDimension() > otherMatrix.getColumnDimension()) {
                return 1;
            }
            for (int i = 0; i < matrix.getRowDimension(); i++) {
                for (int j = 0; j < matrix.getColumnDimension(); j++) {
                    final int cmp = Double.compare(matrix.get(i, j), otherMatrix.get(i, j));
                    if (cmp != 0) {
                        return cmp;
                    }
                }
            }
            return 0;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(SetlMatrix.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(Object other) {
        return other instanceof SetlMatrix && Arrays.deepEquals(this.matrix.getArray(), ((SetlMatrix) other).matrix.getArray());
    }

    @Override
    public int hashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) * 31 + matrix.hashCode();
    }
}

