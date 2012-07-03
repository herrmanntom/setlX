package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.Iterator;
import org.randoom.setlx.utilities.IteratorExecutionContainer;
import org.randoom.setlx.utilities.TermConverter;

/*
grammar rule:
statement
    : [...]
    | 'for' '(' iteratorChain ')' '{' block '}'
    ;

implemented here as:
                ========-----         =====
                  mIterator        mStatements
*/

public class For extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String  FUNCTIONAL_CHARACTER   = "^for";
    // continue execution of this loop in debug mode until it finishes. MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public        static boolean sFinishLoop            = false;

    private final Iterator  mIterator;
    private final Block     mStatements;

    private class Exec implements IteratorExecutionContainer {
        private final Block mStatements;

        public Exec(final Block statements) {
            mStatements = statements;
        }

        public Value execute(final Value lastIterationValue) throws SetlException {
            return mStatements.execute();
            // ContinueException and BreakException are handled by outer iterator
        }
    }

    public For(final Iterator iterator, final Block statements) {
        mIterator   = iterator;
        mStatements = statements;
    }

    protected Value exec() throws SetlException {
        final boolean finishLoop = sFinishLoop;
        if (finishLoop) { // unset, because otherwise it would be reset when this loop finishes
            Environment.setDebugFinishLoop(false);
        }
        final Value result = mIterator.eval(new Exec(mStatements));
        if (sFinishLoop) {
            Environment.setDebugModeActive(true);
            Environment.setDebugFinishLoop(false);
        } else if (finishLoop) {
            Environment.setDebugFinishLoop(true);
        }
        return result;
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        sb.append("for (");
        mIterator.appendString(sb);
        sb.append(") ");
        mStatements.appendString(sb, tabs, true);
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mIterator.toTerm());
        result.addMember(mStatements.toTerm());
        return result;
    }

    public static For termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Iterator  iterator    = Iterator.valueToIterator(term.firstMember());
            final Block     block       = TermConverter.valueToBlock(term.lastMember());
            return new For(iterator, block);
        }
    }
}

