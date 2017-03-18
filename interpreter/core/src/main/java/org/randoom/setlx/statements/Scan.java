package org.randoom.setlx.statements;

import org.randoom.setlx.assignments.AssignableVariable;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.statementBranches.AbstractMatchBranch;
import org.randoom.setlx.statementBranches.AbstractMatchScanBranch;
import org.randoom.setlx.statementBranches.MatchDefaultBranch;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.ScanResult;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;
import org.randoom.setlx.utilities.VariableScope;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for the SetlX scan statement.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'scan' '(' expr ')' ('using' variable)? '{' [...] '}'
 *     ;
 *
 * implemented with different classes which inherit from AbstractMatchScanBranch:
 *                  ====              ========       =====
 *                  expr               posVar      branchList
 */
public class Scan extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(Scan.class);

    private final OperatorExpression expr;
    private final AssignableVariable posVar;
    private final FragmentList<AbstractMatchScanBranch> branchList;

    /**
     * Create a new scan statement.
     *
     * @param expr       Expression forming the string to match.
     * @param posVar     Variable storing the current position inside the string.
     * @param branchList List of scan branches.
     */
    public Scan(final OperatorExpression expr, final AssignableVariable posVar, final FragmentList<AbstractMatchScanBranch> branchList) {
        this.expr       = expr;
        this.posVar     = posVar;
        this.branchList = branchList;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        final Value value = expr.evaluate(state);
        if ( ! (value instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "The value '" + value + "' is not a string and cannot be scaned."
            );
        }
        final VariableScope outerScope = state.getScope();

        try {
            SetlString string = (SetlString) value.clone();
            int        charNr   = 1;
            int        lineNr   = 1;
            int        columnNr = 1;
            while(string.size() > 0) {
                int                     largestMatchSize   = Integer.MIN_VALUE;
                AbstractMatchScanBranch largestMatchBranch = null;
                MatchResult             largestMatchResult = null;

                // map of current position in input-string
                final SetlSet  position = new SetlSet();

                final SetlList charEntry = new SetlList(2);
                charEntry.addMember(state, new SetlString("char"));
                charEntry.addMember(state, Rational.valueOf(charNr));
                position.addMember(state, charEntry);

                final SetlList line   = new SetlList(2);
                line.addMember(state, new SetlString("line"));
                line.addMember(state, Rational.valueOf(lineNr));
                position.addMember(state, line);

                final SetlList column   = new SetlList(2);
                column.addMember(state, new SetlString("column"));
                column.addMember(state, Rational.valueOf(columnNr));
                position.addMember(state, column);

                // find branch which matches largest string
                for (final AbstractMatchScanBranch br : branchList) {
                    final ScanResult result = br.scans(state, string);
                    if (result.isMatch() && result.getEndOffset() > largestMatchSize) {
                        // scope for condition
                        final VariableScope innerScope = outerScope.createLinkedScope();
                        state.setScope(innerScope);

                        // put current position into scope
                        if (posVar != null) {
                            posVar.assignUncloned(state, position, FUNCTIONAL_CHARACTER);
                        }

                        // put all matching variables into scope
                        result.setAllBindings(state, FUNCTIONAL_CHARACTER);

                        // check condition
                        if (br.evalConditionToBool(state)) {
                            largestMatchSize   = result.getEndOffset();
                            largestMatchBranch = br;
                            largestMatchResult = result;
                        }

                        // reset scope
                        state.setScope(outerScope);
                    }
                }
                // execute branch which matches largest string
                if (largestMatchBranch != null && largestMatchResult != null) {
                    // scope for execution
                    final VariableScope innerScope = outerScope.createIteratorBlock();
                    state.setScope(innerScope);

                    // force match variables to be local to this block
                    final int writeThroughToken = innerScope.unsetWriteThrough();
                    // put current position into scope
                    if (posVar != null) {
                        posVar.assignUncloned(state, position, FUNCTIONAL_CHARACTER);
                    }
                    // put all matching variables into current scope
                    largestMatchResult.setAllBindings(state, FUNCTIONAL_CHARACTER);
                    // reset WriteThrough, because changes during execution are not strictly local
                    innerScope.setWriteThrough(writeThroughToken);

                    // execute statements
                    final ReturnMessage execResult = largestMatchBranch.getStatements().execute(state);

                    // reset scope
                    state.setScope(outerScope);

                    // return if we got a valid return value, or if default branch was largest match
                    if (execResult != null || largestMatchSize == MatchDefaultBranch.END_OFFSET) {
                        return execResult;
                    }

                    // compute positions
                    charNr   += largestMatchSize;
                    columnNr += largestMatchSize;
                    // find lineEndings
                    final String matched = string.getMembers(1, largestMatchSize).getUnquotedString(state);
                    int pos  = 0;
                    int size;
                    while (true) {
                        int tmp  = matched.indexOf("\r\n", pos);
                        size = 2;
                        if (tmp < 0) {
                            tmp  = matched.indexOf("\n", pos);
                            size = 1;
                        }
                        if (tmp >= 0) {
                            ++lineNr;
                            pos = tmp + size;
                        } else {
                            break;
                        }
                    }
                    if (pos > 0) {
                        // reduce columnNr by last read lineEnding position
                        columnNr = largestMatchSize - (pos - size);
                    }

                    // reduce scanned string
                    string  = string.getMembers(largestMatchSize + 1, string.size());
                } else {
                    // nothing matched!
                    throw new UndefinedOperationException("Infinite loop in scan-statement detected.");
                }
            }
            return null;
        } finally {
            // make sure scope is always reset
            state.setScope(outerScope);
        }
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);

        /* The Variable in this statement get assigned temporarily.
           Collect it into a temporary list and remove it again before returning. */
        final List<String> tempAssigned = new ArrayList<String>();
        if (posVar != null) {
            posVar.collectVariablesAndOptimize(state, new ArrayList<String>(), tempAssigned, tempAssigned);
        }
        final int preBound = boundVariables.size();
        boundVariables.addAll(tempAssigned);

        // binding inside an scan are only valid if present in all branches
        // and last branch is an default-branch
        List<String> boundHere = null;
        for (final AbstractMatchBranch br : branchList) {
            final List<String> boundTmp = new ArrayList<String>(boundVariables);

            br.collectVariablesAndOptimize(state, boundTmp, unboundVariables, usedVariables);

            if (boundHere == null) {
                boundHere = new ArrayList<String>(boundTmp.subList(preBound, boundTmp.size()));
            } else {
                boundHere.retainAll(boundTmp.subList(preBound, boundTmp.size()));
            }
        }
        while (boundVariables.size() > preBound) {
            boundVariables.remove(boundVariables.size() - 1);
        }
        if (boundHere != null && branchList.get(branchList.size() - 1) instanceof MatchDefaultBranch) {
            boundHere.removeAll(tempAssigned);
            boundVariables.addAll(boundHere);
        }
        return false;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("scan (");
        expr.appendString(state, sb, 0);
        sb.append(") ");
        if (posVar != null) {
            sb.append("using ");
            posVar.appendString(state, sb, 0);
        }
        sb.append("{");
        sb.append(state.getEndl());
        for (final AbstractMatchBranch br : branchList) {
            br.appendString(state, sb, tabs + 1);
        }
        state.appendLineStart(sb, tabs);
        sb.append("}");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 3);

        if (posVar != null) {
            result.addMember(state, posVar.toTerm(state));
        } else {
            result.addMember(state, SetlString.NIL);
        }

        result.addMember(state, expr.toTerm(state));

        final SetlList bList = new SetlList(branchList.size());
        for (final AbstractMatchBranch br: branchList) {
            bList.addMember(state, br.toTerm(state));
        }
        result.addMember(state, bList);

        return result;
    }

    /**
     * Convert a term representing a Scan statement into such a statement.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting Scan Statement.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static Scan termToStatement(final State state, final Term term) throws TermConversionException {
        if (term.size() == 3 && term.lastMember() instanceof SetlList) {
            try {
                AAssignableExpression posVar = null;
                if (! term.firstMember().equals(SetlString.NIL)) {
                    posVar = TermUtilities.valueToAssignableExpr(state, term.firstMember());
                }

                final OperatorExpression                    expr       = OperatorExpression.createFromTerm(state, term.getMember(2));

                final SetlList                              branches   = (SetlList) term.lastMember();
                final FragmentList<AbstractMatchScanBranch> branchList = new FragmentList<AbstractMatchScanBranch>(branches.size());
                for (final Value v : branches) {
                    branchList.add(AbstractMatchScanBranch.valueToMatchAbstractScanBranch(state, v));
                }

                if (posVar == null || posVar.getClass() == AssignableVariable.class) {
                    return new Scan(expr, (AssignableVariable) posVar, branchList);
                }
            } catch (final SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Scan.class) {
            Scan otr = (Scan) other;
            int cmp = expr.compareTo(otr.expr);
            if (cmp != 0) {
                return cmp;
            }
            cmp = branchList.compareTo(otr.branchList);
            if (cmp != 0) {
                return cmp;
            }
            if (posVar != null) {
                if (otr.posVar != null) {
                    return posVar.compareTo(otr.posVar);
                } else {
                    return 1;
                }
            } else if (otr.posVar != null) {
                return -1;
            }
            return 0;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Scan.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == Scan.class) {
            Scan otr = (Scan) obj;
            if (expr.equals(otr.expr) && branchList.equals(otr.branchList)) {
                if (posVar != null && otr.posVar != null) {
                    return posVar.equals(otr.posVar);
                } else if (posVar == null && otr.posVar == null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + expr.hashCode();
        hash = hash * 31 + branchList.hashCode();
        return hash;
    }
}

