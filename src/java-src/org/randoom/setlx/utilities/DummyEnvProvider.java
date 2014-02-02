package org.randoom.setlx.utilities;

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
    public void outWrite(final String arg0) {
    }

    @Override
    public void errWrite(final String arg0) {
    }

    @Override
    public void promptForInput(final String arg0) {
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
    public String filterFileName(final String arg0) {
        return "";
    }

    @Override
    public String filterLibraryName(final String name) {
        return "";
    }

}
