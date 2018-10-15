package org.randoom.setlx.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

@SuppressWarnings("unused")
@Mojo(name = "writeVersion")
public class WriteVersionMojo extends AbstractMojo {
    @Parameter(property = "writeVersion.versionToWrite", required = true)
    private String versionToWrite;

    @Parameter(property = "writeVersion.buildToWrite", required = true)
    private String buildToWrite;

    @Parameter(property = "writeVersion.targetDirectory", required = true)
    private File targetDirectory;

    @Parameter(property = "writeVersion.targetClass", required = true)
    private String targetClass;

    @Parameter(property = "writeVersion.targetPackage", required = true)
    private String targetPackage;

    public void execute() throws MojoFailureException {
        validateVersionToWrite();
        validateBuildToWrite();
        validateTargetDirectory();
        validateTargetClass();
        validateTargetPackage();

        writeTargetClass(versionToWrite, targetDirectory, targetClass, targetPackage);
    }

    private void writeTargetClass(String versionToWrite, File targetDirectory, String targetClass, String targetPackage) throws MojoFailureException {
        File targetFile = buildTargetFile();
        try (
                FileOutputStream outputStream = new FileOutputStream(targetFile);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter writer = new BufferedWriter(outputStreamWriter)
        ) {
            writer.write(
                    "package " + targetPackage + ";\n" +
                    "\n" +
                    "/*package*/ class " + targetClass + " {\n" +
                    "    /*package*/ static String SETLX_SOURCE_VERSION = \"" + versionToWrite + "\";\n" +
                    "    /*package*/ static String SETLX_SOURCE_BUILD = \"" + buildToWrite + "\";\n" +
                    "}\n"
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

    private void validateVersionToWrite() throws MojoFailureException {
        if (versionToWrite == null || versionToWrite.isEmpty()) {
            throw new MojoFailureException("The parameter versionToWrite is not set.");
        }
    }

    private void validateBuildToWrite() throws MojoFailureException {
        if (buildToWrite == null || buildToWrite.isEmpty()) {
            throw new MojoFailureException("The parameter buildToWrite is not set.");
        }
    }
}
