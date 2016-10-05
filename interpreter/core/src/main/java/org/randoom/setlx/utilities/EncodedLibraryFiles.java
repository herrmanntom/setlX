package org.randoom.setlx.utilities;

import net.iharder.Base64;
import org.randoom.setlx.exceptions.JVMIOException;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EncodedLibraryFiles {
    private static final Map<String, String> base64EncodedLibraryFiles = new HashMap<>();
    static {
        base64EncodedLibraryFiles.put("debugUtilities.stlx", "Y2xhc3MgdHJhY2VyKCkgewogICAgLy8gZGljdGlvbmFyeSBmb3IgdGhlIG9yaWdpbmFsIHByb2NlZHVyZSBkZWZpbml0aW9ucwogICAgbVN0b3JlZFByb2NlZHVyZXMgOj0ge307CiAgICAKICAgIC8vIE1vZGlmeSB0aGUgZ2l2ZW4gZnVuY3Rpb24gc28gdGhhdCB0aGUgaW52b2NhdGlvbiBvZiB0aGUgZnVuY3Rpb24gaXMgdHJhY2VkCiAgICB0cmFjZSA6PSBwcm9jZWR1cmUoZnVuY3Rpb24sIGZ1bmN0aW9uTmFtZSkgewogICAgICAgIG1TdG9yZWRQcm9jZWR1cmVzW2Z1bmN0aW9uTmFtZV0gOj0gZnVuY3Rpb247CiAgICAgICAgdHJhY2VkRnVuY3Rpb24gOj0gY2xvc3VyZSgqYXJncykgewogICAgICAgICAgICBhcmdzU3RyaW5nIDo9IGpvaW4oYXJncywgIiwgIik7CiAgICAgICAgICAgIHByaW50KCJjYWxsaW5nICRmdW5jdGlvbk5hbWUkKCRhcmdzU3RyaW5nJCkiKTsKICAgICAgICAgICAgcmVzdWx0IDo9IGZ1bmN0aW9uKCphcmdzKTsKICAgICAgICAgICAgcHJpbnQoIiRmdW5jdGlvbk5hbWUkKCRhcmdzU3RyaW5nJCkgPSAkcmVzdWx0JCIpOwogICAgICAgICAgICByZXR1cm4gcmVzdWx0OwogICAgICAgIH07CiAgICAgICAgcmV0dXJuIHRyYWNlZEZ1bmN0aW9uOwogICAgfTsKICAgIHVudHJhY2UgOj0gcHJvY2VkdXJlKGZ1bmN0aW9uTmFtZSkgewogICAgICAgIHJldHVybiBtU3RvcmVkUHJvY2VkdXJlc1tmdW5jdGlvbk5hbWVdOwogICAgfTsKfQoKY2xhc3MgcHJvZmlsZXIoKSB7CiAgICBtVGltZXMgICAgICAgICAgICA6PSB7fTsKICAgIG1TdG9yZWRQcm9jZWR1cmVzIDo9IHt9OwogICAgCiAgICAvLyBNb2RpZnkgdGhlIGdpdmVuIGZ1bmN0aW9uIHNvIHRoYXQgdGhlIGV4ZWN1dGlvbiB0aW1lcyBhcmUgcmVjb3JkZWQuCiAgICBwcm9maWxlIDo9IGNsb3N1cmUoZnVuY3Rpb24sIGZ1bmN0aW9uTmFtZSkgewogICAgICAgIG1UaW1lc1tmdW5jdGlvbk5hbWVdIDo9IDA7CiAgICAgICAgcHJvZmlsZWRGdW5jdGlvbiA6PSBjbG9zdXJlKCphcmdzKSB7CiAgICAgICAgICAgIHN0YXJ0ICA6PSBub3coKTsKICAgICAgICAgICAgcmVzdWx0IDo9IGZ1bmN0aW9uKCphcmdzKTsKICAgICAgICAgICAgc3RvcCAgIDo9IG5vdygpOwogICAgICAgICAgICB0aW1lICAgOj0gc3RvcCAtIHN0YXJ0OwogICAgICAgICAgICBtVGltZXNbZnVuY3Rpb25OYW1lXSArPSB0aW1lOwogICAgICAgICAgICByZXR1cm4gcmVzdWx0OwogICAgICAgIH07CiAgICAgICAgcmV0dXJuIHByb2ZpbGVkRnVuY3Rpb247CiAgICB9OwogICAgdW5wcm9maWxlIDo9IHByb2NlZHVyZShmdW5jdGlvbk5hbWUpIHsKICAgICAgICByZXR1cm4gbVN0b3JlZFByb2NlZHVyZXNbZnVuY3Rpb25OYW1lXTsKICAgIH07Cn0KCgo=");
        base64EncodedLibraryFiles.put("termUtilities.stlx", "Ly8gVGFrZSBhIHRlcm0gcHJvZHVjZWQgYnkgdGhlIGZ1bmN0aW9uIHBhcnNlIGFuZCB0cmFuc2Zvcm0gaXQgaW50bwovLyBhIHRlcm0gdGhhdCBoYXMgYSBtdWNoIHNpbXBsZXIgc3RydWN0dXJlLgp0b1Rlcm0gOj0gcHJvY2VkdXJlKHQpIHsKICAgIGlmIChpc1ZhcmlhYmxlKHQpIHx8ICFpc1Rlcm0odCkpIHsKICAgICAgICByZXR1cm4gdDsKICAgIH0KICAgIGZjdFN5bWJvbCA6PSBmY3QodCk7CiAgICBhcmd1bWVudHMgOj0gYXJncyh0KTsKICAgIGlmIChmY3RTeW1ib2wgPT0gIkBAQGNhbGwiKSB7CiAgICAgICAgZmN0TmFtZSAgICAgICAgIDo9IGFyZ3MoYXJndW1lbnRzWzFdKVsxXTsKICAgICAgICB0cmFuc2Zvcm1lZEFyZ3MgOj0gdG9UZXJtTGlzdChhcmd1bWVudHNbMl0pOwogICAgICAgIGV4dHJhQXJncyAgICAgICA6PSBhcmd1bWVudHNbM107CiAgICAgICAgaWYgKGV4dHJhQXJncyA9PSAibmlsIikgewogICAgICAgICAgICByZXR1cm4gbWFrZVRlcm0oZmN0TmFtZSwgdHJhbnNmb3JtZWRBcmdzKTsKICAgICAgICB9IGVsc2UgewogICAgICAgICAgICB0cmFuc2Zvcm1lZEFyZ3MgKz0gdG9UZXJtTGlzdChleHRyYUFyZ3MpOwogICAgICAgICAgICByZXR1cm4gbWFrZVRlcm0oZmN0TmFtZSwgdHJhbnNmb3JtZWRBcmdzKTsKICAgICAgICB9CiAgICB9CiAgICBhcmdMaXN0IDo9IHRvVGVybUxpc3QoYXJndW1lbnRzKTsKICAgIHJldHVybiBtYWtlVGVybShmY3RTeW1ib2wsIGFyZ0xpc3QpOyAKfTsKCnRvVGVybUxpc3QgOj0gcHJvY2VkdXJlKHRzKSB7CiAgICBtYXRjaCAodHMpIHsKICAgICAgICBjYXNlIFtdICAgICAgIDogcmV0dXJuIFtdOwogICAgICAgIGNhc2UgWyB0IHwgciBdOiByZXR1cm4gWyB0b1Rlcm0odCkgfCB0b1Rlcm1MaXN0KHIpIF07CiAgICB9CiAgICBhYm9ydCgidG9UZXJtKCkgZG9lcyBub3QgZnVsbHkgc3VwcG9ydCBjYWxscyB1c2luZyBsaXN0IGV4cGFuc2lvbi4gRS5nLiBhIDo9IFsxXTsgc2luKCphKTsiKTsKfTsKCnBhcnNlVGVybSA6PSBwcm9jZWR1cmUocykgewogICAgcmV0dXJuIHRvVGVybShwYXJzZShzKSk7Cn07CgovLyBUYWtlIGEgc2ltcGxlIHRlcm0gcHJvZHVjZWQgYnkgdGhlIGZ1bmN0aW9uIHRvVGVybSBhbmQgdHJhbnNmb3JtIGl0Ci8vIGJhY2sgaW50byBhIHRlcm0gdGhhdCByZXByZXNlbnRzIHRoZSBpbnRlcm5hbCBzdHJ1Y3R1cmUgcmVxdWlyZWQgYnkKLy8gc2V0bFguIFRoaXMgd2F5IGl0IGNhbiBiZSBldmFsdWF0ZWQgdXNpbmcgZXZhbFRlcm0oKS4KZnJvbVRlcm0gOj0gcHJvY2VkdXJlKHQpIHsKICAgIGlmIChpc1ZhcmlhYmxlKHQpIHx8ICFpc1Rlcm0odCkpIHsKICAgICAgICByZXR1cm4gdDsKICAgIH0KICAgIGZjdFN5bWJvbCA6PSBmY3QodCk7CiAgICBhcmd1bWVudHMgOj0gZnJvbVRlcm1MaXN0KGFyZ3ModCkpOwoKICAgIGlmICghc3RhcnRzV2l0aChmY3RTeW1ib2wsICJAQEAiKSkgewogICAgICAgIHJldHVybiBtYWtlVGVybSgKICAgICAgICAgICAgIkBAQGNhbGwiLAogICAgICAgICAgICBbCiAgICAgICAgICAgICAgICBtYWtlVGVybSgiQEBAdmFyaWFibGUiLCBbZmN0U3ltYm9sXSksCiAgICAgICAgICAgICAgICBhcmd1bWVudHMsCiAgICAgICAgICAgICAgICAibmlsIgogICAgICAgICAgICBdCiAgICAgICAgKTsKICAgIH0KCiAgICByZXR1cm4gbWFrZVRlcm0oZmN0U3ltYm9sLCBhcmd1bWVudHMpOyAKfTsKCmZyb21UZXJtTGlzdCA6PSBwcm9jZWR1cmUodHMpIHsKICAgIG1hdGNoICh0cykgewogICAgICAgIGNhc2UgW10gICAgICAgOiByZXR1cm4gW107CiAgICAgICAgY2FzZSBbIHQgfCByIF06IHJldHVybiBbIGZyb21UZXJtKHQpIHwgZnJvbVRlcm1MaXN0KHIpIF07CiAgICB9CiAgICBhYm9ydCgiZnJvbVRlcm0oKSBkb2VzIG5vdCBzdXBwb3J0IHRoaXMgdGVybSIpOwp9OwoKCgo=");
    }

    public static List<String> write(State state) throws JVMIOException {
        return writeFiles(state, EncodedLibraryFiles.getLibraryFiles());
    }

    private static List<String> writeFiles(State state, Map<String, String> libraryFiles) throws JVMIOException {
        List<String> filesWritten = new ArrayList<>(libraryFiles.size());

        for (Entry<String, String> libraryFile : libraryFiles.entrySet()) {
            String nameOfLibraryToWrite = state.filterLibraryName(libraryFile.getKey());
            try (
                    FileOutputStream outputStream = new FileOutputStream(nameOfLibraryToWrite);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                    BufferedWriter writer = new BufferedWriter(outputStreamWriter)
            ) {
                writer.write(libraryFile.getValue());
            } catch (IOException e) {
                throw new JVMIOException("Could not write library files.", e);
            }

            filesWritten.add(nameOfLibraryToWrite);
        }
        return filesWritten;
    }

    private static Map<String, String> getLibraryFiles() throws JVMIOException {
        Map<String, String> libraryFiles = new HashMap<>();
        try {
            for (Entry<String, String> libraryFile : base64EncodedLibraryFiles.entrySet()) {
                libraryFiles.put(libraryFile.getKey(), new String(Base64.decode(libraryFile.getValue()), "UTF-8"));
            }
        } catch (IOException e) {
            throw new JVMIOException("Could not decode library files.", e);
        }
        return libraryFiles;
    }
}
