package org.randoom.setlx.utilities;

import net.iharder.Base64;
import org.randoom.setlx.exceptions.JVMIOException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EncodedFilesWriter {
    public static List<String> write(State state, File baseDirectory, Map<String, String> base64EncodedFiles) throws JVMIOException {
        return writeFiles(state, false, baseDirectory, decodeFiles(base64EncodedFiles));
    }

    public static List<String> writeLibraryFiles(State state, Map<String, String> base64EncodedFiles) throws JVMIOException {
        return writeFiles(state, true, null, decodeFiles(base64EncodedFiles));
    }

    private static List<String> writeFiles(State state, boolean isLibrary, File baseDirectory, Map<String, String> decodedFiles) throws JVMIOException {
        List<String> filesWritten = new ArrayList<>(decodedFiles.size());

        for (Entry<String, String> decodedFile : decodedFiles.entrySet()) {
            String nameOfFileToWrite;
            if (isLibrary) {
                nameOfFileToWrite = state.filterLibraryName(decodedFile.getKey());
            } else {
                String filePath = decodedFile.getKey();
                if (baseDirectory != null) {
                    File path = baseDirectory;
                    for (String part : filePath.split("/")) {
                        path = new File(path, part);
                    }
                    filePath = path.getPath();
                }
                nameOfFileToWrite = state.filterFileName(filePath);
            }
            try (
                    FileOutputStream outputStream = new FileOutputStream(nameOfFileToWrite);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                    BufferedWriter writer = new BufferedWriter(outputStreamWriter)
            ) {
                writer.write(decodedFile.getValue());
            } catch (IOException e) {
                throw new JVMIOException("Could not write files.", e);
            }

            filesWritten.add(nameOfFileToWrite);
        }
        return filesWritten;
    }

    private static Map<String, String> decodeFiles(Map<String, String> base64EncodedFiles) throws JVMIOException {
        Map<String, String> decodedFiles = new HashMap<>();
        try {
            for (Entry<String, String> encodedFile : base64EncodedFiles.entrySet()) {
                decodedFiles.put(encodedFile.getKey(), new String(Base64.decode(encodedFile.getValue()), "UTF-8"));
            }
        } catch (IOException e) {
            throw new JVMIOException("Could not decode files.", e);
        }
        return decodedFiles;
    }
}
