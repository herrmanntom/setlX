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
 * webapi_getAndStoreFile(targetUrl, queryParameterMap, headerParameterMap, cookieDataMap, fileToWrite) : Send GET request to target url and store resulting entity to file
 */
public class PD_webapi_getAndStoreFile extends PreDefinedProcedure {

    private final static ParameterDefinition TARGET_URL = createParameter("targetUrl");
    private final static ParameterDefinition QUERY_PARAMETER_MAP = createParameter("queryParameterMap");
    private final static ParameterDefinition HEADER_PARAMETER_MAP = createParameter("headerParameterMap");
    private final static ParameterDefinition COOKIE_DATA_MAP = createParameter("cookieDataMap");
    private final static ParameterDefinition FILE_TO_WRITE = createParameter("fileToWrite");

    public final static PreDefinedProcedure DEFINITION = new PD_webapi_getAndStoreFile();

    private PD_webapi_getAndStoreFile() {
        super();
        addParameter(TARGET_URL);
        addParameter(QUERY_PARAMETER_MAP);
        addParameter(HEADER_PARAMETER_MAP);
        addParameter(COOKIE_DATA_MAP);
        addParameter(FILE_TO_WRITE);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value argument = args.get(TARGET_URL);
        if (argument.isString() == SetlBoolean.FALSE || argument.size() == 0) {
            throw new IncompatibleTypeException("Target URL argument is not a string: " + argument.toString(state));
        }
        final Value argument2 = args.get(QUERY_PARAMETER_MAP);
        if (argument2.isMap() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("Query Parameter argument is not a map: " + argument2.toString(state));
        }
        final Value argument3 = args.get(HEADER_PARAMETER_MAP);
        if (argument3.isMap() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("Header Parameter argument is not a map: " + argument3.toString(state));
        }
        final Value argument4 = args.get(COOKIE_DATA_MAP);
        if (argument4.isMap() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("Cookie Data argument is not a map: " + argument4.toString(state));
        }
        final Value argument5 = args.get(FILE_TO_WRITE);
        if (argument5.isString() == SetlBoolean.FALSE || argument5.size() == 0) {
            throw new IncompatibleTypeException("File to Write argument is not a string: " + argument5.toString(state));
        }
        String url = argument.getUnquotedString(state);
        String fileToWrite = argument5.getUnquotedString(state);

        return WebRequests.getAndStoreFile(state, url, (SetlSet) argument2, (SetlSet) argument3, (SetlSet) argument4, fileToWrite);
    }
}
