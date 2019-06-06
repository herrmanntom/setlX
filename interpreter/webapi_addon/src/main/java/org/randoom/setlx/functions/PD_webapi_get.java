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
 * webapi_get(targetUrl, cookieDataMap) : Send GET request to target url
 */
public class PD_webapi_get extends PreDefinedProcedure {

    private final static ParameterDefinition BASE_URL = createParameter("baseUrl");
    private final static ParameterDefinition QUERY_PARAMETER_MAP = createParameter("queryParameterMap");
    private final static ParameterDefinition COOKIE_DATA_MAP = createParameter("cookieDataMap");

    public final static PreDefinedProcedure DEFINITION = new PD_webapi_get();

    private PD_webapi_get() {
        super();
        addParameter(BASE_URL);
        addParameter(QUERY_PARAMETER_MAP);
        addParameter(COOKIE_DATA_MAP);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value argument = args.get(BASE_URL);
        if (argument.isString() == SetlBoolean.FALSE || argument.size() == 0) {
            throw new IncompatibleTypeException("Parameter is not a string: " + argument.toString(state));
        }
        final Value argument2 = args.get(QUERY_PARAMETER_MAP);
        if (argument2.isMap() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("Parameter is not a map: " + argument2.toString(state));
        }
        final Value argument3 = args.get(COOKIE_DATA_MAP);
        if (argument3.isMap() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("Parameter is not a map: " + argument3.toString(state));
        }
        String url = argument.getUnquotedString(state);

        return WebRequests.get(state, url, (SetlSet) argument2, (SetlSet) argument3);
    }
}
