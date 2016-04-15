package org.randoom.setlx.statementBranches;

import org.randoom.setlx.assignments.AssignableVariable;
import org.randoom.setlx.exceptions.CatchableInSetlXException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.ThrownInSetlXException;
import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

/**
 * This catchLng block catches any exception, which was not user created, e.g.
 * represents a semantic or syntactic error.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'try' '{' block '}' ('catch' '(' variable ')' '{' block '}' | 'catchLng' '(' variable ')' '{' block '}' | 'catchUsr' '(' variable ')' '{' block '}')+
 *     ;
 *
 * implemented here as:
 *                                                                                    ========         =====
 *                                                                                    errorVar     blockToRecover
 */
public class TryCatchLngBranch extends AbstractTryCatchBranch {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(TryCatchLngBranch.class);

    /**
     * Create new catchLng-branch.
     *
     * @param errorVar       Variable to bind caught exception to.
     * @param blockToRecover Statements to execute when exception is caught.
     */
    public TryCatchLngBranch(final AssignableVariable errorVar, final Block blockToRecover){
        super(errorVar, blockToRecover);
    }

    @Override
    public boolean catches(final State state, final CatchableInSetlXException cise) {
        return !(cise instanceof ThrownInSetlXException);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(" catchLng (");
        errorVar.appendString(state, sb, tabs);
        sb.append(") ");
        blockToRecover.appendString(state, sb, tabs, true);
    }

    /* term operations */

    @Override
    protected String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }

    /**
     * Convert a term representing an catchLng-branch into such a branch.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting branch.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static TryCatchLngBranch termToBranch(final State state, final Term term) throws TermConversionException {
        if (term.size() == 2) {
            final AAssignableExpression var = TermUtilities.valueToAssignableExpr(state, term.firstMember());
            final Block block = TermUtilities.valueToBlock(state, term.lastMember());
            if (var.getClass() == AssignableVariable.class) {
                return new TryCatchLngBranch((AssignableVariable) var, block);
            }
        }
        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
    }

    /* comparisons */

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(TryCatchLngBranch.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    /*package*/ static String functionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}

