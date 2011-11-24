package interpreter.statements;

import interpreter.exceptions.SetlException;

public abstract class BranchAbstract extends Statement {

    public abstract boolean evalConditionToBool() throws SetlException;

}
