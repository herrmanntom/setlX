/**
 * 
 */
package org.randoom.setlx.types;

import java.util.ArrayList;
import java.util.List;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.utilities.State;

/**
 * @author Patrick Robinson
 *
 */
public class Matrix extends Value {
        
        Jama.Matrix value;
    
        public Matrix(final CollectionValue Init) throws SetlException {
            super();
            List<double[]> all = new ArrayList<double[]>(Init.size());
            int n = -1;
            for(Value row : Init) {
                // TODO typeof check
                CollectionValue colrow = (CollectionValue)row;
                int nt = 0;
                double[] cells = new double[colrow.size()];
                for(Value cell : colrow) {
                    // TODO typeof check
                    cells[nt] = cell.toJDoubleValue(null); // TODO State
                    nt++;
                }
                if(n > -1 && nt != n) {
                    // TODO throw exception »rows of different length«
                } else {
                    n = nt;
                }
                all.add(cells);
            }
            double[][] base = new double[all.size()][n];
            all.toArray(base);
            value = new Jama.Matrix(base);
        }

	/* (non-Javadoc)
	 * @see org.randoom.setlx.types.Value#clone()
	 */
	@Override
	public Value clone() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.randoom.setlx.types.Value#appendString(org.randoom.setlx.utilities.State, java.lang.StringBuilder, int)
	 */
	@Override
	public void appendString(State state, StringBuilder sb, int tabs) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.randoom.setlx.types.Value#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

}
