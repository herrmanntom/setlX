package org.randoom.setlx.pc.ui;

import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.utilities.EnvironmentProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

/**
 * This implementation provides access to the I/O mechanisms of PCs.
 */
public class PcEnvProvider implements EnvironmentProvider {

    private final static boolean IS_32_BIT_VM = "32".equals(System.getProperty("sun.arch.data.model"));
    private final static String  TAB          = "\t";

    private final String         endl;
    private final String         osName;
    /**
     * Path where the library files are expected to reside.
     */
    private       String         libraryPath;

    // buffered reader for stdin
    private       BufferedReader stdInReader;

    /**
     * Create new PcEnvProvider.
     *
     * @param libraryPath Path where the library files are expected to reside.
     */
    public PcEnvProvider(final String libraryPath) {
        this.endl        = System.getProperty("line.separator");
        this.osName      = System.getProperty("os.name");
        this.libraryPath = libraryPath;
        this.stdInReader = null;
    }

    /**
     * Set new library path.
     *
     * @param libraryPath Path where the library files are expected to reside.
     */
    public void setlibraryPath(final String libraryPath) {
        this.libraryPath = libraryPath;
    }

    /**
     * Get current StdIn reader.
     * @return StdIn reader.
     */
    private BufferedReader getStdIn() {
        if (stdInReader == null) {
            stdInReader = new BufferedReader(new InputStreamReader(System.in));
        }
        return stdInReader;
    }

    /* interface functions */

    @Override
    public boolean  inReady() throws JVMIOException {
        try {
            return getStdIn().ready();
        } catch (final IOException ioe) {
            throw new JVMIOException("Unable to open stdIn!", ioe);
        }
    }
    @Override
    public String   inReadLine() throws JVMIOException {
        try {
                   // line is read and returned without termination character(s)
            return getStdIn().readLine();
        } catch (final IOException ioe) {
            throw new JVMIOException("Unable to open stdIn!", ioe);
        }
    }

    @Override
    public void     outWrite(final String msg) {
        System.out.print(msg);
    }

    @Override
    public void     errWrite(final String msg) {
        System.err.print(msg);
    }

    @Override
    public void    promptForInput(final String msg) {
        System.out.print(msg);
        System.out.flush();
    }

    @Override
    public String  promptSelectionFromAnswers(final String question, final List<String> answers) throws JVMIOException {
        final StringBuilder prompt = new StringBuilder(question);
        if (answers.size() > 1) {
            int nAnswer = 0;
            for (final String answer : answers) {
                prompt.append("\n");
                if (answers.size() >= 10 && nAnswer < 9) {
                    prompt.append(" ");
                }
                prompt.append(++nAnswer);
                prompt.append(") ");
                prompt.append(answer);
            }
            prompt.append("\n");
            promptForInput(prompt.toString());
            final String promptEnd = "Please enter a number between 1 and " + nAnswer + ": ";
            while (true) {
                promptForInput(promptEnd);
                String input = inReadLine();
                try {
                    final int n = Integer.valueOf(input);
                    if (n >= 1 && n <= nAnswer) {
                        return answers.get(n - 1);
                    }
                } catch (final Exception e) {
                    // don't care
                }
            }
        } else /* if (answers.size() == 1) */ {
            final String answer = answers.get(0);
            prompt.append("\n[Enter] to confirm");
            if (answer.length() > 0) {
                prompt.append(" '");
                prompt.append(answer);
                prompt.append("'");
            }
            prompt.append(".");
            promptForInput(prompt.toString());
            inReadLine();
            return answer;
        }
    }

    @Override
    public String   getTab() {
        return TAB;
    }
    @Override
    public String   getEndl() {
        return endl;
    }

    @Override
    public String   getOsID() {
        return osName;
    }

    @Override
    public String   filterFileName(final String fileName) {
        return fileName; // not required on PCs
    }

    @Override
    public String   filterLibraryName(String name) {
        name = name.trim();
        if (name.length() >= 1 && name.charAt(0) != '/' && (name.length() < 2 || name.charAt(1) != ':')) {
            return new File(libraryPath).toPath().resolve(name).toString();
        }
        return name;
    }

    @Override
    public int getStackSizeWishInKb() {
        if (IS_32_BIT_VM) {
            return 86 * 1024;
        }
        return 172 * 1024;
    }

    @Override
    public int getMediumStackSizeWishInKb() {
        return 0; // use default
    }

    @Override
    public int getSmallStackSizeWishInKb() {
        if (IS_32_BIT_VM) {
            return 256;
        }
        return 512;
    }
}

