package interpreter.types;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.expressions.Expr;
import interpreter.expressions.Negate;
import interpreter.expressions.Power;
import interpreter.expressions.ValueExpr;
import interpreter.utilities.Environment;

import java.util.Iterator;
import java.util.List;

/*
    This class implements terms in the form of

        f(e1, e2, ..., en)

*/

public class Term extends CollectionValue {

    private String      mFunctionalCharacter; // functional character of the term;    e.g. f
    private SetlList    mBody;                // arguments in inner body of the term; e.g. e1, e2, ..., en

    public Term(String functionalCharacter) {
        this(functionalCharacter, new SetlList());
    }

    public Term(String functionalCharacter, SetlList body) {
        mFunctionalCharacter = functionalCharacter;
        mBody                = body;
    }

    public Term clone() {
        return new Term(mFunctionalCharacter, mBody.clone());
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
        for (Value v: mBody) {
            if (v.equals(element)) {
                return SetlBoolean.TRUE;
            } else if (v instanceof CollectionValue) {
                CollectionValue innerValue = (CollectionValue) v;
                if (innerValue.containsMember(element) == SetlBoolean.TRUE) {
                    return SetlBoolean.TRUE;
                }
            }
        }
        return SetlBoolean.FALSE;
    }

    public Value firstMember() {
        return mBody.firstMember();
    }

    public SetlString functionalCharacter() {
        return new SetlString(mFunctionalCharacter);
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

    /* calls (function calls) */

    public Value call(List<Expr> exprs, List<Value> args) throws SetlException {
        if (mBody.size() > 0) {
            throw new UndefinedOperationException("You may not use more than one set of brackets when creating terms.");
        } else if (args.contains(RangeDummy.RD)) {
            throw new UndefinedOperationException("Terms can not be created with ranges as parameters.");
        }

        Term result = new Term(mFunctionalCharacter);

        for (Value arg: args) {
            result.addMember(arg);
        }

        return result;
    }

    /* string and char operations */

    public String toString() {
        boolean interprete  = Environment.isInterpreteStrings();
        Environment.setInterpreteStrings(false);

        if (mBody.size() <= 0) {
            return mFunctionalCharacter;
        }
        // lists use [] in toString, which have to be removed...
        String result = mBody.toString();
        result = mFunctionalCharacter + "(" + result.substring(1, result.length() - 1) + ")";

        Environment.setInterpreteStrings(interprete);

        return result;
    }

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     * "incomparable" values, e.g. of different types are ranked as follows:
     * SetlError < Om < -Infinity < SetlBoolean < SetlInt & Real < SetlString < SetlSet < SetlList < Term < ProcedureDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    public int compareTo(Value v){
        if (v instanceof Term) {
            Term other = (Term) v;
            int cmp = mFunctionalCharacter.compareTo(other.mFunctionalCharacter);
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

