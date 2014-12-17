package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.IndexedCollectionValue;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

public abstract class GfxPolygonFunction extends GfxFunction {

    protected GfxPolygonFunction() {
        super();
    }


    protected double[] doubleArrayFromValue(final State state, final Value v) throws SetlException{
        if (v instanceof IndexedCollectionValue){
            final double[] array = new double[((IndexedCollectionValue) v).size()];
            int i = 0;
            for ( final Value val : ((IndexedCollectionValue) v) ){
                array[i++] = doubleFromValue(state, val);
            }
            return array;
        }else{
            throw new IncompatibleTypeException( "Error in \""+ getName() +"\":\n" +
                                                 "Parameter " + v + " of incompatible Type.");
        }
    }


}
