package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.JVMException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlClass;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlObject;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {

    private static final String CLASS_NAME = "webapi_xml_node";

    private final static String CLASS_CODE_NODE = "class " + CLASS_NAME + "(node_name, attributes, text_content, child_nodes) {\n" +
                                                  "    this.node_name := node_name;\n" +
                                                  "    this.attributes := attributes;\n" +
                                                  "    this.text_content := text_content;\n" +
                                                  "    this.child_nodes := child_nodes;\n" +
                                                  "}";

    public static SetlObject parse(State state, InputStream inputStream) throws SetlException {
        Value classCandidate = state.findValue(CLASS_NAME);
        if (classCandidate == Om.OM) {
            Block block = ParseSetlX.parseStringToBlock(state, CLASS_CODE_NODE);
            block.execute(state);
            classCandidate = state.findValue(CLASS_NAME);
        }

        if (classCandidate.isClass() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("Value bound to variable '" + CLASS_NAME + "' is not a class: " + classCandidate.toString(state));
        }

        Document xmlDocument;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            xmlDocument = builder.parse(inputStream);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new JVMException("Could not parse xml: " + e.getMessage(), e);
        }

        return mapXmlNode(state, (SetlClass) classCandidate, xmlDocument.getDocumentElement());
    }

    private static SetlObject mapXmlNode(State state, SetlClass xmlNodeClass, Node node) throws SetlException {
        List<Value> argumentValues = new ArrayList<>();
        argumentValues.add(new SetlString(node.getNodeName()));
        argumentValues.add(mapAttributes(state, node));
        argumentValues.add(new SetlString(node.getTextContent()));
        argumentValues.add(mapChildNodes(state, xmlNodeClass, node));
        return (SetlObject) xmlNodeClass.call(state, argumentValues, null, null, null);
    }

    private static SetlSet mapAttributes(State state, Node node) {
        SetlSet attributes = new SetlSet();
        NamedNodeMap attributeMap = node.getAttributes();
        for (int i = 0; i < attributeMap.getLength(); ++i) {
            Node attribute = attributeMap.item(i);

            SetlList attributePair = new SetlList();
            attributePair.addMember(state, new SetlString(attribute.getNodeName()));
            attributePair.addMember(state, new SetlString(attribute.getNodeValue()));

            attributes.addMember(state, attributePair);
        }
        return attributes;
    }

    private static SetlList mapChildNodes(State state, SetlClass xmlNodeClass, Node node) throws SetlException {
        SetlList children = new SetlList();

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node child = childNodes.item(i);
            if (!"#text".equals(child.getNodeName())) {
                children.addMember(state, mapXmlNode(state, xmlNodeClass, child));
            }
        }

        return children;
    }
}
