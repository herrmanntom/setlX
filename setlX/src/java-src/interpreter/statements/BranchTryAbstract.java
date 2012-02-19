package interpreter.statements;

import interpreter.exceptions.CatchableInSetlXException;

public abstract class BranchTryAbstract extends Statement {

    public abstract boolean catches(CatchableInSetlXException cise);

}

