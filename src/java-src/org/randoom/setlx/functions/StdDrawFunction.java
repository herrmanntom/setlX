package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.NumberValue;
import org.randoom.setlx.types.Real;
import org.randoom.setlx.types.Value;

public abstract class StdDrawFunction extends PreDefinedFunction{

    protected StdDrawFunction(String name) {
        super(name);
    }

    protected Integer integerFromValue(Value v) throws SetlException{
        if (v instanceof NumberValue){
            Value arg = v.toReal();
            return (int) ((Real) arg).jDoubleValue();
        }else{
            throw new IncompatibleTypeException( "Error in \""+ getName() +"\":\n" + 
                                                 "Parameter " + v + " of incompatible Type.");
        }
    }
    
    
    protected Double doubleFromValue(Value v) throws SetlException{
        if (v instanceof NumberValue){
            Value arg = v.toReal();
            return ((Real) arg).jDoubleValue();
        }else{
            throw new IncompatibleTypeException( "Error in \""+ getName() +"\":\n" + 
                                                 "Parameter " + v + " of incompatible Type.");
        }
    }
    
    protected String stringFromValue(Value v){
        return v.toString();
    }
}
