package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.WebRequests;

import java.util.HashMap;

/**
 * webapi_postJsonEntity(targetUrl, queryParameterMap, jsonEntityString, cookieDataMap) : Send POST request to target url
 */
public class PD_webapi_postJsonEntity extends PreDefinedProcedure {

    private final static ParameterDefinition TARGET_URL = createParameter("targetUrl");
    private final static ParameterDefinition QUERY_PARAMETER_MAP = createParameter("queryParameterMap");
    private final static ParameterDefinition ENTITY_STRING = createParameter("entityString");
    private final static ParameterDefinition COOKIE_DATA_MAP = createParameter("cookieDataMap");

    public final static PreDefinedProcedure DEFINITION = new PD_webapi_postJsonEntity();

    private PD_webapi_postJsonEntity() {
        super();
        addParameter(TARGET_URL);
        addParameter(QUERY_PARAMETER_MAP);
        addParameter(ENTITY_STRING);
        addParameter(COOKIE_DATA_MAP);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value argument = args.get(TARGET_URL);
        if (argument.isString() == SetlBoolean.FALSE || argument.size() == 0) {
            throw new IncompatibleTypeException("Target URL argument is not a string: " + argument.toString(state));
        }
        final Value argument2 = args.get(QUERY_PARAMETER_MAP);
        if (argument2.isMap() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("Query parameter argument is not a map: " + argument2.toString(state));
        }
        final Value argument3 = args.get(ENTITY_STRING);
        if (argument3.isString() == SetlBoolean.FALSE || argument.size() == 0) {
            throw new IncompatibleTypeException("Entity String argument is not a String: " + argument3.toString(state));
        }
        final Value argument4 = args.get(COOKIE_DATA_MAP);
        if (argument4.isMap() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("Cookie Data argument is not a map: " + argument4.toString(state));
        }
        String url = argument.getUnquotedString(state);
        String entity = argument3.getUnquotedString(state);

        return WebRequests.postJsonEntity(state, url, (SetlSet) argument2, entity, (SetlSet) argument4);
    }
}
