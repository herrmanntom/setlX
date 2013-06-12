package org.randoom.setlx.utilities;

// This class lets all calls die
public class DummyEnvProvider implements EnvironmentProvider {

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
    public int getMaxStackSize() {
        return -1;
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
