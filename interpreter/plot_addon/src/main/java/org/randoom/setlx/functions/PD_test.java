package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CalcFunction;
import org.randoom.setlx.utilities.ConvertSetlTypes;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;
import java.util.List;

public class PD_test extends PreDefinedProcedure {

    public final static ParameterDef PARAM = createParameter("parameter");
    public final static PreDefinedProcedure
            DEFINITION = new PD_test();

    private PD_test() {
        super();
        addParameter(PARAM);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        CalcFunction cf = new CalcFunction("sin(x)", state);
        p(cf.calcYfromX(8.8));
        SetlList l = (SetlList)args.get(PARAM);
        List asdf = ConvertSetlTypes.convertSetlListAsDouble(l);

        l = null;
        return new SetlString("String");
    }

    public void p(Object o){
        System.out.println(o.toString());
    }
}
