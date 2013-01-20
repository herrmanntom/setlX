package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.IndexedCollectionValue;
import org.randoom.setlx.types.Value;

public abstract class StdDrawPolygonFunction extends StdDrawFunction {

    protected StdDrawPolygonFunction(String name) {
        super(name);
    }

        
    protected double[] doubleArrayFromValue( Value v ) throws SetlException{
        if (v instanceof IndexedCollectionValue){
            double[] array = new double[((IndexedCollectionValue) v).size()];
            int i = 0;
            for ( Value val : ((IndexedCollectionValue) v) ){
                array[i++] = doubleFromValue(val);        
            }
            return array;
        }else{
            throw new IncompatibleTypeException( "Error in \""+ getName() +"\":\n" + 
                                                 "Parameter " + v + " of incompatible Type.");
        }    
    }
    

}
