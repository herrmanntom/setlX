package org.randoom.setlx.utilities;

import net.iharder.Base64;
import org.randoom.setlx.exceptions.JVMIOException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class EncodedFilesWriter {
    public static List<String> write(State state, String baseDirectory, Set<String> directoriesToSkip, Map<String, String> base64EncodedFiles) throws JVMIOException {
        return writeFiles(state, false, baseDirectory, directoriesToSkip, decodeFiles(base64EncodedFiles));
    }

    public static List<String> writeLibraryFiles(State state, Map<String, String> base64EncodedFiles) throws JVMIOException {
        return writeFiles(state, true, null, null, decodeFiles(base64EncodedFiles));
    }

    private static List<String> writeFiles(State state, boolean isLibrary, String baseDirectory, Set<String> directoriesToSkip, Map<String, String> decodedFiles) throws JVMIOException {
        List<String> filesWritten = new ArrayList<>(decodedFiles.size());

        for (Entry<String, String> decodedFile : decodedFiles.entrySet()) {
            String filePath = decodedFile.getKey();
            if (isExcluded(filePath, directoriesToSkip)) {
                continue;
            }
            filePath = filePath.replace("/", File.separator);

            String nameOfFileToWrite;
            if (isLibrary) {
                nameOfFileToWrite = state.filterLibraryName(filePath);
            } else {
                if (baseDirectory != null) {
                    filePath = baseDirectory + File.separator + filePath;
                }
                nameOfFileToWrite = state.filterFileName(filePath);
            }
            File fileToWrite = new File(nameOfFileToWrite);
            if (fileToWrite.getParentFile() != null) {
                //noinspection ResultOfMethodCallIgnored
                fileToWrite.getParentFile().mkdirs();
            }
            try (
                    FileOutputStream outputStream = new FileOutputStream(fileToWrite);
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

    private static boolean isExcluded(String filePath, Set<String> directoriesToSkip) {
        if (directoriesToSkip == null || directoriesToSkip.isEmpty()) {
            return false;
        }
        for (String skippedDirectory : directoriesToSkip) {
            if (filePath.startsWith(skippedDirectory + "/")) {
                return true;
            }
        }
        return false;
    }

    private static Map<String, String> decodeFiles(Map<String, String> base64EncodedFiles) throws JVMIOException {
        Map<String, String> decodedFiles = new TreeMap<>();
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
