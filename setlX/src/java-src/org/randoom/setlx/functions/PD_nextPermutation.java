package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

// nextPermutation(list)         : returns the next permutation of the list, om if there `list' already is the last permutation

public class PD_nextPermutation extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_nextPermutation();

    private PD_nextPermutation() {
        super("nextPermutation");
        addParameter("list");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {

        return args.get(0).nextPermutation();

    }

}

