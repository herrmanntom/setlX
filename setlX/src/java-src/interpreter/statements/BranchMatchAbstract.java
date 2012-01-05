package interpreter.statements;

import interpreter.types.Value;

public abstract class BranchMatchAbstract extends Statement {

    public abstract boolean matches(Value term);

}

