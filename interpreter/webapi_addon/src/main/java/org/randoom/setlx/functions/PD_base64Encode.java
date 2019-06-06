package org.randoom.setlx.functions;

import org.apache.cxf.common.util.Base64Utility;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * base64Encode(plainString) : Encode string with base64 (Assumes UTF8 character encoding)
 */
public class PD_base64Encode extends PreDefinedProcedure {

    private final static ParameterDefinition PLAIN_STRING = createParameter("plainString");

    public final static PreDefinedProcedure DEFINITION = new PD_base64Encode();

    private PD_base64Encode() {
        super();
        addParameter(PLAIN_STRING);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value argument = args.get(PLAIN_STRING);
        if (argument.isString() == SetlBoolean.FALSE || argument.size() == 0) {
            throw new IncompatibleTypeException("Parameter is not a string: " + argument.toString(state));
        }

        return new SetlString(Base64Utility.encode(argument.getUnquotedString(state).getBytes(StandardCharsets.UTF_8)));
    }
}
