package interpreter.statements;

import interpreter.Environment;
import interpreter.boolExpressions.BoolExpr;
import interpreter.expressions.ValueExpr;
import interpreter.types.SetlBoolean;

import java.util.List;

public class DefaultBranch extends AbstractBranch {
    private List<Statement>  mStatements;

    public DefaultBranch(List<Statement> statements){
        mStatements = statements;
    }

    public List<Statement> getStatements(){
        return mStatements;
    }

    public BoolExpr getCondition(){
        return new BoolExpr(new ValueExpr(SetlBoolean.TRUE));
    }

    public void addStatement(Statement stmnt){
        mStatements.add(stmnt);
    }

    public String toString(int tabs){
        String endl = " ";
        if (Environment.isPrintVerbose()) {
            endl = "\n";
        }
        String result = Environment.getTabs(tabs) + "otherwise =>" + endl;
        for (Statement stmnt: mStatements) {
            result += stmnt.toString(tabs + 1) + endl;
        }
        return result.substring(0, result.length() - endl.length());
    }
}
