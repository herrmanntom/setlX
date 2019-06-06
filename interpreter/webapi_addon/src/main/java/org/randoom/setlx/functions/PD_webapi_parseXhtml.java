package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.XhtmlParser;

import java.util.HashMap;

/**
 * webapi_parseXhtml(xhtmlString) : Use javax parsers to parse given xhtml content into document and map it to setlx objects.
 *                                  Also tries to fix up some common problems when parsing it as xml
 */
public class PD_webapi_parseXhtml extends PreDefinedProcedure {

    private final static ParameterDefinition XHTML_STRING = createParameter("xhtmlString");

    public final static PreDefinedProcedure DEFINITION = new PD_webapi_parseXhtml();

    private PD_webapi_parseXhtml() {
        super();
        addParameter(XHTML_STRING);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value argument = args.get(XHTML_STRING);
        if (argument.isString() == SetlBoolean.FALSE || argument.size() == 0) {
            throw new IncompatibleTypeException("Parameter is not a string: " + argument.toString(state));
        }
        String xhtmlString = argument.getUnquotedString(state);

        return XhtmlParser.parse(state, xhtmlString);
    }
}
