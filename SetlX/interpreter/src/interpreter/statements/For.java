package interpreter.statements;

import interpreter.Environment;
import interpreter.exceptions.ContinueException;
import interpreter.exceptions.ExitException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Iterator;
import interpreter.expressions.IteratorExecutionContainer;
import interpreter.types.Value;

import java.util.List;

public class For extends Statement {
    private Iterator        mIterator;
    private List<Statement> mStmntList;

    private class Exec implements IteratorExecutionContainer {
        private List<Statement> mStmntList;

        public Exec (List<Statement> stmntList) {
            mStmntList = stmntList;
        }

        public void execute(Value lastIterationValue) throws SetlException {
            for (Statement s : mStmntList) {
                s.execute();
            }
            // ContinueException and ExitException are handled by outer iteration
        }
    }

    public For(Iterator iterator, List<Statement> stmntList) {
        mIterator  = iterator;
        mStmntList = stmntList;
    }

    public void execute() throws SetlException {
        Exec e = new Exec(mStmntList);
        mIterator.eval(e);
    }

    public String toString(int tabs) {
        String endl = " ";
        if (Environment.isPrintVerbose()) {
            endl = "\n";
        }
        String result = Environment.getTabs(tabs) + "for " + mIterator + " loop" + endl;
        for (Statement stmnt: mStmntList) {
            result += stmnt.toString(tabs + 1) + endl;
        }
        result += Environment.getTabs(tabs) + "end loop;";
        return result;
    }
}
