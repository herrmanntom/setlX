package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.JVMException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlClass;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlObject;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.State;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * webapi_parse_xml(xml_string) : Use javax parsers to parse given xml content into document and map it to setlx objects.
 */
public class PD_webapi_parse_xml extends PreDefinedProcedure {

    private final static ParameterDefinition XML_STRING = createParameter("xml_string");

    private final static String CLASS_CODE_NODE = "class webapi_xml_node(name, text_content, child_nodes) {\n" +
                                                  "    this.name := name;\n" +
                                                  "    this.text_content := text_content;\n" +
                                                  "    this.child_nodes := child_nodes;\n" +
                                                  "}";
    private       static SetlClass xmlNodeClass = null;

    /** Definition of the PreDefinedProcedure 'stat_beta' */
    public final static PreDefinedProcedure DEFINITION = new PD_webapi_parse_xml();

    private PD_webapi_parse_xml() {
        super();
        addParameter(XML_STRING);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value argument = args.get(XML_STRING);
        if (argument.isString() == SetlBoolean.FALSE || argument.size() == 0) {
            throw new IncompatibleTypeException("Could not '" + argument.getUnquotedString(state) + "' as xml");
        }
        String xmlString = argument.getUnquotedString(state);

        Document xmlDocument;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            xmlDocument = builder.parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new JVMException("Could not parse '" + argument.getUnquotedString(state) + "' as xml", e);
        }

        if (xmlNodeClass == null) {
            Block block = ParseSetlX.parseStringToBlock(state, CLASS_CODE_NODE);
            block.execute(state);
            xmlNodeClass = (SetlClass) state.findValue("webapi_xml_node");
        }


        Element documentElement = xmlDocument.getDocumentElement();

        SetlList childNodes = extractChildNodes(state, documentElement);

        SetlObject rootNode = newXmlNode(state, documentElement.getNodeName(), documentElement.getTextContent(), childNodes);

        return rootNode;
    }

    private SetlList extractChildNodes(State state, Element documentElement) {
        return new SetlList();
    }

    private SetlObject newXmlNode(State state, String nodeName, String textContent, SetlList childNodes) throws SetlException {
        List<Value> argumentValues = new ArrayList<>();
        argumentValues.add(new SetlString(nodeName));
        argumentValues.add(new SetlString(textContent));
        argumentValues.add(childNodes);
        return (SetlObject) xmlNodeClass.call(state, argumentValues, null, null, null);
    }
}
