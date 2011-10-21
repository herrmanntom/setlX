package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.utilities.Environment;

import java.util.LinkedList;
import java.util.List;

public class Block extends Statement {
    private List<Statement>  mStatements;

    public Block(){
        this(new LinkedList<Statement>());
    }

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
        String endl      = Environment.getEndl();
        int    stmntTabs = tabs;
        if (brackets) {
            stmntTabs += 1;
        }
        String result = "";
        if (brackets) {
            result += "{" + endl;
        }
        int count = 1;
        for (Statement stmnt: mStatements) {
            result += stmnt.toString(stmntTabs);
            if (count < mStatements.size()) {
                result += endl;
            }
            count++;
        }
        if (brackets) {
            result += endl + Environment.getTabs(tabs) + "}";
        }
        return result;
    }
}
