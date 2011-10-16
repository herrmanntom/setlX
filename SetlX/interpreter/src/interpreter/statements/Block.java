package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.utilities.Environment;

import java.util.List;

public class Block extends Statement {
    private List<Statement>  mStatements;

    public Block(List<Statement> statements){
        mStatements = statements;
    }

    public void execute() throws SetlException {
        for (Statement stmnt : mStatements) {
            stmnt.execute();
        }
    }

    public String toString(int tabs) {
        return toString(tabs, false);
    }

    public String toString(int tabs, boolean brackets) {
        int stmntTabs  = tabs;
        if ( ! brackets) {
            stmntTabs += 1;
        }
        String result = "";
        if (brackets) {
            result += "{";
        }
        for (Statement stmnt: mStatements) {
            result += stmnt.toString(stmntTabs) + Environment.getEndl();
        }
        if (brackets) {
            result += Environment.getTabs(tabs) + "}";
        }
        return result;
    }
}
