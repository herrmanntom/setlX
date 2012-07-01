package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;

import java.util.List;

public class SetlString extends Value {

    public static SetlString createFromConstructor(String s) {
        // parse escape sequences
        final int           length    = s.length();
        final StringBuilder sb        = new StringBuilder(length);
        for (int i = 0; i < length; ) {
            final char c = s.charAt(i);                          // current char
            final char n = (i+1 < length)? s.charAt(i+1) : '\0'; // next char
            if (c == '\\') {
                if (n == '\\') {
                    sb.append('\\');
                } else if (n == 'n') {
                    sb.append('\n');
                } else if (n == 'r') {
                    sb.append('\r');
                } else if (n == 't') {
                    sb.append('\t');
                } else if (n == '"') {
                    sb.append('"');
                } else if (n == '0') {
                    sb.append('\0');
                } else {
                    // seems like not part of known escape sequence
                    sb.append(n);
                }
                i += 2;
            } else {
                sb.append(c);
                i += 1;
            }
        }
        return new SetlString(sb.toString());
    }

    private String mString;

    public SetlString(String string){
        mString = string;
    }

    public SetlString clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }

    /* type checks (sort of boolean operation) */

    public SetlBoolean isString() {
        return SetlBoolean.TRUE;
    }

    /* type conversions */

    public Value toInteger() {
        try {
            final Rational result = new Rational(mString);
            if (result.isInteger() == SetlBoolean.TRUE) {
                return result;
            } else {
                return Om.OM;
            }
        } catch (NumberFormatException nfe) {
            return Om.OM;
        }
    }

    public Value toRational() {
        try {
            return new Rational(mString);
        } catch (NumberFormatException nfe) {
            return Om.OM;
        }
    }

    public Value toReal() {
        try {
            return new Real(mString);
        } catch (NumberFormatException nfe) {
            return Om.OM;
        }
    }

    /* arithmetic operations */

    public Rational absoluteValue() throws IncompatibleTypeException {
        if (mString.length() == 1) {
            return new Rational((int) mString.charAt(0));
        } else {
            throw new IncompatibleTypeException(
                "Operand of 'abs(" + this + ")' is not a singe character."
            );
        }
    }

    public Value multiply(Value multiplier) throws SetlException {
        if (multiplier instanceof Rational) {
            final int           m   = ((Rational) multiplier).intValue();
            final StringBuilder sb  = new StringBuilder(mString.length() * m);
            for (int i = 0; i < m; ++i) {
                sb.append(mString);
            }
            return new SetlString(sb.toString());
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).multiplyFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "String multiplier '" + multiplier + "' is not an integer."
            );
        }
    }

    public Value sum(Value summand) throws IncompatibleTypeException {
        if (summand instanceof SetlString) {
            return new SetlString(mString.concat(((SetlString) summand).mString));
        } else if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(this);
        } else if (summand != Om.OM) {
            return new SetlString(mString.concat(summand.toString()));
        } else {
            throw new IncompatibleTypeException(
                "'" + this + " + " + summand + "' is undefined."
            );
        }
    }

    public SetlString sumFlipped(Value summand) throws IncompatibleTypeException {
        if (summand != Om.OM) {
            return new SetlString(summand.toString().concat(mString));
        } else {
            throw new IncompatibleTypeException(
                "'" + this + " + " + summand + "' is undefined."
            );
        }
    }

    /* operations on collection values (Lists, Sets [, Strings]) */

    public Value collectionAccess(List<Value> args) throws SetlException {
        final int   aSize   = args.size();
        final Value vFirst  = (aSize >= 1)? args.get(0) : null;
        if (args.contains(RangeDummy.RD)) {
            if (aSize == 2 && vFirst == RangeDummy.RD) {
                // everything up to high boundary: this(  .. y);
                return getMembers(new Rational(1), args.get(1));

            } else if (aSize == 2 && args.get(1) == RangeDummy.RD) {
                // everything from low boundary:   this(x ..  );
                return getMembers(vFirst, new Rational(size()));

            } else if (aSize == 3 && args.get(1) == RangeDummy.RD) {
                // full range spec:                this(x .. y);
                return getMembers(vFirst, args.get(2));
            }
            throw new UndefinedOperationException(
                "Can not access elements using arguments '" + args + "' on '" + this + "';" +
                " arguments are malformed."
            );
        } else if (aSize == 1) {
            return getMember(vFirst);
        } else {
            throw new UndefinedOperationException(
                "Can not access elements using arguments '" + args + "' on '" + this + "';" +
                " arguments are malformed."
            );
        }
    }

    public SetlBoolean containsMember(Value element) throws IncompatibleTypeException {
        if ( ! (element instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Left-hand-side of '" + element  + " in " + this + "' is not a string."
            );
        }
        if (mString.contains(((SetlString) element).mString)) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }

    public SetlString getMember(Value vIndex) throws SetlException {
        int index = 0;
        if (vIndex.isInteger() == SetlBoolean.TRUE) {
            index = ((Rational)vIndex).intValue();
        } else {
            throw new IncompatibleTypeException(
                "Index '" + vIndex + "' is not an integer."
            );
        }
        if (index > mString.length()) {
            throw new NumberToLargeException(
                "Index '" + index + "' is larger as size '" + mString.length() + "' of string '" + mString + "'."
            );
        }
        if (index < 1) {
            throw new NumberToLargeException(
                "Index '" + index + "' is lower as 1."
            );
        }
        return new SetlString(mString.substring(index - 1, index));
    }

    public Value getMembers(Value vLow, Value vHigh) throws SetlException {
        int low = 0, high = 0;
        if (vLow.isInteger() == SetlBoolean.TRUE) {
            low = ((Rational)vLow).intValue();
        } else {
            throw new IncompatibleTypeException(
                "Lower bound '" + vLow + "' is not an integer."
            );
        }
        if (vHigh.isInteger() == SetlBoolean.TRUE) {
            high = ((Rational)vHigh).intValue();
        } else {
            throw new IncompatibleTypeException(
                "Upper bound '" + vHigh + "' is not an integer."
            );
        }
        if (low < 1) {
            throw new NumberToLargeException(
                "Lower bound '" + low + "' is lower as 1."
            );
        }
        if (size() == 0) {
            throw new NumberToLargeException(
                "Lower bound '" + low + "' is larger as list size '" + size() + "'."
            );
        }
        if (high > mString.length()) {
            throw new NumberToLargeException(
                "Upper bound '" + high + "' is larger as size '" + mString.length() + "' of string '" + mString + "'."
            );
        }
        return new SetlString(mString.substring(low - 1, high));
    }

    public SetlString reverse() {
        return new SetlString((new StringBuilder(mString)).reverse().toString());
    }

    public int size() {
        return mString.length();
    }

    public SetlList split(Value pattern) throws IncompatibleTypeException {
        if ( ! (pattern instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Pattern '" + pattern  + "' is not a string."
            );
        }
        final String    p       = pattern.getUnquotedString();
        final String[]  strings = mString.split(p);
        final SetlList  result  = new SetlList();
        for (final String str : strings) {
            result.addMember(new SetlString(str));
        }

        // fix split("foo", "") => ["", "f", "o", "o"], should be ["", "f", "o", "o"]
        if (strings.length >= 1 && strings[0].equals("") && p.equals("")) {
            result.removeFirstMember();
        }
        // fix split(";", ";") => [], should be ["", ""]
        else if (mString.equals(p)) {
            result.addMember(new SetlString(""));
            result.addMember(new SetlString(""));
        }
        // fix split(";f;o;o;", ";") => ["", "f", "o", "o"], should be ["", "f", "o", "o", ""]
        else if (mString.endsWith(p)) {
            result.addMember(new SetlString(""));
        }

        return result;
    }

    /* string and char operations */

    public SetlString str() {
        return this;
    }

    public String getUnquotedString() {
        return mString;
    }

    public String getEscapedString() {
        // parse escape sequences
        final int           length  = mString.length();
        final StringBuilder sb      = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            final char c = mString.charAt(i);  // current char
            if (c == '\\') {
                sb.append("\\\\");
            } else if (c == '\n') {
                sb.append("\\n");
            } else if (c == '\r') {
                sb.append("\\r");
            } else if (c == '\t') {
                sb.append("\\t");
            } else if (c == '"') {
                sb.append("\\\"");
            } else if (c == '\0') {
                sb.append("\\0");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public String toString() {
        return "\"" + mString + "\"";
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
    public int compareTo(Value v){
        if (v instanceof SetlString) {
            return mString.compareTo(((SetlString) v).mString);
        } else if (v instanceof SetlSet || v instanceof SetlList || v instanceof Term ||
                   v instanceof ProcedureDefinition || v == Infinity.POSITIVE) {
            // SetlSet, SetlList, Term, ProcedureDefinition and +Infinity are bigger
            return -1;
        } else {
            return 1;
        }
    }
}
