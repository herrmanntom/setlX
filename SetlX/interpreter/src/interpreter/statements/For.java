package interpreter.statements;

import interpreter.Environment;
import interpreter.exceptions.SetlException;
import interpreter.types.Value;
import interpreter.utilities.Iterator;
import interpreter.utilities.IteratorExecutionContainer;

public class For extends Statement {
    private Iterator    mIterator;
    private Block       mStatements;

    private class Exec implements IteratorExecutionContainer {
        private Block   mStatements;

        public Exec (Block statements) {
            mStatements = statements;
        }

        public void execute(Value lastIterationValue) throws SetlException {
            mStatements.execute();
            // ContinueException and BreakException are handled by outer iterator
        }
    }

    public For(Iterator iterator, Block statements) {
        mIterator   = iterator;
        mStatements = statements;
    }

    public void execute() throws SetlException {
        Exec e = new Exec(mStatements);
        mIterator.eval(e);
    }

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs);
        result += "for (" + mIterator + ") {" + Environment.getEndl();
        result += mStatements.toString(tabs + 1);
        result += Environment.getTabs(tabs) + "}";
        return result;
    }
}
