package interpreter.statements;

import interpreter.Environment;
import interpreter.exceptions.ContinueException;
import interpreter.exceptions.ExitException;
import interpreter.exceptions.SetlException;

import java.util.List;

public class Loop extends Statement {
    private List<Statement> mStmntList;

    public Loop(List<Statement> stmntList) {
        mStmntList = stmntList;
    }

    public void execute() throws SetlException {
        while(true){
            try{
                for(Statement s : mStmntList){
                    s.execute();
                }
            } catch (ContinueException e) {
                continue;
            } catch (ExitException e) {
                break;
            }
        }
    }

    public String toString(int tabs) {
        String endl = " ";
        if (Environment.isPrintVerbose()) {
            endl = "\n";
        }
        String result = Environment.getTabs(tabs) + "loop" + endl;
        for (Statement stmnt: mStmntList) {
            result += stmnt.toString(tabs + 1) + endl;
        }
        result += Environment.getTabs(tabs) + "end loop;";
        return result;
    }
}
