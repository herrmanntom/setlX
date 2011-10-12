package interpreter.statements;

import interpreter.Environment;
import interpreter.boolExpressions.BoolExpr;

import java.util.List;

public class CaseBranch extends AbstractBranch{
    private BoolExpr         mBoolExpr;
    private List<Statement>  mStatements;

    public CaseBranch(BoolExpr b, List<Statement> statements){
        mBoolExpr   = b;
        mStatements = statements;
    }

    public BoolExpr getCondition(){
        return mBoolExpr;
    }

    public List<Statement> getStatements(){
        return mStatements;
    }

    public void setCondition(BoolExpr boolExpr){
        mBoolExpr = boolExpr;
    }

    public void addStatement(Statement stmnt){
        mStatements.add(stmnt);
    }

    public String toString(int tabs){
        String endl = " ";
        if (Environment.isPrintVerbose()) {
            endl = "\n";
        }
        String result = Environment.getTabs(tabs) + "when " + mBoolExpr + " =>" + endl;
        for (Statement stmnt: mStatements) {
            result += stmnt.toString(tabs + 1) + endl;
        }
        return result.substring(0, result.length() - endl.length());
    }
}
