package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Canvas;
import org.randoom.setlx.utilities.ConnectJFreeChart;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_addBullet extends PreDefinedProcedure {


    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef XYTUPEL = createParameter("xyTupel");
    public final static PreDefinedProcedure DEFINITION = new PD_addBullet();

    private PD_addBullet() {
        super();
        addParameter(CANVAS);
        addParameter(XYTUPEL);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        SetlList list = (SetlList) args.get(XYTUPEL);
        if (!(list.size() == 2)) {
            return new SetlString("Parameter XYTUPEL must have exactly two entrys");
        }
        Value xV = list.firstMember();
        Value yV = list.lastMember();
        double xD;
        double yD;

        if (xV.isInteger().equalTo(SetlBoolean.TRUE)) {
            xD = (double) xV.jIntValue();
        } else {
            xD = xV.jDoubleValue();
        }
        if (yV.isInteger().equalTo(SetlBoolean.TRUE)) {
            yD = (double) yV.jIntValue();
        } else {
            yD = yV.jDoubleValue();
        }

        ConnectJFreeChart.getInstance().addBullet((Canvas) args.get(CANVAS), xD, yD);
        return new SetlString("Added Bullet (" + xD + "," + yD + ") to Canvas " + args.get(CANVAS));
    }
}
