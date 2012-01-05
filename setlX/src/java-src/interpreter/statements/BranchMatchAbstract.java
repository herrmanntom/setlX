package interpreter.statements;

import interpreter.types.Value;
import interpreter.utilities.MatchResult;

public abstract class BranchMatchAbstract extends Statement {

    public abstract MatchResult matches(Value term);

}

