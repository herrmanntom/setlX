package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.FileNotReadableException;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.XmlParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * webapi_parseXmlFile(pathToXmlFile) : Use javax parsers to parse given xml content into document and map it to setlx objects.
 */
public class PD_webapi_parseXmlFile extends PreDefinedProcedure {

    private final static ParameterDefinition PATH_TO_XML_FILE = createParameter("pathToXmlFile");

    public final static PreDefinedProcedure DEFINITION = new PD_webapi_parseXmlFile();

    private PD_webapi_parseXmlFile() {
        super();
        addParameter(PATH_TO_XML_FILE);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value argument = args.get(PATH_TO_XML_FILE);
        if (argument.isString() == SetlBoolean.FALSE || argument.size() == 0) {
            throw new IncompatibleTypeException("Parameter is not a string: " + argument.toString(state));
        }
        String xmlPath = argument.getUnquotedString(state);

        try {
            return XmlParser.parse(state, new FileInputStream(xmlPath));
        } catch (FileNotFoundException e) {
            throw new FileNotReadableException("Could not parse '" + xmlPath + "' as xml", e);
        }
    }
}
