package org.randoom.setlx.expressionUtilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;
import org.randoom.setlx.utilities.VariableScope;

import java.util.ArrayList;
import java.util.List;

/*
grammar rule:
iteratorChain
    : iterator                       (',' iterator)*
    ;

iterator
    : assignable 'in' expr
    ;

implemented here as:
      ==========      ====        ||      ========
      assignable   collection     ||        next
*/

public class SetlIterator extends CodeFragment {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(SetlIterator.class);

    private final Expr          assignable; // simple variable or a list (hopefully only of (lists of) variables)
    private final Expr          collection; // should be Set/List
    private       SetlIterator  next;       // next iterator in iteratorChain

    public SetlIterator(final Expr assignable, final Expr collection) {
        this(assignable, collection, null);
    }

    private SetlIterator(final Expr assignable, final Expr collection, final SetlIterator next) {
        this.assignable = assignable;
        this.collection = collection;
        this.next       = next;
    }

    // adds next iterator to end of current iterator chain
    public void add(final SetlIterator i) {
        if (next == null) {
            next = i;
        } else {
            next.add(i);
        }
    }

    /* executes container in scope created by this iteration
       note: resets to outer scope after iteration is finished!
       note: each iterator introduces a new scope to allow its iteration
             variable to be local
       note: variables inside the whole iteration are not _not_ local
             all will be written `through' these inner scopes                 */
    public ReturnMessage eval(final State state, final SetlIteratorExecutionContainer exec) throws SetlException {
        final VariableScope outerScope = state.getScope();
        try {
            final ReturnMessage result = evaluate(state, exec, outerScope);

            if (result == ReturnMessage.BREAK) {
                return null; // remove break message
            }

            return result;
        } finally { // make sure scope is always reset
            state.setScope(outerScope);
        }
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    public void collectVariablesAndOptimize (
        final SetlIteratorExecutionContainer container,
        final List<String>             boundVariables,
        final List<String>             unboundVariables,
        final List<String>             usedVariables
    ) {
        collection.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);

        /* Variables in this expression get assigned temporarily.
           Collect them into a temporary list, add them to boundVariables and
           remove them again before returning. */
        final List<String> tempAssigned = new ArrayList<String>();
        assignable.collectVariablesAndOptimize(new ArrayList<String>(), tempAssigned, tempAssigned);

        final int preIndex = boundVariables.size();
        boundVariables.addAll(tempAssigned);

        if (next != null) {
            next.collectVariablesAndOptimize(container, boundVariables, unboundVariables, usedVariables);
        } else {
            container.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }

        // remove the added variables (DO NOT use removeAll(); same variable name could be there multiple times!)
        for (int i = tempAssigned.size(); i > 0; --i) {
            boundVariables.remove(preIndex + (i - 1));
        }
    }
    @Override
    public void collectVariablesAndOptimize (
        final List<String>             boundVariables,
        final List<String>             unboundVariables,
        final List<String>             usedVariables
    ) {
        throw new UnsupportedOperationException("Iterators can only be optimized together with their execution container.");
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        assignable.appendString(state, sb, 0);
        sb.append(" in ");
        collection.appendString(state, sb, 0);
        if (next != null) {
            sb.append(", ");
            next.appendString(state, sb, tabs);
        }
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 3);
        result.addMember(state, assignable.toTerm(state));
        result.addMember(state, collection.toTerm(state));
        if (next != null) {
            result.addMember(state, next.toTerm(state));
        } else {
            result.addMember(state, new SetlString("nil"));
        }
        return result;
    }

    public static SetlIterator valueToIterator(final Value value) throws TermConversionException {
        if ( ! (value instanceof Term)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                final Term      term    = (Term) value;
                final String    fc      = term.functionalCharacter().getUnquotedString();
                if (! fc.equals(FUNCTIONAL_CHARACTER) || term.size() != 3) {
                    throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
                }

                final Expr      assignable  = TermConverter.valueToExpr(term.firstMember());

                final Expr      collection  = TermConverter.valueToExpr(term.getMember(2));

                      SetlIterator  iterator    = null;
                if (! term.lastMember().equals(new SetlString("nil"))) {
                    iterator    = SetlIterator.valueToIterator(term.lastMember());
                }
                return new SetlIterator(assignable, collection, iterator);
            } catch (final SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }

    /* private functions */

    private ReturnMessage evaluate(final State state, final SetlIteratorExecutionContainer exec, final VariableScope outerScope) throws SetlException {
        if (state.isExecutionStopped) {
            throw new StopExecutionException("Interrupted");
        }
        final Value iterationValue = collection.eval(state); // trying to iterate over this value
        if (iterationValue instanceof CollectionValue) {
            final CollectionValue   coll        = (CollectionValue) iterationValue;
            // scope for inner execution/next iterator
            final VariableScope     innerScope  = state.getScope().createInteratorBlock();
            // iterate over items
            for (final Value v: coll) {
                // restore inner scope
                state.setScope(innerScope);
                innerScope.setWriteThrough(false); // force iteration variables to be local to this block

                // assign value from collection
                final boolean successful = assignable.assignUnclonedCheckUpTo(state, v.clone(), outerScope);

                /*
                 * This check is done to allow the following statement to work as expected:
                 *
                 * for (x in s, [x,y] in t) {...}
                 *
                 * where the the loop is only executed, when the first
                 * `x' is equal to the second `x'
                 */
                if ( ! successful) {
                    continue;
                }

                if (state.traceAssignments) {
                    state.outWriteLn("~< Trace (iterator): " + assignable.toString() + " := " + v + " >~");
                }

                // reset WriteThrough, because changes during execution are not strictly local
                innerScope.setWriteThrough(true);
                /* Starts iteration of next iterator or execution if this is the
                   last iterator.
                   Stops iteration if requested by execution.                 */
                ReturnMessage result = null;
                if (next != null) {
                    result = next.evaluate(state, exec, outerScope);
                } else {
                    result = exec.execute(state, v);
                }
                if (result != null) {
                    if (result == ReturnMessage.CONTINUE) {
                        continue;
                    } /* else if (result == ReturnMessage.BREAK) {
                        return result; // also break next iterator
                    } */
                    return result;
                }
            }
            return null;
        } else {
            throw new IncompatibleTypeException(
                "Evaluation of iterator '" + iterationValue + "' is not a collection value."
            );
        }
    }
}

