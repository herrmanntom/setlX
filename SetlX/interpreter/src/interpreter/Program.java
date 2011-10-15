package interpreter;

import interpreter.exceptions.SetlException;
import interpreter.functions.PreDefinedFunction;
import interpreter.statements.Block;
import interpreter.types.SetlDefinition;

import java.util.List;

// This class represents a program definition
public class Program {
    private Block   mStatements;        // statements in the body of the program

    public Program(Block statements) {
        mStatements = statements;
    }

    public void execute() throws SetlException {
        mStatements.execute();
    }

    public String toString(int tabs) {
        return mStatements.toString(tabs);
    }

    public String toString() {
        return this.toString(0);
    }
}
