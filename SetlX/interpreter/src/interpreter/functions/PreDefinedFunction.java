package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.statements.Statement;
import interpreter.types.SetlDefinition;
import interpreter.types.Value;

import java.util.LinkedList;
import java.util.List;

public abstract class PreDefinedFunction extends SetlDefinition {

    protected PreDefinedFunction(String name) {
        super(name, new LinkedList<String>(), new LinkedList<Statement>(), new LinkedList<SetlDefinition>());
    }

    public Value   call(List<Value> args, boolean returnCollection) throws SetlException {
        if (returnCollection) {
            throw new UndefinedOperationException("Incorrect set of brackets for function call.");
        }
        return this.call(args);
    }
    public abstract Value   call(List<Value> args) throws SetlException;

    public abstract boolean writeVars();

}
