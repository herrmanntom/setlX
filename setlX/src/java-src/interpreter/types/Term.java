package interpreter.types;

import interpreter.exceptions.SetlException;

import java.util.Iterator;

/*
    This class implements terms in the form of

        f(e1, e2, ..., en)

*/

public class Term extends CollectionValue {

    private String      mName;  // functional character of the term;    e.g. f
    private SetlList    mBody;  // arguments in inner body of the term; e.g. e1, e2, ..., en

    public Term(String name) {
        this(name, new SetlList());
    }

    public Term(String name, SetlList body) {
        mName = name;
        mBody = body;
    }

    public Term clone() {
        return new Term(mName, mBody.clone());
    }

    public Iterator<Value> iterator() {
        return mBody.iterator();
    }

    public int size() {
        return mBody.size();
    }

    /* type checks (sort of Boolean operation) */

    public SetlBoolean isTerm() {
        return SetlBoolean.TRUE;
    }

    /* operations on compound values (Lists, Sets [, Strings]) */

    public void addMember(Value element) {
        mBody.addMember(element);
    }

    public SetlList arguments() {
        return mBody.clone();
    }

    public SetlBoolean containsMember(Value element) {
        return mBody.containsMember(element);
    }

    public Value firstMember() {
        return mBody.firstMember();
    }

    public SetlString functionalCharacter() {
        return new SetlString(mName);
    }

    public Value lastMember() {
        return mBody.lastMember();
    }

    public Value maximumMember() throws SetlException {
        return mBody.maximumMember();
    }

    public Value minimumMember() throws SetlException {
        return mBody.minimumMember();
    }

    public void removeMember(Value element) {
        mBody.removeMember(element);
    }

    public void removeFirstMember() {
        mBody.removeFirstMember();
    }

    public void removeLastMember() {
        mBody.removeLastMember();
    }

    /* String and Char operations */

    public String toString() {
        if (mBody.size() <= 0) {
            return mName;
        }
        // lists use [] in toString, which have to be removed...
        String s = mBody.toString();
        return mName + "(" + s.substring(1, s.length() - 1) + ")";
    }

    /* Comparisons */

    /* Compare two Values.  Returns -1 if this value is less than the value given
     * as argument, +1 if its greater and 0 if both values contain the same
     * elements.
     * Useful output is only possible if both values are of the same type.
     * "incomparable" values, e.g. of different types are ranked as follows:
     * Om < -Infinity < SetlBoolean < SetlInt & Real < SetlString < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    public int compareTo(Value v){
        if (v instanceof Term) {
            Term other = (Term) v;
            int cmp = mName.compareTo(other.mName);
            if (cmp != 0) {
                return cmp;
            }
            return mBody.compareTo(other.mBody);
        } else if (v instanceof ProcedureDefinition || v == Infinity.POSITIVE) {
            // only ProcedureDefinition and +Infinity are bigger
            return -1;
        } else {
            // everything else is smaller
            return 1;
        }
    }
}

