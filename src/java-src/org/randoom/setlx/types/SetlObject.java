package org.randoom.setlx.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.ValueExpr;
import org.randoom.setlx.utilities.State;

/* This class implements a object which can store arbitrary SetlX values.
 *                                      TODO
 * It will most likely be created by an SetObjectConstructor
 * (or is result of an operation).
 *
 * Also see:
 *   TODO
 *   interpreter.expressions.SetObjectConstructor
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

    private HashMap<String, Value> mMembers;
    // is this list a clone
    private boolean                isCloned;

    public SetlObject() {
        mMembers = new HashMap<String, Value>();
        isCloned = false; // new objects are not a clone
    }

    private SetlObject(final HashMap<String, Value> members) {
        mMembers = members;
        isCloned = true;  // objects created from another object ARE a clone
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
        return new SetlObject(mMembers);
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
            final HashMap<String, Value> original = mMembers;
            mMembers = new HashMap<String, Value>();
            for (final Map.Entry<String, Value> entry : original.entrySet()) {
                mMembers.put(entry.getKey(), entry.getValue().clone());
            }
            isCloned = false;
        }
    }

    private Value overload(final State  state,
                           final String member
    ) throws SetlException {
        final ArrayList<Expr> args = new ArrayList<Expr>();
        args.add(new ValueExpr(this));
        return overloadQuerry(member).call(state, args);
    }

    private Value overload(final State  state,
                           final String member,
                           final Value  other
    ) throws SetlException {
        final ArrayList<Expr> args = new ArrayList<Expr>();
        args.add(new ValueExpr(this));
        args.add(new ValueExpr(other));
        return overloadQuerry(member).call(state, args);
    }

    private Value overloadQuerry(final String member) throws UndefinedOperationException {
        final Value function = mMembers.get(member);
        if (function != null) {
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
        return overload(state, "factorial");
    }

    @Override
    public Value sum(final State state, final Value summand) throws SetlException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand instanceof SetlString) {
            return ((SetlString) summand).sumFlipped(state, this);
        }
        return overload(state, "sum", summand);
    }

    /* operations on collection values (Lists/Tuples, Sets [, Strings]) */

    /* features of objects */

    @Override
    public Value getObjectMember(final String variable) {
        final Value result = mMembers.get(variable);
        if (result != null) {
            return result.clone();
        } else {
            return Om.OM;
        }
    }

    @Override
    public Value getObjectMemberUnCloned(final String variable) {
        separateFromOriginal();
        final Value result = mMembers.get(variable);
        if (result != null) {
            return result;
        } else {
            return Om.OM;
        }
    }

    @Override
    public void setObjectMember(final String variable, final Value value) {
        separateFromOriginal();
        mMembers.put(variable, value);
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("object<{");
        final Iterator<Entry<String, Value>> iter = mMembers.entrySet().iterator();
        while (iter.hasNext()) {
            final Entry<String, Value> entry = iter.next();
            sb.append(entry.getKey());
            sb.append(" := ");
            entry.getValue().appendString(state, sb, tabs);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("}>");
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     * "incomparable" values, e.g. of different types are ranked as follows:
     * SetlError < Om < -Infinity < SetlBoolean < Rational & Real < SetlString
     * < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    @Override
    // TODO fix order of setl types
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else if (v instanceof SetlObject) {
            final SetlObject other = (SetlObject) v;
            final int        size  = mMembers.size();
            final int        oSize = other.mMembers.size();
            if (size < oSize) {
                return -1;
            } else if (size == oSize) {
                return mMembers.toString().compareTo(other.mMembers.toString());
            } else {
                return 1;
            }
        } else {
            // TODO everything is bigger
            return 1;
        }
    }

    @Override
    public boolean equalTo(final Value v) {
        if (this == v) {
            return true;
        } else if (v instanceof SetlObject) {
            final SetlObject other = (SetlObject) v;
            if (mMembers.size() == other.mMembers.size()) {
                for (final Map.Entry<String, Value> entry : mMembers.entrySet()) {
                    final Value otherV = other.mMembers.get(entry.getKey());
                    if (otherV == null || ! entry.getValue().equalTo(otherV)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
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

