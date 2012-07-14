package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;

import java.util.List;

// split(string, pattern)        : splits string at pattern into a list of strings

public class PD_split extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_split();

    private PD_split() {
        super("split");
        addParameter("string");
        addParameter("pattern");
    }

    public Value execute(final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        return args.get(0).split(args.get(1));
    }
}

