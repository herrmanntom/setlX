package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.expressions.Expr;
import interpreter.utilities.Environment;
import interpreter.utilities.ParseSetlX;

import java.util.List;

public class SetlString extends Value {

    public static SetlString createFromParserString(String s) {
        // Strip out double qoutes which the parser left in
        s                       = s.substring(1, s.length() - 1);
        // parse escape sequences
        int           length    = s.length();
        StringBuilder sb        = new StringBuilder(length);
        for (int i = 0; i < length; ) {
            char c = s.charAt(i);                          // current char
            char n = (i+1 < length)? s.charAt(i+1) : '\0'; // next char
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
                    sb.append('\\');
                    i -= 1; // char sequence read was only one char long
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

    public SetlString(String s){
        mString = s;
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
            return new SetlInt(mString);
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

    public SetlInt absoluteValue() throws IncompatibleTypeException {
        if (mString.length() == 1) {
            return new SetlInt((int) mString.charAt(0));
        } else {
            throw new IncompatibleTypeException("Operand of 'abs(" + this + ")' is not a singe character.");
        }
    }

    public SetlString add(Value summand) throws IncompatibleTypeException {
        if (summand instanceof SetlString) {
            SetlString s      = (SetlString) summand;
            return new SetlString(mString.concat(s.mString));
        } else {
            return new SetlString(mString.concat(summand.toString()));
        }
    }

    public SetlString addFlipped(Value summand) throws IncompatibleTypeException {
        return new SetlString(summand.toString().concat(mString));
    }

    public SetlString multiply(Value multiplier) throws SetlException {
        if (multiplier instanceof SetlInt) {
            int    m      = ((SetlInt) multiplier).intValue();
            String result = "";
            for (int i = 0; i < m; ++i) {
                result += mString;
            }
            return new SetlString(result);
        } else {
            throw new IncompatibleTypeException("String multiplier '" + multiplier + "' is not an integer.");
        }
    }

    /* operations on compound values (Lists, Sets [, Strings]) */

    public SetlString getMember(Value vIndex) throws SetlException {
        int index = 0;
        if (vIndex instanceof SetlInt) {
            index = ((SetlInt)vIndex).intValue();
        } else {
            throw new IncompatibleTypeException("Index '" + vIndex + "' is not an integer.");
        }
        if (index > mString.length()) {
            throw new NumberToLargeException("Index '" + index + "' is larger as size '" + mString.length() + "' of string '" + mString + "'.");
        }
        return new SetlString(mString.substring(index - 1, index));
    }

    public Value getMembers(Value vLow, Value vHigh) throws SetlException {
        int low = 0, high = 0;
        if (vLow instanceof SetlInt) {
            low = ((SetlInt)vLow).intValue();
        } else {
            throw new IncompatibleTypeException("Lower bound '" + vLow + "' is not an integer.");
        }
        if (vHigh instanceof SetlInt) {
            high = ((SetlInt)vHigh).intValue();
        } else {
            throw new IncompatibleTypeException("Upper bound '" + vHigh + "' is not an integer.");
        }
        if (high > mString.length()) {
            throw new NumberToLargeException("Upper bound '" + high + "' is larger as size '" + mString.length() + "' of string '" + mString + "'.");
        }
        String result = mString.substring(low - 1, high);
        return new SetlString(result);
    }

    public int size() {
        return mString.length();
    }

    /* calls (element access) */

    public Value call(List<Expr> exprs, List<Value> args) throws SetlException {
        int   aSize  = args.size();
        Value vFirst = (aSize >= 1)? args.get(0) : null;
        if (args.contains(RangeDummy.RD)) {
            if (aSize == 2 && vFirst == RangeDummy.RD) {
                // everything up to high boundary: this(  .. y);
                return getMembers(new SetlInt(1), args.get(1));

            } else if (aSize == 2 && args.get(1) == RangeDummy.RD) {
                // everything from low boundary:   this(x ..  );
                return getMembers(vFirst, new SetlInt(size()));

            } else if (aSize == 3 && args.get(1) == RangeDummy.RD) {
                // full range spec:                this(x .. y);
                return getMembers(vFirst, args.get(2));
            }
            throw new UndefinedOperationException("Can not perform call with arguments '" + args + "' on '" + this + "'; arguments are malformed.");
        } else if (aSize == 1) {
            return getMember(vFirst);
        } else {
            throw new UndefinedOperationException("Can not perform call with arguments '" + args + "' on '" + this + "'; arguments are malformed.");
        }
    }

    /* String and Char operations */

    public SetlString str() {
        return this;
    }

    /* When string is printed 'inside' some SetlX code block (verbose printing),
       all non-printable characters should be replaced with their
       escape sequences using this function.

       However, when using a string as argument for print all
       non-printable characters escape sequence should be left alone
       before passing to System.out                                     */

    public String toString() {
        int           length = mString.length();
        StringBuilder result = new StringBuilder(length);
        if (Environment.isPrintVerbose()) {
            for (int i = 0; i < length; ++i) {
                char c = mString.charAt(i);
                if (c == '\\') {
                    result.append('\\');
                    result.append('\\');
                } else if (c == '\n') {
                    result.append('\\');
                    result.append('n');
                } else if (c == '\r') {
                    result.append('\\');
                    result.append('r');
                } else if (c == '\t') {
                    result.append('\\');
                    result.append('t');
                } else if (c == '"') {
                    result.append('\\');
                    result.append('"');
                } else if (c == '\0') {
                    result.append('\\');
                    result.append('0');
                } else {
                    result.append(c);
                }
            }
            return "\"" + result.toString() + "\"";
        } else {
            StringBuilder expr      = null;  // buffer for inner expr string
            boolean       innerExpr = false; // currently reading inner expr
            for (int i = 0; i < length; ++i) {
                char c = mString.charAt(i);  // current char
                char n = (i+1 < length)? mString.charAt(i+1) : '\0';  // next char
                if (innerExpr) {
                    if (c == '$') {
                        // end of inner expr
                        innerExpr = false;
                        // eval inner expression and add to resulting string
                        try {
                            Expr exp = ParseSetlX.parseStringToExpr(expr.toString());
                            // add inner expr to result
                            result.append(exp.eval());
                        } catch (SetlException se) {
                            result.append("$Error: " + se.getMessage() + "$");
                        }
                    } else {
                        expr.append(c);
                    }
                } else {
                    if (c == '\\' && n == '$') {
                        // escaped dollar
                        result.append('$');
                        i++; // jump over next char
                    } else if (c == '$') {
                        // start inner expression
                        innerExpr = true;
                        expr      = new StringBuilder();
                    } else {
                        // continue outer string
                        result.append(c);
                    }
                }
            }
            if (innerExpr) { // inner expr not complete
                result.append("$Error: closing '$' missing.$");
            }
            return "\"" + result.toString() + "\"";
        }
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
        if (v instanceof SetlString) {
            SetlString str = (SetlString) v;
            return mString.compareTo(str.mString);
        } else if (v instanceof SetlSet || v instanceof SetlList || v instanceof Term || v instanceof ProcedureDefinition || v == Infinity.POSITIVE) {
            // SetlSet, SetlList, Term, ProcedureDefinition and +Infinity are bigger
            return -1;
        } else {
            return 1;
        }
    }
}
