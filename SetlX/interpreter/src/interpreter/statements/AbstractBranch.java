package interpreter.statements;

import java.util.List;

import interpreter.boolExpressions.BoolExpr;

public abstract class AbstractBranch  {
	public abstract String toString(int tabs);
    public abstract List<Statement> getStatements();
    public abstract BoolExpr getCondition();
}
