package interpreter.statements;

import interpreter.exceptions.SetlException;

public abstract class Statement {
    public abstract void execute() throws SetlException;
    
    public abstract String toString(int tabs);

    public String toString() {
        return toString(0);
    }
}
