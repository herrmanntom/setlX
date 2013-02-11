package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_text extends StdDrawFunction {
    public final static PreDefinedFunction DEFINITION = new PD_text();
    
    
    protected PD_text() {
        super("text");
        addParameter("x");
        addParameter("y");
        addParameter("string");
        addParameter("degrees");
        allowFewerParameters();
    }

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        double x = doubleFromValue(args.get(0));
        double y = doubleFromValue(args.get(1));
        String s = stringFromValue(args.get(2));
        if ( args.size() == 3 ){
            StdDraw.text(x, y, s);
        }else if ( args.size()  == 4 ){
            StdDraw.text(x, y, s, doubleFromValue(args.get(3)));
        }else{
            return SetlBoolean.FALSE;
        }
        return SetlBoolean.TRUE;
    }

}
