package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;

import java.util.List;

// rnd(numberOrCollectionValue [, numberOfChoices]) :
//                              a) If argument is an integer, returns a random number
//                                 between 0 and the argument (inclusive).
//                              b) If the argument is a rational number or real,
//                                 the `numberOfChoices' MUST be used and
//                                 a random number between 0 and the argument
//                                 (inclusive) will be returned. The number of
//                                 possible values in this range will equal
//                                 `numberOfChoices' which MUST be an integer >= 2.
//                              c) If the argument is a collectionValue,
//                                 a randomly selected member will be returned.

public class PD_rnd extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_rnd();

    private PD_rnd() {
        super("rnd");
        addParameter("collectionValue");
        addParameter("numberOfChoices");
        allowFewerParameters();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        if (args.size() == 1) {
            return args.get(0).rnd();
        } else if (args.size() == 2) {
            return args.get(0).rnd(args.get(1));
        } else {
            String error = "Procedure is defined with a larger number of parameters ";
            error +=       "(1 or 2).";
            throw new IncorrectNumberOfParametersException(error);
        }
    }
}

