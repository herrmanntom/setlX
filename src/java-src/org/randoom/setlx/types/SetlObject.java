package org.randoom.setlx.types;

import java.util.ArrayList;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.ValueExpr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

/* This class implements a object which can store arbitrary SetlX values.
 * It will most likely be created by a ConstructorDefinition
 * (or is result of an operation).
 *
 * Also see:
 *   interpreter.types.ConstructorDefinition
 */

public class SetlObject extends Value {
    /* To allow initially `free' cloning, by only marking a clone without
     * actually doing any cloning, this object carries a isClone flag.
     *
     * If the contents of this SetlObject is modified `separateFromOriginal()'
     * MUST be called before the modification, which then performs the real cloning,
     * if required.
     *
     * Main benefit of this technique is to perform the real cloning only
     * when a clone is actually modified, thus not performing a time consuming
     * cloning, when the clone is only used read-only, which it is in most cases.
     */

    private     final VariableScope mStaticDefinitions;
    /*package*/       VariableScope mMembers;
    // is this object a clone
    private           boolean       isCloned;

    private SetlObject(final VariableScope staticDefinitions, final VariableScope members) {
        mStaticDefinitions = staticDefinitions;
        mMembers           = members;
        isCloned           = false; // new objects are not a clone
    }

    public static SetlObject createNew(final VariableScope staticDefinitions, final VariableScope members) {
        return new SetlObject(staticDefinitions, members);
    }

    private static SetlObject createClone(final VariableScope staticDefinitions, final VariableScope members) {
        final SetlObject result = new SetlObject(staticDefinitions, members);
        result.isCloned = true;
        return result;
    }

    @Override
    public SetlObject clone() {
        /* When cloning, THIS object is marked to be a clone as well.
         *
         * This is done, because even though THIS is the original, it must also be
         * cloned upon modification, otherwise clones which carry the same
         * member map of THIS object would not notice, e.g.
         * modifications of THIS original would bleed through to the clones.
         */
        isCloned = true;
        return createClone(mStaticDefinitions, mMembers);
    }

    /* If the contents of THIS SetlList is modified, the following function MUST
     * be called before the modification. It performs the real cloning,
     * if THIS is actually marked as a clone.
     *
     * While clone() is called upon all members of this list, this does not perform
     * a `deep' cloning, as the members themselves are only marked for cloning.
     */
    private void separateFromOriginal() {
        if (isCloned) {
            mMembers = mMembers.clone();
            isCloned = false;
        }
    }

    private Value overload(final State    state,
                           final Variable member
    ) throws SetlException {
        final ArrayList<Expr> args = new ArrayList<Expr>();
        return overloadQuerry(state, member).call(state, args);
    }

    private Value overload(final State    state,
                           final Variable member,
                           final Value    other
    ) throws SetlException {
        final ArrayList<Expr> args = new ArrayList<Expr>();
        args.add(new ValueExpr(other));
        return overloadQuerry(state, member).call(state, args);
    }

    private Value overloadQuerry(final State state, final Variable member) throws SetlException {
        final Value function = getObjectMemberUnCloned(state, member);
        if (function != Om.OM) {
            return function;
        }
        throw new UndefinedOperationException(
            "Member '" + member + " in " + this + "' is undefined."
        );
    }

    /* type checks (sort of boolean operation) */

    @Override
    public SetlBoolean isObject() {
        return SetlBoolean.TRUE;
    }

    /* arithmetic operations */

    @Override
    public Value factorial(final State state) throws SetlException {
        return overload(state, FACTORIAL);
    }
    final static Variable FACTORIAL = new Variable("factorial");

    @Override
    public Value sum(final State state, final Value summand) throws SetlException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand instanceof SetlString) {
            return ((SetlString) summand).sumFlipped(state, this);
        }
        return overload(state, SUM, summand);
    }
    final static Variable SUM = new Variable("sum");

    /* operations on collection values (Lists/Tuples, Sets [, Strings]) */

    /* features of objects */

    @Override
    public Value getObjectMember(final State state, final Variable variable) throws SetlException {
        return getObjectMemberUnCloned(state, variable).clone();
    }

    @Override
    public Value getObjectMemberUnCloned(final State state, final Variable variable) throws SetlException {
        separateFromOriginal();
        final VariableScope oldScope = state.getScope();
        state.setScope(mMembers);
        try {
            final Value value = variable.eval(state);
            if (value instanceof ProcedureDefinition) {
                ((ProcedureDefinition) value).addSurroundingObject(this);
            }
            return value;
        } finally {
            state.setScope(oldScope);
        }
    }

    @Override
    public void setObjectMember(final State state, final Variable variable, final Value value) {
        separateFromOriginal();
        final VariableScope oldScope = state.getScope();
        state.setScope(mMembers);
        try {
            variable.assignUncloned(state, value);
        } finally {
            state.setScope(oldScope);
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("object<{");
        mMembers.appendString(state, sb, tabs);
        if (mStaticDefinitions != null) {
            sb.append(" static{");
            mStaticDefinitions.appendString(state, sb, tabs);
            sb.append("}");
        }
        sb.append("}>");
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     */
    @Override
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else if (v instanceof SetlObject) {
            final SetlObject other = (SetlObject) v;
            final int cmp = mMembers.compareTo(other.mMembers);
            if (cmp != 0) {
                return cmp;
            } else if (mStaticDefinitions != null && other.mStaticDefinitions != null) {
                return mStaticDefinitions.compareTo(other.mStaticDefinitions);
            } else if (mStaticDefinitions != null) {
                return 1;
            } else if (other.mStaticDefinitions != null) {
                return -1;
            } else {
                return 0;
            }
        } else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

    /* To compare "incomparable" values, e.g. of different types, the following
     * order is established and used in compareTo():
     * SetlError < Om < -Infinity < SetlBoolean < Rational & Real
     * < SetlString < SetlSet < SetlList < Term < ProcedureDefinition
     * < SetlObject < ConstructorDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    @Override
    protected int compareToOrdering() {
        return 1100;
    }

    @Override
    public boolean equalTo(final Value v) {
        if (this == v) {
            return true;
        } else if (v instanceof SetlObject) {
            final SetlObject other = (SetlObject) v;
            if (mMembers.equalTo(other.mMembers)) {
                if (mStaticDefinitions != null && other.mStaticDefinitions != null) {
                    return mStaticDefinitions.equalTo(other.mStaticDefinitions);
                } else if (mStaticDefinitions == null && other.mStaticDefinitions == null) {
                    return true;
                }
            }
        }
        return false;
    }

    private final static int initHashCode = SetlObject.class.hashCode();

    @Override
    public int hashCode() {
        final int size = mMembers.size();
        int hash = initHashCode + size;
        if (size >= 1) {
            hash = hash * 31 + mMembers.hashCode();
        }
        return hash;
    }
}

