package interpreter;

import interpreter.exceptions.SetlException;
import interpreter.statements.Statement;
import interpreter.functions.PreDefinedFunction;
import interpreter.types.SetlDefinition;

import java.util.List;

// This class represents a program definition
public class InterpreterProgram {
    private List<Statement>      mStatements;  // statements in the body of the program
    private List<SetlDefinition> mDefinitions; // definitions in the body of the program

    public InterpreterProgram(List<Statement> stmnts, List<SetlDefinition> dfntns) {
        mStatements     = stmnts;
        mDefinitions    = dfntns;
    }

    public void execute() throws SetlException {
        // first execute the definitions, because statements might call them
        for(SetlDefinition d : mDefinitions){
            d.addToEnvironment();
        }
        for(Statement s : mStatements){
            s.execute();
        }
    }

    public String toString(int tabs) {
        String result = "";
        for (Statement stmnt: mStatements) {
            result += stmnt.toString(tabs) + "\n";
        }
        for (SetlDefinition dfntn: mDefinitions) {
            result += dfntn.toString(tabs) + "\n";
        }
        return result;
    }

    public String toString() {
        return this.toString(0);
    }
}
