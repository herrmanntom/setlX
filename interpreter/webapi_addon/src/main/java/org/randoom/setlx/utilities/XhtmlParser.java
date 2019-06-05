package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlObject;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class XhtmlParser {

    public static SetlObject parse(State state, String xhtmlString) throws SetlException {
        xhtmlString = xhtmlString.replace("&nbsp;", "&#160;");
        xhtmlString = xhtmlString.replaceAll("<\\s*br\\s*>;", "<br />");

        if (!xhtmlString.contains("</img>") && !xhtmlString.matches(".*<\\s*img([^>]*)/>.*")) {
            xhtmlString = xhtmlString.replaceAll("<\\s*img([^>]*)>", "<img$1/>");
        }

        return XmlParser.parse(state, new ByteArrayInputStream(xhtmlString.getBytes(StandardCharsets.UTF_8)));
    }
}
