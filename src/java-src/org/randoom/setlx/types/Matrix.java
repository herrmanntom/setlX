/**
 * 
 */
package org.randoom.setlx.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.MatrixException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

/**
 * @author Patrick Robinson
 *
 */
public class Matrix extends Value { // CollectionValue ?

    Jama.Matrix value;

    private Matrix(Jama.Matrix v) {
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
        // TODO Auto-generated method stub
        // FIXME What the hell is this supposed to do?
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
        // TODO
        return null;
    }
    
    @Override
    public Value productAssign(final State state, final Value multiplier) throws SetlException {
        // TODO
        return null;
    }
    
    @Override
    public Value sum(final State state, final Value summand) throws IncompatibleTypeException {
        // TODO
        return null;
    }
    
    @Override
    public Value sumAssign(final State state, final Value summand) throws SetlException {
        // TODO
        return null;
    }
    
    @Override
    public void canonical(final State state, final StringBuilder sb) {
        double[][] a = value.getArray();
        sb.append("[");
        for(double[] a1 : a) {
            sb.append(" [");
            for(double a2 : a1) {
                sb.append(" ").append(a2).append(" ");
            }
            sb.append("] ");
        }
        sb.append("]");
    }
}
