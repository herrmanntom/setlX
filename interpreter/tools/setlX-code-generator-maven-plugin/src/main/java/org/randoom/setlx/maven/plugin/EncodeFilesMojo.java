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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("unused")
@Mojo(name = "encodeFiles")
public class EncodeFilesMojo extends AbstractMojo {

    @Parameter(property = "encodeFiles.sourceDirectory")
    private File sourceDirectory;

    @Parameter(property = "encodeFiles.targetDirectory")
    private File targetDirectory;

    @Parameter(property = "encodeFiles.targetClass")
    private String targetClass;

    @Parameter(property = "encodeFiles.targetPackage")
    private String targetPackage;

    public void execute() throws MojoFailureException {
        validateSourceDirectory();
        validateTargetDirectory();
        validateTargetClass();
        validateTargetPackage();

        writeTargetClass(
                getEncodedFiles()
        );
    }

    private void writeTargetClass(Map<String, String> encodedFiles) throws MojoFailureException {
        File targetFile = getTargetFile();
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
                    "public class " + targetClass + " {\n" +
                    "    private static final Map<String, String> base64EncodedFiles = new HashMap<>();\n" +
                    "    static {\n"
            );
            for (Entry<String, String> encodedFile : encodedFiles.entrySet()) {
                writer.write(
                        "        base64EncodedFiles.put(\"" + encodedFile.getKey() + "\", \"" + encodedFile.getValue() + "\");\n"
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

    private File getTargetFile() {
        File targetFile = targetDirectory;
        String[] packages = targetPackage.split("\\.");
        for (String aPackage : packages) {
            targetFile = new File(targetFile, aPackage);
        }
        //noinspection ResultOfMethodCallIgnored
        targetFile.mkdirs();
        return new File(targetFile, targetClass + ".java");
    }

    private Map<String, String> getEncodedFiles() throws MojoFailureException {
        Map<String, String> encodedFiles = new HashMap<>();
        //noinspection ConstantConditions
        for (File file : sourceDirectory.listFiles()) {
            try {
                String fileContentAsBase64 = Base64.encodeFromFile(file.getAbsolutePath());
                encodedFiles.put(
                        file.getName(),
                        fileContentAsBase64
                );
            } catch (IOException e) {
                throw new MojoFailureException("Source file '" + file.getAbsolutePath() + "' cannot be read.", e);
            }
        }
        return encodedFiles;
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
