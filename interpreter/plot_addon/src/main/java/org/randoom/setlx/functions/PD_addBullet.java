package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Canvas;
import org.randoom.setlx.utilities.ConnectJMathPlot;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_addBullet extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef XYTUPEL = createParameter("xyTupel");
    public final static PreDefinedProcedure DEFINITION = new PD_addBullet();
    private PD_addBullet(){
        super();
        addParameter(CANVAS);
        addParameter(XYTUPEL);
    }
    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        SetlList list = (SetlList)args.get(XYTUPEL);
        if(!(list.size()==2)){
            return new SetlString("Parameter XYTUPEL must have exactly two entrys");
        }
        ConnectJMathPlot.getInstance().addBullet((Canvas)args.get(CANVAS), list);
        return new SetlString("Added Bullet "+args.get(XYTUPEL)+" to Canvas "+args.get(CANVAS));
    }
}
