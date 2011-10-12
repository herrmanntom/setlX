package interpreter.functions;

import interpreter.expressions.ValueExpr;
import interpreter.statements.Statement;
import interpreter.statements.ExpressionStatement;
import interpreter.types.SetlDefinition;
import interpreter.types.SetlOm;
import interpreter.types.SetlString;
import interpreter.types.Value;

import java.util.LinkedList;
import java.util.List;

public abstract class PreDefinedFunction extends SetlDefinition {

    protected PreDefinedFunction(String name) {
        super(name, new LinkedList<String>(), new LinkedList<Statement>(), new LinkedList<SetlDefinition>());
    }

    public abstract Value   call(List<Value> args, boolean returnCollection);

    public abstract boolean writeVars();

}
