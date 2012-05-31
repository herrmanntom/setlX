package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Real;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;

import java.util.List;

// mathConst(name_of_constant)   : get the value of a mathematical constant (currently only pi and e)

public class PD_mathConst extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_mathConst();

    private PD_mathConst() {
        super("mathConst");
        addParameter("name_of_constant");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException {
        Value name = args.get(0);

        // create constants with max precision possible when --real256 option is used
        if (       name.getUnquotedString().equalsIgnoreCase( "e"  )) {

            return new Real("2.718281828459045235360287471352662497757247093699959574966967627724077");

        } else if (name.getUnquotedString().equalsIgnoreCase( "pi" )) {

            return new Real("3.141592653589793238462643383279502884197169399375105820974944592307816");

        } else {
            throw new IncompatibleTypeException("Name-argument '" + name + "' is not a known constant or not a string.");
        }
    }
}
