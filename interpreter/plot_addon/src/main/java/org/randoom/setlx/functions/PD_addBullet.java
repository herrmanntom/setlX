package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.HashMap;
import java.util.List;

public class PD_addBullet extends PreDefinedProcedure {


    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef XYTUPEL = createParameter("xyTupel");
    private final static ParameterDef RGBLIST = createParameter("RGBList");
    public final static PreDefinedProcedure DEFINITION = new PD_addBullet();

    private PD_addBullet() {
        super();
        addParameter(CANVAS);
        addParameter(XYTUPEL);
        addParameter(RGBLIST);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        SetlList xylist = (SetlList) args.get(XYTUPEL);
        SetlList rgblistSetl = (SetlList) args.get(RGBLIST);

        if (!(xylist.size() == 2)) {
            return new SetlString("Parameter XYTUPEL must have exactly two entrys");
        }
        if(!(rgblistSetl.size()==3)){
            return new SetlString("Paramter RGBLIST must have exactly three entrys");
        }

        Value xV = xylist.firstMember();
        Value yV = xylist.lastMember();
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


        List rgblist = ConvertSetlTypes.convertSetlList(rgblistSetl);

        ConnectJFreeChart.getInstance().addBullet((Canvas) args.get(CANVAS), xD, yD, rgblist);
        return new SetlString("Added Bullet (" + xD + "," + yD + ") to Canvas " + args.get(CANVAS));
    }
}
