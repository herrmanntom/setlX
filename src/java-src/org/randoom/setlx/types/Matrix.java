package org.randoom.setlx.types;

import java.util.Arrays;
import java.util.Iterator;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.MatrixException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;

/**
 * @author Patrick Robinson
 */
public class Matrix extends IndexedCollectionValue { // TODO Is not a CollectionValue Exception ?
    public Jama.Matrix value;

    public Matrix(Jama.Matrix v) {
        super();
        this.value = v;
    }

    public Matrix(final CollectionValue Init) throws SetlException {
        super();
        final int rowCount = Init.size();
        final int columnCount = ((CollectionValue)Init.firstMember()).size();
        double[][] base = new double[rowCount][columnCount];
        int currentRow = 0;
        for(Value row : Init) {
            if(!(row instanceof CollectionValue)) throw new MatrixException("Row " + (currentRow + 1) + " is not of collection type.");
            CollectionValue rowAsCollection = (CollectionValue)row;
            if(rowAsCollection.size() != columnCount) throw new MatrixException("Row " + (currentRow + 1) + " does not have the same length as the first row.");
            int currentColumn = 0;
            for(Value cell : rowAsCollection) {
                if(!(cell instanceof NumberValue)) throw new MatrixException("Cell(row " + (currentRow + 1) + " column " + (currentColumn + 1) + ") is not a number.");
                base[currentRow][currentColumn] = cell.toJDoubleValue(null); // TODO State
                currentColumn++;
            }
            currentRow++;
        }
        value = new Jama.Matrix(base);
    }

    /* (non-Javadoc)
     * @see org.randoom.setlx.types.Value#clone()
     */
    @Override
    public Value clone() {
        return new Matrix(value.copy());
    }

    /* (non-Javadoc)
     * @see org.randoom.setlx.types.Value#appendString(org.randoom.setlx.utilities.State, java.lang.StringBuilder, int)
     */
    @Override
    public void appendString(State state, StringBuilder sb, int tabs) {
        // TODO does this work as it should?
        canonical(state, sb);
    }

