package org.randoom.setlx.maven.plugin;

import net.iharder.Base64;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

@SuppressWarnings("unused")
@Mojo(name = "encodeFiles")
public class EncodeFilesMojo extends AbstractMojo {
    @SuppressWarnings("FieldCanBeLocal")
    private final int MAX_LINE_LENGTH = 512;

    @Parameter(property = "encodeFiles.sourceDirectory", required = true)
    private File sourceDirectory;

    @Parameter(property = "encodeFiles.excludedFileEndings")
    private List<String> excludedFileEndings;

    @Parameter(property = "encodeFiles.targetDirectory", required = true)
    private File targetDirectory;

    @Parameter(property = "encodeFiles.targetClass", required = true)
    private String targetClass;

    @Parameter(property = "encodeFiles.targetPackage", required = true)
    private String targetPackage;

    public void execute() throws MojoFailureException {
        validateSourceDirectory();
        validateTargetDirectory();
        validateTargetClass();
        validateTargetPackage();

        writeTargetClass(
                encodeFiles("", sourceDirectory.listFiles())
        );
    }

    private void writeTargetClass(Map<String, String> encodedFiles) throws MojoFailureException {
        File targetFile = buildTargetFile();
        try (
                FileOutputStream outputStream = new FileOutputStream(targetFile);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter writer = new BufferedWriter(outputStreamWriter)
        ) {
            writer.write(
                    "package " + targetPackage + ";\n" +
                    "\n" +
                    "import java.util.HashMap;\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "@SuppressWarnings(\"StringBufferReplaceableByString\")\n" +
                    "public class " + targetClass + " {\n" +
                    "    private static final Map<String, String> base64EncodedFiles = new HashMap<>();\n" +
                    "    static {\n"
            );
            for (Entry<String, String> encodedFile : encodedFiles.entrySet()) {
                writer.write(
                        "        base64EncodedFiles.put(\n" +
                        "                \"" + encodedFile.getKey() + "\",\n" +
                        "                new StringBuilder()\n"
                );
                String encodedFileContents = encodedFile.getValue();
                while (encodedFileContents.length() > MAX_LINE_LENGTH) {
                    writer.write(
                            "                        .append(\"" + encodedFileContents.substring(0, MAX_LINE_LENGTH) + "\")\n"
                    );
                    encodedFileContents = encodedFileContents.substring(MAX_LINE_LENGTH);
                }
                writer.write(
                        "                        .append(\"" + encodedFileContents + "\")\n"
                );
                writer.write(
                        "                        .toString()\n" +
                        "        );\n"
                );
            }
            writer.write(
                    "    }\n" +
                    "\n" +
                    "    public static Map<String, String> getBase64EncodedFiles() {\n" +
                    "        return new HashMap<>(base64EncodedFiles);\n" +
                    "    }\n" +
                    "}\n" +
                    ""
            );
        } catch (IOException e) {
            throw new MojoFailureException("Target class '" + targetFile.getAbsolutePath() + "' cannot be written.", e);
        }
    }

    private File buildTargetFile() {
        File targetFile = targetDirectory;
        String[] packages = targetPackage.split("\\.");
        for (String aPackage : packages) {
            targetFile = new File(targetFile, aPackage);
        }
        //noinspection ResultOfMethodCallIgnored
        targetFile.mkdirs();
        return new File(targetFile, targetClass + ".java");
    }

    private Map<String, String> encodeFiles(String filePrefix, File[] files) throws MojoFailureException {
        Map<String, String> encodedFiles = new TreeMap<>();
        //noinspection ConstantConditions
        for (File file : files) {
            if (file.isFile() && isIncluded(file)) {
                try {
                    String fileContentAsBase64 = Base64.encodeFromFile(file.getAbsolutePath());
                    encodedFiles.put(
                            filePrefix + file.getName(),
                            fileContentAsBase64
                    );
                } catch (IOException e) {
                    throw new MojoFailureException("Source file '" + file.getAbsolutePath() + "' cannot be read.", e);
                }
            } else if (file.isDirectory()){
                encodedFiles.putAll(
                        encodeFiles(
                                filePrefix + file.getName() + "/",
                                file.listFiles()
                        )
                );
            }
        }
        return encodedFiles;
    }

    private boolean isIncluded(File file) {
        if (excludedFileEndings == null || excludedFileEndings.isEmpty()) {
            return true;
        }
        for (String suffix : excludedFileEndings) {
            if (file.getName().endsWith(suffix)) {
                return false;
            }
        }
        return true;
    }

    private void validateTargetPackage() throws MojoFailureException {
        if (targetPackage == null || targetPackage.isEmpty()) {
            throw new MojoFailureException("The parameter targetPackage is not set.");
        }
    }

    private void validateTargetClass() throws MojoFailureException {
        if (targetClass == null) {
            throw new MojoFailureException("The parameter targetClass is not set.");
        }
    }

    private void validateTargetDirectory() throws MojoFailureException {
        if (targetDirectory == null) {
            throw new MojoFailureException("The parameter targetDirectory is not set.");
        }
        if (!targetDirectory.isDirectory() || !targetDirectory.canWrite()) {
            throw new MojoFailureException("Target directory '" + targetDirectory.getAbsolutePath() + "' cannot be modified.");
        }
    }

    private void validateSourceDirectory() throws MojoFailureException {
        if (sourceDirectory == null) {
            throw new MojoFailureException("The parameter sourceDirectory is not set.");
        }
        if (!sourceDirectory.isDirectory() || !sourceDirectory.canRead()) {
            throw new MojoFailureException("Source directory '" + sourceDirectory.getAbsolutePath() + "' cannot be read.");
        }
        //noinspection ConstantConditions
        if (sourceDirectory.listFiles() == null || sourceDirectory.listFiles().length < 1) {
            throw new MojoFailureException("Source directory '" + sourceDirectory.getAbsolutePath() + "' is empty.");
        }
    }
}
