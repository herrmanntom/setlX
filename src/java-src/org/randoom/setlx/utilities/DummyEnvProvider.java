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
    public void outWrite(String arg0) {
    }

    @Override
    public void errWrite(String arg0) {
    }

    @Override
    public void promptForInput(String arg0) {
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
    public String filterFileName(String arg0) {
        return "";
    }

    @Override
    public String filterLibraryName(String name) {
        return "";
    }

}
