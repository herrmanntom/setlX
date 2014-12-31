package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.CatchableInSetlXException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.ThrownInSetlXException;
import org.randoom.setlx.operatorUtilities.AssignableOperatorExpression;
import org.randoom.setlx.operators.Variable;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

/**
 * This catchUsr block catches any exception, which was user created, e.g.
 * by using the   throw()  function in SetlX.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'try' '{' block '}' ('catch' '(' variable ')' '{' block '}' | 'catchLng' '(' variable ')' '{' block '}' | 'catchUsr' '(' variable ')' '{' block '}')+
 *     ;
 *
 * implemented here as:
 *                                                                                                                                ========         =====
 *                                                                                                                                errorVar     blockToRecover
 */
public class TryCatchUsrBranch extends AbstractTryCatchBranch {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(TryCatchUsrBranch.class);

    /**
     * Create new catchUsr-branch.
     *
     * @param errorVar       Variable to bind caught exception to.
     * @param blockToRecover Statements to execute when exception is caught.
     */
    public TryCatchUsrBranch(final Variable errorVar, final Block blockToRecover){
        super(errorVar, blockToRecover);
    }

    private TryCatchUsrBranch(final AssignableOperatorExpression errorVar, final Block blockToRecover){
        super(errorVar, blockToRecover);
    }

    @Override
    public boolean catches(final State state, final CatchableInSetlXException cise) {
        return cise instanceof ThrownInSetlXException;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(" catchUsr (");
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
     * Convert a term representing an catchUsr-branch into such a branch.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting branch.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static TryCatchUsrBranch termToBranch(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final AssignableOperatorExpression var = TermConverter.valueToAssignableExpr(state, term.firstMember());
            final Block block = TermConverter.valueToBlock(state, term.lastMember());
            return new TryCatchUsrBranch(var, block);
        }
    }

    /* comparisons */

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(TryCatchUsrBranch.class);

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

