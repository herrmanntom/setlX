package org.randoom.setlx.utilities;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleXmlSerializer;
import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class XhtmlParser {
    public static SetlObject parse(State state, String xhtmlString) throws SetlException {
        CleanerProperties props = new CleanerProperties();

        // set some properties to non-default values
        props.setTranslateSpecialEntities(true);
        props.setTransResCharsToNCR(true);
        props.setOmitComments(true);

        StringWriter writer = new StringWriter();
        try {
            new SimpleXmlSerializer(props).write(new HtmlCleaner(props).clean(xhtmlString), writer, "utf-8");
        } catch (IOException e) {
            throw new JVMIOException("Could not serialize cleaned HTML: " + e.getMessage(), e);
        }

        return XmlParser.parse(state, new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8)));
    }
}
