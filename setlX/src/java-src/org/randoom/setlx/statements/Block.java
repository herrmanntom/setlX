package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
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
    // Request execution to stop. MAY ONLY BE SET BY ENVIRONMENT CLASS!
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

    public Value execute() throws SetlException {
        Value result = null;
        for (final Statement stmnt : mStatements) {
            if (sStopExecution) {
                throw new StopExecutionException("Interrupted");
            }
            result = stmnt.execute();
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    protected Value exec() throws SetlException {
        return execute();
    }

    public void markLastExprStatement() {
        if (mStatements.size() > 0) {
            final Statement stmnt = mStatements.get(mStatements.size() - 1);
            if (stmnt instanceof ExpressionStatement) {
                ((ExpressionStatement) stmnt).setPrintAfterEval();
            } else if (stmnt instanceof DifferenceAssignment) {
                ((DifferenceAssignment) stmnt).setPrintAfterEval();
            } else if (stmnt instanceof DivideAssignment) {
                ((DivideAssignment) stmnt).setPrintAfterEval();
            } else if (stmnt instanceof IntegerDivisionAssignment) {
                ((IntegerDivisionAssignment) stmnt).setPrintAfterEval();
            } else if (stmnt instanceof ModuloAssignment) {
                ((ModuloAssignment) stmnt).setPrintAfterEval();
            } else if (stmnt instanceof MultiplyAssignment) {
                ((MultiplyAssignment) stmnt).setPrintAfterEval();
            } else if (stmnt instanceof SumAssignment) {
                ((SumAssignment) stmnt).setPrintAfterEval();
            }
        }
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        appendString(sb, tabs, false);
    }

    public void appendString(final StringBuilder sb, final int tabs, final boolean brackets) {
        final String endl      = Environment.getEndl();
              int    stmntTabs = tabs;
        if (brackets) {
            stmntTabs += 1;
            sb.append("{");
            sb.append(endl);
        }
        final Iterator<Statement> iter = mStatements.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(sb, stmntTabs);
            if (iter.hasNext()) {
                sb.append(endl);
            }
        }
        if (brackets) {
            sb.append(endl);
            Environment.getLineStart(sb, tabs);
            sb.append("}");
        }
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);

        final SetlList stmntList = new SetlList(mStatements.size());
        for (Statement s: mStatements) {
            stmntList.addMember(s.toTerm());
        }
        result.addMember(stmntList);

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

