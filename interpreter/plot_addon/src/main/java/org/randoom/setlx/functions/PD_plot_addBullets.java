package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.HashMap;
import java.util.List;

public class PD_plot_addBullets extends PreDefinedProcedure {


    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef XYTUPEL = createParameter("xyTupel");
    private final static ParameterDef RGBLIST = createOptionalParameter("RGBList", Rational.ONE);
    public final static PreDefinedProcedure DEFINITION = new PD_plot_addBullets();

    private PD_plot_addBullets() {
        super();
        addParameter(CANVAS);
        addParameter(XYTUPEL);
        addParameter(RGBLIST);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        SetlList xylist = (SetlList) args.get(XYTUPEL);
        List<List<Double>> bulletList = ConvertSetlTypes.convertSetlListAsDouble(xylist);

        //simple check to reminde user of list of lists
        if(!(bulletList.get(0) instanceof List)){
            return new SetlString("Parameter xyTupel has to be a list of lists eq [[1,2], [3,4]]");
        }

        //if the color parameter is set
        if(!args.get(RGBLIST).equalTo(Rational.ONE)){
            SetlList rgblistSetl = (SetlList) args.get(RGBLIST);
            if(!(rgblistSetl.size()==3)){
                return new SetlString("Paramter RGBLIST must have exactly three entrys");
            }
            List<Integer> rgblist = ConvertSetlTypes.convertSetlListAsInteger(rgblistSetl);
            //TODO: change bulletSize to Parameter
            return ConnectJFreeChart.getInstance().addBullets((Canvas) args.get(CANVAS), bulletList, rgblist, 5.0);
        }

        //if the color parameter is not set
        //TODO: change bulletSize to Parameter
        return ConnectJFreeChart.getInstance().addBullets((Canvas) args.get(CANVAS), bulletList, 5.0);
    }
}
