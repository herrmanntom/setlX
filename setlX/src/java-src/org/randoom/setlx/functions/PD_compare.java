package org.randoom.setlx.functions;

import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;

import java.util.List;

// compare(valueA, valueB)       : Compares two arbitrary values, returns an integer.
//                                 Return value <  0 means: valueA "<" valueB
//                                 Return value == 0 means: valueA ==  valueB
//                                 Return value >  0 means: valueA ">" valueB
//                                 Note that order between different types was chosen
//                                 arbitrarily and implies no special meaning.

public class PD_compare extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_compare();

    private PD_compare() {
        super("compare");
        addParameter("valueA");
        addParameter("valueB");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return Rational.valueOf(args.get(0).compareTo(args.get(1)));
    }
}

