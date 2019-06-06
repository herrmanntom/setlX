package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.WebRequests;

import java.util.HashMap;

/**
 * webapi_post(targetUrl, formDataMap, cookieDataMap) : Send POST request to target url
 */
public class PD_webapi_post extends PreDefinedProcedure {

    private final static ParameterDefinition TARGET_URL = createParameter("targetUrl");
    private final static ParameterDefinition FORM_DATA_MAP = createParameter("formDataMap");
    private final static ParameterDefinition COOKIE_DATA_MAP = createParameter("cookieDataMap");

    /** Definition of the PreDefinedProcedure 'webapi_parse_xhtml' */
    public final static PreDefinedProcedure DEFINITION = new PD_webapi_post();

    private PD_webapi_post() {
        super();
        addParameter(TARGET_URL);
        addParameter(FORM_DATA_MAP);
        addParameter(COOKIE_DATA_MAP);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value argument = args.get(TARGET_URL);
        if (argument.isString() == SetlBoolean.FALSE || argument.size() == 0) {
            throw new IncompatibleTypeException("Parameter is not a string: " + argument.toString(state));
        }
        final Value argument2 = args.get(FORM_DATA_MAP);
        if (argument2.isMap() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("Parameter is not a map: " + argument2.toString(state));
        }
        final Value argument3 = args.get(COOKIE_DATA_MAP);
        if (argument3.isMap() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("Parameter is not a map: " + argument3.toString(state));
        }
        String url = argument.getUnquotedString(state);

        HashMap<String, String> formData = new HashMap<>();
        for (Value e : (SetlSet) argument2) {
            SetlList entry = (SetlList) e;
            formData.put(entry.getMember(1).getUnquotedString(state), entry.getMember(2).getUnquotedString(state));
        }

        return WebRequests.post(state, url, formData, (SetlSet) argument3);
    }
}
