package org.randoom.setlx.functions;

import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

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
        super();
        addParameter("valueA");
        addParameter("valueB");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return Rational.valueOf(args.get(0).compareTo(args.get(1)));
    }
}

