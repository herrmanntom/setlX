package interpreter.types;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.expressions.Expr;
import interpreter.expressions.Variable;
import interpreter.utilities.Environment;
import interpreter.utilities.MatchResult;
import interpreter.utilities.TermConverter;

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

    /* operations on collection values (Lists, Sets [, Strings]) */

    public void addMember(Value element) {
        mBody.addMember(element);
    }

    public SetlList arguments() {
        return mBody.clone();
    }

    public SetlBoolean containsMember(Value element) {
        // Terms are inherently recursive, so search recursively
        return containsMemberRecursive(element);
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


    // someday return new Term 'call(...) here
//    public Value call(List<Expr> exprs, List<Value> args) throws SetlException {
//        return mBody.call(exprs, args);
//    }

    /* string and char operations */

    public String canonical() {
        String result = mBody.canonical();
        result = "(" + result.substring(1, result.length() - 1) + ")";

        if (mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER)) {
            result = Variable.FUNCTIONAL_CHARACTER_EXTERNAL + result;
        } else {
            result = mFunctionalCharacter + result;
        }

        return result;
    }

    public String toString() {
        try {
            return TermConverter.termToCodeFragment(this).toString();
        } catch (TermConversionException tce) {
            return canonical();
        }
    }

    /* term operations */

    public MatchResult matchesTerm(Value other) {
        if (other == IgnoreDummy.ID) {
            return new MatchResult(true);
        } else if (mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER) && mBody.size() == 1) {
            // 'this' is a variable, which match anything (except ignore of course)
            MatchResult result  = new MatchResult(true);
            // get name of variable
            Value       idStr   = mBody.iterator().next();
            if ( ! (idStr instanceof SetlString)) { // wrong 'variable term
                return new MatchResult(false);
            }
            String      id      = idStr.toString();
            if (id.length() <= 2) { // wrong 'variable term (name is to short)
                return new MatchResult(false);
            }
            // remove quotes
            id = id.substring(1, id.length() - 1);
            //store other to be stored into this variable upon complete match
            result.addBinding(id, other);
            return result;
        } else if ( ! (other instanceof Term)) {
            return new MatchResult(false);
        }
        // 'other' is a term
        Term otherTerm = (Term) other;

        if ( ! mFunctionalCharacter.equals(otherTerm.mFunctionalCharacter)) {
            // functional characters do not match
            if ( ! (mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER_EXTERNAL) &&
                    otherTerm.mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER)   )
               ) {
                // however this only unacceptable when ! (this == 'Variable AND other == 'variable)
                // e.g 'Variable must match 'variable
                return new MatchResult(false);
            }
        } else if (mBody.size() != otherTerm.mBody.size()) {
            return new MatchResult(false);
        }

        // same functional character & same number of arguments
        MatchResult     result      = new MatchResult(true);
        Iterator<Value> thisIter    = iterator();
        Iterator<Value> otherIter   = otherTerm.iterator();
        while (thisIter.hasNext() && otherIter.hasNext()) {
            Value       thisMember  = thisIter .next();
            Value       otherMember = otherIter.next();
            MatchResult subResult   = thisMember.matchesTerm(otherMember);
            if (subResult.isMatch()) {
                result.addBindings(subResult);
            } else {
                return new MatchResult(false);
            }
        }
        if (thisIter.hasNext() || otherIter.hasNext()) {
            // this should not happen, as sizes are the same
            return new MatchResult(false);
        }

        // all members match
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
            if (cmp != 0 && (
                    (
                        mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER_EXTERNAL) &&
                        other.mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER)
                    ) || (
                        mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER) &&
                        other.mFunctionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER_EXTERNAL)
                    )
                )
            ) {
                // these are regarded as one and the same
                cmp = 0;
            }
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

