package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.HashMap;
import java.util.List;

public class PD_plot_addBullet extends PreDefinedProcedure {


    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef XYTUPEL = createParameter("xyTupel");
    private final static ParameterDef RGBLIST = createOptionalParameter("RGBList", Rational.ONE);
    public final static PreDefinedProcedure DEFINITION = new PD_plot_addBullet();

    private PD_plot_addBullet() {
        super();
        addParameter(CANVAS);
        addParameter(XYTUPEL);
        addParameter(RGBLIST);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        SetlList xylist = (SetlList) args.get(XYTUPEL);

        if(!args.get(RGBLIST).equalTo(Rational.ONE)){

        }

        SetlList rgblistSetl = (SetlList) args.get(RGBLIST);

        if(!(rgblistSetl.size()==3)){
            return new SetlString("Paramter RGBLIST must have exactly three entrys");
        }

        List bulletList = ConvertSetlTypes.convertSetlListAsDouble(xylist);

        List rgblist = ConvertSetlTypes.convertSetlListAsInteger(rgblistSetl);

        return ConnectJFreeChart.getInstance().addBullets((Canvas) args.get(CANVAS), bulletList, rgblist);
    }
}
