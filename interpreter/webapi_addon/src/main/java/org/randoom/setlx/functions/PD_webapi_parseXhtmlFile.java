package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.XhtmlParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

/**
 * webapi_parseXhtmlFile(pathToXmlFile) : Use javax parsers to parse given xhtml content into document and map it to setlx objects.
 *                                        Also tries to fix up some common problems when parsing it as xml
 */
public class PD_webapi_parseXhtmlFile extends PreDefinedProcedure {

    private final static ParameterDefinition PATH_TO_XHTML_FILE = createParameter("pathToXmlFile");

    public final static PreDefinedProcedure DEFINITION = new PD_webapi_parseXhtmlFile();

    private PD_webapi_parseXhtmlFile() {
        super();
        addParameter(PATH_TO_XHTML_FILE);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value argument = args.get(PATH_TO_XHTML_FILE);
        if (argument.isString() == SetlBoolean.FALSE || argument.size() == 0) {
            throw new IncompatibleTypeException("Parameter is not a string: " + argument.toString(state));
        }
        String xhtmlPathString = argument.getUnquotedString(state);

        try {
            List<String> strings = Files.readAllLines(Paths.get(xhtmlPathString), StandardCharsets.UTF_8);
            StringBuilder xhtmlContent = new StringBuilder();
            for (String string : strings) {
                xhtmlContent.append(string);
                xhtmlContent.append('\n');
            }
            return XhtmlParser.parse(state, xhtmlContent.toString());
        } catch (IOException e) {
            throw new JVMIOException("Could not read from '" + xhtmlPathString + "'", e);
        }
    }
}
