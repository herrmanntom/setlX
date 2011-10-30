package interpreter.types;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.expressions.Expr;

import java.util.List;

public class SetlString extends Value {

    private String mString;

    public SetlString(String s){
        // Strip out double qoutes which the parser left in
        int length = s.length();
        if (length >= 2 && s.charAt(0) == '"' && s.charAt(length - 1) == '"') {
            s = s.substring(1, length - 1);
        }
        // parse escape sequences
        length              = s.length();
        StringBuilder sb    = new StringBuilder(length);
        for (int i = 0; i < length; ) {
            char c = s.charAt(i);                          // current
            char n = (i+1 < length)? s.charAt(i+1) : '\0'; // next
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
        mString = sb.toString();
    }

    public SetlString clone() {
        // this value is more or less atomic and can not be changed once set
        return this;
    }

    /* type checks (sort of boolean operation) */

    public SetlBoolean isString() {
        return SetlBoolean.TRUE;
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
            throw new NumberToLargeException("Index '" + index + "' is larger as string size '" + mString.length() + "'.");
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
            throw new NumberToLargeException("Upper bound '" + high + "' is larger as string size '" + mString.length() + "'.");
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

    /*  When string is printed 'inside' some compound value (Lists etc),
       all non-printable characters should be replaced with their
       escape sequences using this function.

       However, when using a string as argument for print all
       non-printable characters escape sequence should be left alone
       before passing to System.out                                     */

    public String toString() {
        int           length = mString.length();
        StringBuilder sb     = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            char c = mString.charAt(i);  // current
            if (c == '\\') {
                sb.append('\\');
                sb.append('\\');
            } else if (c == '\n') {
                sb.append('\\');
                sb.append('n');
            } else if (c == '\r') {
                sb.append('\\');
                sb.append('r');
            } else if (c == '\t') {
                sb.append('\\');
                sb.append('t');
            } else if (c == '"') {
                sb.append('\\');
                sb.append('"');
            } else if (c == '\0') {
                sb.append('\\');
                sb.append('0');
            } else {
                sb.append(c);
            }
        }
        return "\"" + sb.toString() + "\"";
    }

    public String toStringForPrint() {
        return mString;
    }

    /* Comparisons */

    // Compare two Values.  Returns -1 if this value is less than the value given
    // as argument, +1 if its greater and 0 if both values contain the same
    // elements.
    // Useful output is only possible if both values are of the same type.
    // "incomparable" values, e.g. of different types are ranked as follows:
    // SetlOm < SetlBoolean < SetlInt & SetlReal < SetlString < SetlSet < SetlList < SetlDefinition
    // This ranking is necessary to allow sets and lists of different types.
    public int compareTo(Value v){
        if (v instanceof SetlString) {
            SetlString str = (SetlString) v;
            return mString.compareTo(str.mString);
        } else if (v instanceof SetlSet || v instanceof SetlList || v instanceof SetlDefinition) {
            // SetlSet, SetlList and SetlDefinition are bigger
            return -1;
        } else {
            return 1;
        }
    }
}
