package org.randoom.setlx.functions;

import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64Utility;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.JVMException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * base64_decode(base64String) : Decode base64 string (Assumes UTF8 character encoding)
 */
public class PD_base64_decode extends PreDefinedProcedure {

    private final static ParameterDefinition BASE_64_STRING = createParameter("base64String");

    public final static PreDefinedProcedure DEFINITION = new PD_base64_decode();

    private PD_base64_decode() {
        super();
        addParameter(BASE_64_STRING);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value argument = args.get(BASE_64_STRING);
        if (argument.isString() == SetlBoolean.FALSE || argument.size() == 0) {
            throw new IncompatibleTypeException("Parameter is not a string: " + argument.toString(state));
        }

        try {
            return new SetlString(new String(Base64Utility.decode(argument.getUnquotedString(state)), StandardCharsets.UTF_8));
        } catch (Base64Exception e) {
            throw new JVMException("Could not base64 decode string: " + argument.toString(state), e);
        }
    }
}
