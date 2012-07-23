package org.randoom.setlx.utilities;


import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

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
      mAssignable  mCollection    ||       mNext
*/

public class Iterator {
    // functional character used in terms
    private final static String     FUNCTIONAL_CHARACTER = "^iterator";
    // Trace all assignments. MAY ONLY BE SET BY CONTINUE CLASS!
    public        static boolean    sContinue            = false;
    // Trace all assignments. MAY ONLY BE SET BY BREAK CLASS!
    public        static boolean    sBreak               = false;
    // Request execution to stop. MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public        static boolean    sStopExecution       = false;
    // Trace all assignments. MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public        static boolean    sTraceAssignments    = false;

    private final Expr      mAssignable; // Lhs is a simple variable or a list (hopefully only of (lists of) variables)
    private final Expr      mCollection; // Rhs (should be Set/List)
    private       Iterator  mNext;       // next iterator in iteratorChain

    public Iterator(final Expr assignable, final Expr collection) {
        mAssignable = assignable;
        mCollection = collection;
        mNext       = null;
    }

    private Iterator(final Expr assignable, final Expr collection, final Iterator next) {
        mAssignable = assignable;
        mCollection = collection;
        mNext       = next;
    }

    // adds next iterator to end of current iterator chain
    public void add(final Iterator i) {
        if (mNext == null) {
            mNext = i;
        } else {
            mNext.add(i);
        }
    }

    /* executes container in scope created by this iteration
       note: resets to outer scope after iteration is finished!
       note: each iterator introduces a new scope to allow its iteration
             variable to be local
       note: variables inside the whole iteration are not _not_ local
             all will be written `through' these inner scopes                 */
    public Value eval(final IteratorExecutionContainer exec) throws SetlException {
        final VariableScope outerScope = VariableScope.getScope();
        try {
            final Value result = evaluate(exec);

            if (result == Om.OM && Om.OM.isBreak()) {
                return null; // remove break message
            }

            return result;
        } finally { // make sure scope is always reset
            VariableScope.setScope(outerScope);
        }
    }

    /* string operations */

    public void appendString(final StringBuilder sb) {
        mAssignable.appendString(sb, 0);
        sb.append(" in ");
        mCollection.appendString(sb, 0);
        if (mNext != null) {
            sb.append(", ");
            mNext.appendString(sb);
        }
    }

    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        appendString(sb);
        return sb.toString();
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mAssignable.toTerm());
        result.addMember(mCollection.toTerm());
        if (mNext != null) {
            result.addMember(mNext.toTerm());
        } else {
            result.addMember(new SetlString("nil"));
        }
        return result;
    }

    public static Iterator valueToIterator(final Value value) throws TermConversionException {
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

                      Iterator  iterator    = null;
                if (! term.lastMember().equals(new SetlString("nil"))) {
                    iterator    = Iterator.valueToIterator(term.lastMember());
                }
                return new Iterator(assignable, collection, iterator);
            } catch (SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }

    /* private functions */

    private Value evaluate(final IteratorExecutionContainer exec) throws SetlException {
        if (sStopExecution) {
            throw new StopExecutionException("Interrupted");
        }
        final Value iterationValue = mCollection.eval(); // trying to iterate over this value
        if (iterationValue instanceof CollectionValue) {
            final CollectionValue   coll        = (CollectionValue) iterationValue;
            // scope for inner execution/next iterator
            final VariableScope     innerScope  = VariableScope.getScope().createInteratorBlock();
            // iterate over items
            for (final Value v: coll) {
                // restore inner scope
                VariableScope.setScope(innerScope);
                innerScope.setWriteThrough(false); // force iteration variables to be local to this block
                // assign value from collection
                mAssignable.assign(v.clone());

                if (sTraceAssignments) {
                    Environment.outWriteLn("~< Trace (iterator): " + mAssignable.toString() + " := " + v + " >~");
                }

                // reset WriteThrough, because changes during execution are not strictly local
                innerScope.setWriteThrough(true);
                /* Starts iteration of next iterator or execution if this is the
                   last iterator.
                   Stops iteration if requested by execution.                 */
                Value result = null;
                if (mNext != null) {
                    result = mNext.evaluate(exec);
                } else {
                    result = exec.execute(v);
                }
                if (result != null) {
                    if (result == Om.OM) {
                        if (Om.OM.isContinue()) {
                            continue;
                        } else if (Om.OM.isBreak()) {
                            return Om.OM.setBreak(); // also break next iterator
                        }
                    }
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

