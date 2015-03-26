package org.randoom.setlx.functions;


import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.*;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * Created by fabian on 26.03.2015.
 */
public class PD_createCanvas extends PreDefinedProcedure {

    public final static PreDefinedProcedure
            DEFINITION = new PD_createCanvas();

    private PD_createCanvas(){ super(); }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        return new SetlString("created Canvas");
    }
}
