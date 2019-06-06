package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.XmlParser;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * webapi_parseXml(xmlString) : Use javax parsers to parse given xml content into document and map it to setlx objects.
 */
public class PD_webapi_parseXml extends PreDefinedProcedure {

    private final static ParameterDefinition XML_STRING = createParameter("xmlString");

    public final static PreDefinedProcedure DEFINITION = new PD_webapi_parseXml();

    private PD_webapi_parseXml() {
        super();
        addParameter(XML_STRING);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value argument = args.get(XML_STRING);
        if (argument.isString() == SetlBoolean.FALSE || argument.size() == 0) {
            throw new IncompatibleTypeException("Parameter is not a string: " + argument.toString(state));
        }
        String xmlString = argument.getUnquotedString(state);

        return XmlParser.parse(state, new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
    }
}