    /* (non-Javadoc)
     * @see org.randoom.setlx.types.Value#compareTo(org.randoom.setlx.types.Value)
     */
    @Override
    public int compareTo(Value other) {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.randoom.setlx.types.Value#compareToOrdering()
     */
    @Override
    protected int compareToOrdering() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.randoom.setlx.types.Value#equalTo(org.randoom.setlx.types.Value)
     */
    @Override
    public boolean equalTo(Value other) {
        return other instanceof Matrix && Arrays.deepEquals(this.value.getArray(), ((Matrix)other).value.getArray());
    }

    /* (non-Javadoc)
     * @see org.randoom.setlx.types.Value#hashCode()
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public Value product(final State state, final Value multiplier) throws SetlException {
        if(multiplier instanceof Matrix) {
            Matrix b = (Matrix)multiplier;
            // TODO check conditions
            return new Matrix(this.value.times(b.value));
        } else if(multiplier instanceof NumberValue) {
            NumberValue n = (NumberValue)multiplier;
            return new Matrix(this.value.times(n.toJDoubleValue(null)));
        } else {
            throw new MatrixException("Summand is not of type Matrix.");
        }
    }
    
    @Override
    public Value productAssign(final State state, final Value multiplier) throws MatrixException {
        // TODO
        return null;
    }
    
    public Matrix transpose() {
        // TODO map this to !
        return new Matrix(this.value.transpose());
    }
    
    public Matrix power(final State state, final int exponent) throws SetlException {
        // TODO exponent == 0 ?
        // TODO check condition
        Jama.Matrix base = exponent < 0 ? this.value.inverse() : this.value;
        Jama.Matrix result = base;
        for(int i = 1 /* No mistake, should be one */; i < Math.abs(exponent); i++) result = result.times(base);
        return new Matrix(result);
    }
    
    @Override
    public Value sum(final State state, final Value summand) throws MatrixException {
        if(summand instanceof Matrix) {
            Matrix b = (Matrix)summand;
            // TODO check conditions
            return new Matrix(this.value.plus(b.value));
        } else {
            throw new MatrixException("Summand is not of type Matrix.");
        }
    }
    
    @Override
    public Value sumAssign(final State state, final Value summand) throws MatrixException {
        if(summand instanceof Matrix) {
            Matrix b = (Matrix)summand;
            // TODO check conditions
            this.value.plusEquals(b.value);
            return this;
        } else {
            throw new MatrixException("Summand is not of type Matrix.");
        }
    }
    
    @Override
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        if(subtrahend instanceof Matrix) {
            Matrix b = (Matrix)subtrahend;
            // TODO check conditions
            return new Matrix(this.value.minus(b.value));
        } else {
            throw new MatrixException("Subtrahend is not of type Matrix.");
        }
    }
    
    @Override
    public Value differenceAssign(final State state, final Value subtrahend) throws SetlException {
        if(subtrahend instanceof Matrix) {
            Matrix b = (Matrix)subtrahend;
            // TODO check conditions
            this.value.minusEquals(b.value);
            return this;
        } else {
            throw new MatrixException("Subtrahend is not of type Matrix.");
        }
    }
    
    @Override
    public void canonical(final State state, final StringBuilder sb) {
        double[][] a = value.getArray();
        sb.append("«");
        for(double[] a1 : a) {
            sb.append(" ‹");
            for(double a2 : a1) {
                sb.append(" ").append(a2).append(" ");
            }
            sb.append("› ");
        }
        sb.append("»");
    }

    @Override
    public Value getMember(int index) throws SetlException {
        SetlList container = new SetlList(this.value.getColumnDimension());
        for(double d : this.value.getArray()[index - 1]) container.addMember(null, SetlDouble.valueOf(d));
        return container;
    }
    
    private SetlList toSetlList() {
        SetlList container = new SetlList(this.value.getRowDimension());
        for(double[] a : this.value.getArray()) {
            SetlList row = new SetlList(this.value.getColumnDimension());
            for(double b : a) {
                try {
                    row.addMember(null, SetlDouble.valueOf(b));
                } catch (UndefinedOperationException ex) {
                    // TODO wtf?
                }
            }
            container.addMember(null, row);
        }
        return container;
    }

    @Override
    public Iterator<Value> iterator() {
        
        return this.toSetlList().iterator();
    }

    @Override
    public Iterator<Value> descendingIterator() {
        return this.toSetlList().descendingIterator();
    }

    @Override
    public void addMember(State state, Value element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private static double epsilon = 1E-5;
    
    @Override
    public SetlBoolean containsMember(State state, Value element) throws IncompatibleTypeException {
        if(element instanceof CollectionValue) {
            CollectionValue c = (CollectionValue)element;
            double[] v = new double[c.size()];
            int i = 0;
            for(Value subelem : c) {
                try {
                    v[i] = subelem.toJDoubleValue(state);
                } catch (SetlException ex) {
                    return SetlBoolean.FALSE;
                }
                i++;
            }
            for(double[] a : this.value.getArray()) {
                if(Arrays.equals(a,v)) {
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
            } catch (SetlException ex) {
                // TODO Do something more?
                return SetlBoolean.FALSE;
            }
        }
        return SetlBoolean.FALSE;
    }

    @Override
    public Value firstMember() {
        try {
            return this.getMember(1);
        } catch (SetlException ex) {
            // TODO do something
            // Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
            return Om.OM;
        }
    }

    @Override
    public Value getMember(State state, Value index) throws SetlException {
        if((index instanceof NumberValue)) {
            return this.getMember(((NumberValue)index).toJIntValue(state));
        } else {
            throw new MatrixException("Given index is not a number.");
        }
    }

    @Override
    public Value lastMember() {
        try {
            return this.getMember(this.value.getRowDimension());
        } catch (SetlException ex) {
            // TODO do something
            // Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
            return Om.OM;
        }
    }

    @Override
    public Value maximumMember(State state) throws SetlException {
        double momentaryMax = Double.NEGATIVE_INFINITY;
        for(double[] a : this.value.getArray()) {
            for(double b : a) {
                if(b > momentaryMax) momentaryMax = b;
            }
        }
        return SetlDouble.valueOf(momentaryMax);
    }

    @Override
    public Value minimumMember(State state) throws SetlException {
        double momentaryMin = Double.POSITIVE_INFINITY;
        for(double[] a : this.value.getArray()) {
            for(double b : a) {
                if(b < momentaryMin) momentaryMin = b;
            }
        }
        return SetlDouble.valueOf(momentaryMin);
    }

    @Override
    public void removeMember(Value element) throws IncompatibleTypeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Value removeFirstMember() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Value removeLastMember() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int size() {
        return this.value.getRowDimension();
    }

    @Override
    public MatchResult matchesTerm(State state, Value other) throws SetlException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
