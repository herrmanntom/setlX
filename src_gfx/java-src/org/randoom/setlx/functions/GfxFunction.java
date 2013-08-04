package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.NumberValue;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

public abstract class GfxFunction extends PreDefinedProcedure {

    protected GfxFunction() {
        super();
    }

    protected Integer integerFromValue(final State state, final Value v) throws SetlException{
        if (v instanceof NumberValue){
            final Value arg = v.toDouble(state);
            return (int) ((SetlDouble) arg).jDoubleValue();
        }else{
            throw new IncompatibleTypeException( "Error in \""+ getName() +"\":\n" +
                                                 "Parameter " + v + " of incompatible Type.");
        }
    }


    protected Double doubleFromValue(final State state, final Value v) throws SetlException{
        if (v instanceof NumberValue){
            final Value arg = v.toDouble(state);
            return ((SetlDouble) arg).jDoubleValue();
        }else{
            throw new IncompatibleTypeException( "Error in \""+ getName() +"\":\n" +
                                                 "Parameter " + v + " of incompatible Type.");
        }
    }

    protected String stringFromValue(final Value v){
        return v.getUnquotedString();
    }
}
