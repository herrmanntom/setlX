package interpreter.statements;

import interpreter.Environment;
import interpreter.boolExpressions.BoolExpr;
import interpreter.expressions.ValueExpr;
import interpreter.types.SetlBoolean;

import java.util.List;

public class IfBranch extends AbstractBranch {
    public final static int IF     = 0;
    public final static int ELSEIF = 1;
    public final static int ELSE   = 2;

    private int              mType;
    private BoolExpr         mBoolExpr;
    private List<Statement>  mStatements;

    public IfBranch(int type, BoolExpr b, List<Statement> statements){
        mType       = type;
        mBoolExpr   = b;
        mStatements = statements;
    }

    public BoolExpr getCondition(){
        if (mType == ELSE) {
            return new BoolExpr(new ValueExpr(SetlBoolean.TRUE));
        }
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
        String result = Environment.getTabs(tabs);
        if (mType == ELSE || mType == ELSEIF) {
            result += "else";
        }
        if (mType == IF || mType == ELSEIF) {
            result += "if " + mBoolExpr + " then";
        }
        result += endl;
        for (Statement stmnt: mStatements) {
            result += stmnt.toString(tabs + 1) + endl;
        }
        return result;
    }
}
