package interpreter;

import interpreter.exceptions.SetlException;
import interpreter.statements.Statement;
import interpreter.types.SetlDefinition;

import java.util.List;

// This class represents a program definition
public class Program extends InterpreterProgram {
    private String           mName;        // program name

    public Program(String name, List<Statement> stmnts, List<SetlDefinition> dfntns) {
        super(stmnts, dfntns);
        mName = name;
    }

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs) + "program " + mName + ";\n";
        result += super.toString(tabs + 1);
        result += Environment.getTabs(tabs) + "end " + mName + ";";
        return result;
    }
}
