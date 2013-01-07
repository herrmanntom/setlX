package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
grammar rules:
initBlock
    : statement+
    ;

block
    : statement*
    ;

implemented here as:
      =========
     mStatements
*/

public class Block extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String     FUNCTIONAL_CHARACTER = "^block";
    // Request execution to stop. MAY ONLY BE SET BY STATE CLASS!
    public        static boolean    sStopExecution       = false;

    private final List<Statement> mStatements;

    public Block() {
        this(new ArrayList<Statement>());
    }

    public Block(final int size) {
        this(new ArrayList<Statement>(size));
    }

    public Block(final List<Statement> statements) {
        mStatements = statements;
    }

    public void add(final Statement stmnt) {
        mStatements.add(stmnt);
    }

    @Override
    public ReturnMessage exec(final State state) throws SetlException {
        ReturnMessage result = null;
        for (final Statement stmnt : mStatements) {
            if (sStopExecution) {
                throw new StopExecutionException("Interrupted");
            }
            result = stmnt.exec(state);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    protected ReturnMessage execute(final State state) throws SetlException {
        return exec(state);
    }

    /* Gather all bound and unbound variables in this statement and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       Optimize sub-expressions during this process by calling optimizeAndCollectVariables()
       when adding variables from them.
    */
    @Override
    public void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        for (final Statement stmnt : mStatements) {
            stmnt.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
    }

    public void markLastExprStatement() {
        if (mStatements.size() > 0) {
            final Statement stmnt = mStatements.get(mStatements.size() - 1);
            if (stmnt instanceof StatementWithPrintableResult) {
                ((StatementWithPrintableResult) stmnt).setPrintAfterEval();
            }
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        appendString(state, sb, tabs, false);
    }

    public void appendString(final State state, final StringBuilder sb, final int tabs, final boolean brackets) {
        final String endl      = state.getEndl();
              int    stmntTabs = tabs;
        if (brackets) {
            stmntTabs += 1;
            sb.append("{");
            sb.append(endl);
        }
        final Iterator<Statement> iter = mStatements.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, stmntTabs);
            if (iter.hasNext()) {
                sb.append(endl);
            }
        }
        if (brackets) {
            sb.append(endl);
            state.getLineStart(sb, tabs);
            sb.append("}");
        }
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);

        final SetlList stmntList = new SetlList(mStatements.size());
        for (final Statement s: mStatements) {
            stmntList.addMember(state, s.toTerm(state));
        }
        result.addMember(state, stmntList);

        return result;
    }

    public static Block termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList    stmnts  = (SetlList) term.lastMember();
            final Block       block   = new Block(stmnts.size());
            for (final Value v : stmnts) {
                block.add(TermConverter.valueToStatement(v));
            }
            return block;
        }
    }
}

