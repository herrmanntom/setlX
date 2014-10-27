package org.randoom.setlx.utilities;

import java.util.List;

/**
 * This "environment" lets all calls die silently.
 */
public class DummyEnvProvider implements EnvironmentProvider {

    /**
     * Singleton dummy environment provider.
     */
    public final static DummyEnvProvider DUMMY = new DummyEnvProvider();

    private DummyEnvProvider() {
        /* do nothing */
    }

    @Override
    public boolean inReady() {
        return false;
    }

    @Override
    public String inReadLine() {
        return "";
    }

    @Override
    public void outWrite(final String msg) {
    }

    @Override
    public void errWrite(final String msg) {
    }

    @Override
    public void promptForInput(final String msg) {
    }

    @Override
    public String promptSelectionFromAnswers(final String question, final List<String> answers) {
        return answers.get(0); // may fail if called incorrectly
    }

    @Override
    public String getTab() {
        return "";
    }

    @Override
    public String getEndl() {
        return "";
    }

    @Override
    public String getOsID() {
        return "";
    }

    @Override
    public String filterFileName(final String fileName) {
        return "";
    }

    @Override
    public String filterLibraryName(final String name) {
        return "";
    }

    @Override
    public int getMaximumNumberOfThreads() {
        return 0;
    }

    @Override
    public int getStackSizeWishInKb() {
        return 0;
    }

}
