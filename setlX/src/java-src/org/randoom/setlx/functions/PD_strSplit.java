package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;

import java.util.List;

// strSplit(string, pattern)     : splits string at pattern into a list of strings

public class PD_strSplit extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_strSplit();

    private PD_strSplit() {
        super("strSplit");
        addParameter("string");
        addParameter("pattern");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException {
        return args.get(0).split(args.get(1));
    }
}

