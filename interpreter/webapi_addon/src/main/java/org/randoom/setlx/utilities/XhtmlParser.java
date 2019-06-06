package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlObject;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class XhtmlParser {
    private final static Pattern PROPER_IMO_TAG = Pattern.compile(".*<\\s*img([^>]*)/>.*");

    public static SetlObject parse(State state, String xhtmlString) throws SetlException {
        xhtmlString = xhtmlString.replace("&nbsp;", "&#160;");
        xhtmlString = xhtmlString.replaceAll("<\\s*br\\s*>;", "<br />");

        if (!xhtmlString.contains("</img>") && !PROPER_IMO_TAG.matcher(xhtmlString).find()) {
            xhtmlString = xhtmlString.replaceAll("<\\s*img([^>]*)>", "<img$1/>");
        }

        return XmlParser.parse(state, new ByteArrayInputStream(xhtmlString.getBytes(StandardCharsets.UTF_8)));
    }
}
