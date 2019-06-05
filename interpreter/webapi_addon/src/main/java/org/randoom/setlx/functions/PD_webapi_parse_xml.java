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
 * webapi_parse_xml(xml_string) : Use javax parsers to parse given xml content into document and map it to setlx objects.
 */
public class PD_webapi_parse_xml extends PreDefinedProcedure {

    private final static ParameterDefinition XML_STRING = createParameter("xml_string");

    /** Definition of the PreDefinedProcedure 'webapi_parse_xml' */
    public final static PreDefinedProcedure DEFINITION = new PD_webapi_parse_xml();

    private PD_webapi_parse_xml() {
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
