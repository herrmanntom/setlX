package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressions.StringConstructor;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SetlString extends IndexedCollectionValue {
    /* To allow initially `free' cloning, by only marking a clone without
     * actually doing any cloning, this SetlString carries a isClone flag.
     *
     * If the contents of this SetlString is modified `separateFromOriginal()'
     * MUST be called before the modification, which then performs the real cloning,
     * if required.
     *
     * Main benefit of this technique is to perform the real cloning only
     * when a clone is actually modified, thus not performing a time consuming
     * cloning, when the clone is only used read-only, which it is in most cases.
     */

    // this method is used when creating strings from StringConstructor
    public static String parseString(final String s) {
        final StringBuilder out = new StringBuilder(s.length() + 8);
        parseString(s, out);
        return out.toString();
    }

    public static void parseString(final String s, final StringBuilder out) {
        // parse escape sequences
        final int           length    = s.length();
        for (int i = 0; i < length; ) {
            final char c = s.charAt(i);                          // current char
            final char n = (i+1 < length)? s.charAt(i+1) : '\0'; // next char
            if (c == '\\') {
                if (n == '\\') {
                    out.append('\\');
                } else if (n == 'n') {
                    out.append('\n');
                } else if (n == 'r') {
                    out.append('\r');
                } else if (n == 't') {
                    out.append('\t');
                } else if (n == '"') {
                    out.append('"');
                } else if (n == '0') {
                    out.append('\0');
                } else {
                    // seems like not part of known escape sequence
                    out.append(n);
                }
                i += 2;
            } else {
                out.append(c);
                i += 1;
            }
        }
    }

    public static SetlString newLiteral(final String s) {
        final SetlString result = new SetlString();
        // parse escape sequences (only \' is parsed in literals)
        final int           length    = s.length();
        for (int i = 1; i < length - 1; ) {
            final char c = s.charAt(i);                          // current char
            final char n = (i+1 < length)? s.charAt(i+1) : '\0'; // next char
            if (c == '\\' && (n == '\'' || n == '\\')) {
                result.content.append(n);
                i += 2;
            } else {
                result.content.append(c);
                i += 1;
            }
        }
        return result;
    }

    private StringBuilder content;
    // is this strings a clone
    private boolean       isCloned;

    public SetlString(){
        this.content  = new StringBuilder();
        this.isCloned = false; // new strings are not a clone
    }

    public SetlString(final char c){
        this.content    = new StringBuilder();
        this.content.append(c);
        this.isCloned   = false; // new strings are not a clone
    }

    public SetlString(final String string){
        this.content    = new StringBuilder();
        this.content.append(string);
        this.isCloned   = false; // new strings are not a clone
    }

    private SetlString(final StringBuilder content){
        this.content  = content;
        this.isCloned = true;  // strings created from another string ARE a clone (most of the time)
    }

    public static SetlString newSetlStringFromSB(final StringBuilder content){
        final SetlString result = new SetlString(content);
        result.isCloned = false; // strings created from a StringBuilder can possibly be not a clone as well
        return result;
    }

    @Override
    public SetlString clone() {
        /* When cloning, THIS string is marked to be a clone as well.
         *
         * This is done, because even though THIS is the original, it must also be
         * cloned upon modification, otherwise clones which carry the same
         * member characters of THIS string would not notice, e.g.
         * modifications of THIS original would bleed through to the clones.
         */
        isCloned = true;
        return new SetlString(content);
    }

    /* If the contents of THIS string is modified, the following function MUST
     * be called before the modification. It performs the real cloning,
     * if THIS is actually marked as a clone.
     */

    private void separateFromOriginal() {
        if (isCloned) {
            final StringBuilder original = content;
            content = new StringBuilder(original.capacity());
            content.append(original);
            isCloned = false;
        }
    }

    private class SetlStringIterator implements Iterator<Value> {
        private final SetlString    stringShell;
        private final StringBuilder content;
        private final boolean       decending;
        private       int           size;
        private       int           position;

        private SetlStringIterator(final SetlString stringShell, final boolean decending) {
            this.stringShell = stringShell;
            this.content     = stringShell.content;
            this.decending   = decending;
            this.size        = this.content.length();
            if (decending) {
                this.position  = this.size - 1;
            } else {
                this.position  = 0;
            }
        }

        @Override
        public boolean hasNext() {
            return (decending && 0 < position) || ( ! decending && position < size);
        }

        @Override
        public SetlString next() {
            if (decending) {
                return new SetlString(content.charAt(position--));
            } else {
                return new SetlString(content.charAt(position++));
            }
        }

        @Override
        public void remove() {
            stringShell.separateFromOriginal();
            if (decending) {
                content.deleteCharAt(position--);
            } else {
                content.deleteCharAt(position);
            }
            size = content.length();
        }
    }

    @Override
    public Iterator<Value> iterator() {
        return new SetlStringIterator(this, false /*not decending*/);
    }

    @Override
    public Iterator<Value> descendingIterator() {
        return new SetlStringIterator(this, true  /*decending*/);
    }

    /* type checks (sort of boolean operation) */

    @Override
    public SetlBoolean isString() {
        return SetlBoolean.TRUE;
    }

    /* type conversions */

    @Override
    public Value toInteger(final State state) {
        try {
            final Rational result = Rational.valueOf(content.toString());
            if (result.isInteger() == SetlBoolean.TRUE) {
                return result;
            } else {
                return Om.OM;
            }
        } catch (final NumberFormatException nfe) {
            return Om.OM;
        }
    }

    /*package*/ SetlList toList(final State state) {
        final SetlList result = new SetlList(size());
        for (final Value str : this) {
            result.addMember(state, str);
        }
        return result;
    }

    @Override
    public Value toRational(final State state) {
        try {
            return Rational.valueOf(content.toString());
        } catch (final NumberFormatException nfe) {
            return Om.OM;
        }
    }

    @Override
    public Value toReal(final State state) {
        try {
            return Real.valueOf(content.toString());
        } catch (final NumberFormatException nfe) {
            return Om.OM;
        }
    }

    /* arithmetic operations */

    @Override
    public Rational absoluteValue(final State state) throws IncompatibleTypeException {
        if (content.length() == 1) {
            return Rational.valueOf((long) content.charAt(0));
        } else {
            throw new IncompatibleTypeException(
                "Operand of 'abs(" + this + ")' is not a singe character."
            );
        }
    }

    @Override
    public Value product(final State state, final Value multiplier) throws SetlException {
        if (multiplier instanceof Rational) {
            final int m = multiplier.jIntValue();
            if (m < 0) {
                throw new IncompatibleTypeException(
                    "String multiplier '" + multiplier + "' is negative."
                );
            }
            final StringBuilder sb  = new StringBuilder(content.length() * m);
            for (int i = 0; i < m; ++i) {
                sb.append(content);
            }
            return newSetlStringFromSB(sb);
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "String multiplier '" + multiplier + "' is not an integer."
            );
        }
    }

    @Override
    public Value productAssign(final State state, final Value multiplier) throws SetlException {
        if (multiplier instanceof Rational) {
            separateFromOriginal();
            final int    m       = multiplier.jIntValue();
            if (m < 0) {
                throw new IncompatibleTypeException(
                    "String multiplier '" + multiplier + "' is negative."
                );
            }
            final String current = content.toString();
            // clear builder
            content.setLength(0);
            content.ensureCapacity(current.length() * m);
            for (int i = 0; i < m; ++i) {
                content.append(current);
            }
            return this;
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(state, this);
        } else {
            throw new IncompatibleTypeException(
                "String multiplier '" + multiplier + "' is not an integer."
            );
        }
    }

    @Override
    public Value sum(final State state, final Value summand) throws IncompatibleTypeException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand == Om.OM) {
            throw new IncompatibleTypeException(
                "'" + this + " + " + summand + "' is undefined."
            );
        } else  {
            final SetlString result = clone();
            result.separateFromOriginal();
            summand.appendUnquotedString(state, result.content, 0);
            return result;
        }
    }

    @Override
    public Value sumAssign(final State state, final Value summand) throws IncompatibleTypeException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand == Om.OM) {
            throw new IncompatibleTypeException(
                "'" + this + " += " + summand + "' is undefined."
            );
        } else  {
            separateFromOriginal();
            summand.appendUnquotedString(state, content, 0);
            return this;
        }
    }

    public SetlString sumFlipped(final State state, final Value summand) throws IncompatibleTypeException {
        if (summand == Om.OM) {
            throw new IncompatibleTypeException(
                "'" + this + " + " + summand + "' is undefined."
            );
        } else {
            final SetlString result = new SetlString();
            summand.appendUnquotedString(state, result.content, 0);
            result.content.append(content);
            return result;
        }
    }

    /* operations on collection values (Lists, Sets [, Strings]) */

    @Override
    public void addMember(final State state, final Value element) {
        separateFromOriginal();
        element.appendUnquotedString(state, content, 0);
    }

    @Override
    public Value collectionAccess(final State state, final List<Value> args) throws SetlException {
        final int   aSize   = args.size();
        final Value vFirst  = (aSize >= 1)? args.get(0) : null;
        if (args.contains(RangeDummy.RD)) {
            if (aSize == 2 && vFirst == RangeDummy.RD) {
                // everything up to high boundary: this(  .. y);
                return getMembers(state, Rational.ONE, args.get(1));

            } else if (aSize == 2 && args.get(1) == RangeDummy.RD) {
                // everything from low boundary:   this(x ..  );
                return getMembers(state, vFirst, Rational.valueOf(size()));

            } else if (aSize == 3 && args.get(1) == RangeDummy.RD) {
                // full range spec:                this(x .. y);
                return getMembers(state, vFirst, args.get(2));
            }
            throw new UndefinedOperationException(
                "Can not access elements using arguments '" + args + "' on '" + this + "';" +
                " arguments are malformed."
            );
        } else if (aSize == 1) {
            return getMember(state, vFirst);
        } else {
            throw new UndefinedOperationException(
                "Can not access elements using arguments '" + args + "' on '" + this + "';" +
                " arguments are malformed."
            );
        }
    }

    @Override
    public SetlBoolean containsMember(final State state, final Value element) throws IncompatibleTypeException {
        if (content.indexOf(element.getUnquotedString()) >= 0) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }

    @Override
    public Value firstMember() {
        if (size() > 0) {
            return new SetlString(content.charAt(0));
        } else {
            return new SetlString();
        }
    }

    @Override
    public SetlString getMember(final int index) throws SetlException {
        if (index > content.length()) {
            throw new NumberToLargeException(
                "Index '" + index + "' is larger as size '" + content.length() + "' of string '" + content.toString() + "'."
            );
        }
        if (index < 1) {
            throw new NumberToLargeException(
                "Index '" + index + "' is lower as 1."
            );
        }
        return new SetlString(content.substring(index - 1, index));
    }

    @Override
    public SetlString getMember(final State state, final Value vIndex) throws SetlException {
        int index = 0;
        if (vIndex.isInteger() == SetlBoolean.TRUE) {
            index = vIndex.jIntValue();
        } else {
            throw new IncompatibleTypeException(
                "Index '" + vIndex + "' is not an integer."
            );
        }
        return getMember(index);
    }

    @Override
    public Value getMembers(final State state, final Value vLow, final Value vHigh) throws SetlException {
        int low = 0, high = 0;
        if (vLow.isInteger() == SetlBoolean.TRUE) {
            low = vLow.jIntValue();
        } else {
            throw new IncompatibleTypeException(
                "Lower bound '" + vLow + "' is not an integer."
            );
        }
        if (vHigh.isInteger() == SetlBoolean.TRUE) {
            high = vHigh.jIntValue();
        } else {
            throw new IncompatibleTypeException(
                "Upper bound '" + vHigh + "' is not an integer."
            );
        }
        return getMembers(low, high);
    }

    // getMembers, i.e. substring
    public SetlString getMembers(final int low, final int high) throws SetlException {
        if (low < 1) {
            throw new NumberToLargeException(
                "Lower bound '" + low + "' is lower as 1."
            );
        }
        if (size() == 0) {
            throw new NumberToLargeException(
                "Lower bound '" + low + "' is larger as string size '" + size() + "'."
            );
        }
        if (high > content.length()) {
            throw new NumberToLargeException(
                "Upper bound '" + high + "' is larger as string size '" + size() + "'."
            );
        }
        if (high < low) {
            return new SetlString();
        } else {
            return new SetlString(content.substring(low - 1, high));
        }
    }

    @Override
    public Value lastMember() {
        if (size() > 0) {
            return new SetlString(content.charAt(size() - 1));
        } else {
            return new SetlString();
        }
    }

    @Override
    public Value maximumMember(final State state) throws SetlException {
        throw new UndefinedOperationException(
            "'max(" + this + ")' is undefined."
        );
    }

    @Override
    public Value minimumMember(final State state) throws SetlException {
        throw new UndefinedOperationException(
            "'min(" + this + ")' is undefined."
        );
    }

    @Override
    public Value nextPermutation(final State state) throws SetlException {
        final Value p = toList(state).nextPermutation(state);
        if (p == Om.OM) {
            return p;
        } else {
            return p.sumOfMembers(state, new SetlString());
        }
    }

    @Override
    public SetlSet permutations(final State state) throws SetlException {
        final SetlSet p = toList(state).permutations(state);
        if (p.size() == 0) {
            return p;
        } else {
            final SetlString neutral = new SetlString();
            final SetlSet    result  = new SetlSet();
            for (final Value v : p) {
                result.addMember(state, v.sumOfMembers(state, neutral));
            }
            return result;
        }
    }

    @Override
    public void removeMember(final Value element) throws IncompatibleTypeException {
        final String needle = element.getUnquotedString();
        final int    pos    = content.indexOf(needle);
        if (pos >= 0) {
            separateFromOriginal();
            content.delete(pos, pos + needle.length());
        }
    }

    @Override
    public Value removeFirstMember() {
        if (size() < 1) {
            return Om.OM;
        }
        separateFromOriginal();
        final char result = content.charAt(0);
        content.deleteCharAt(0);
        return new SetlString(result);
    }

    @Override
    public Value removeLastMember() {
        final int  index  = size() - 1;
        if (index < 0) {
            return Om.OM;
        }
        separateFromOriginal();
        final char result = content.charAt(index);
        content.deleteCharAt(index);
        return new SetlString(result);
    }

    @Override
    public Value reverse(final State state) {
        final SetlString result = clone();
        result.separateFromOriginal();
        result.content.reverse();
        return result;
    }

    @Override
    public void setMember(final State state, final Value vIndex, final Value v) throws SetlException {
        separateFromOriginal();
        int index = 0;
        if (vIndex.isInteger() == SetlBoolean.TRUE) {
            index = vIndex.jIntValue();
        } else {
            throw new IncompatibleTypeException(
                "Index '" + vIndex + "' is not a integer."
            );
        }
        if (index < 1) {
            throw new NumberToLargeException(
                "Index '" + index + "' is lower as '1'."
            );
        }
        if (v == Om.OM) {
            throw new IncompatibleTypeException(
                "Target value is undefined (om)."
            );
        }

        final String value = v.getUnquotedString();

        // in java the index is one lower
        --index;

        if (index >= content.length()) {
            content.ensureCapacity(index + value.length());
            // fill gap from size to index with banks, if necessary
            while (index >= content.length()) {
                content.append(" ");
            }
        }
        // remove char at index
        content.deleteCharAt(index);
        // insert value at index
        content.insert(index, value);
    }

    @Override
    public SetlString shuffle(final State state) throws IncompatibleTypeException {
        final List<String> shuffled = Arrays.asList(content.toString().split(""));

        Collections.shuffle(shuffled, state.getRandom());

        final SetlString result = new SetlString();
        for (final String c : shuffled) {
            result.content.append(c);
        }
        return result;
    }

    @Override
    public int size() {
        return content.length();
    }

    @Override
    public SetlString sort(final State state) throws IncompatibleTypeException {
        final char[] chars = content.toString().toCharArray();
        Arrays.sort(chars);
        return new SetlString(new String(chars));
    }

    @Override
    public SetlList split(final State state, final Value pattern) throws IncompatibleTypeException, SyntaxErrorException {
        if ( ! (pattern instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Pattern '" + pattern  + "' is not a string."
            );
        }
        final String p = ((SetlString) pattern).getUnquotedString();

        try {
            // parse pattern
            final Pattern pttrn = Pattern.compile(p);

            final List<String> strings = Arrays.asList(pttrn.split(content));

            final SetlList     result  = new SetlList(strings.size());
            for (final String str : strings) {
                result.addMember(state, new SetlString(str));
            }

            /* some fixes to make the output a bit less confusing */

            // fix split("foo", "") => ["", "f", "o", "o"], should be ["", "f", "o", "o"]
            if (strings.size() >= 1 && strings.get(0).equals("") && p.equals("")) {
                result.removeFirstMember();
            }
            // fix split(";", ";") => [], should be ["", ""]
            else if (content.toString().equals(p)) {
                result.addMember(state, new SetlString());
                result.addMember(state, new SetlString());
            }
            // fix split(";f;o;o;", ";") => ["", "f", "o", "o"], should be ["", "f", "o", "o", ""]
            else if (content.toString().endsWith(p)) {
                result.addMember(state, new SetlString());
            }

            return result;
        } catch (final PatternSyntaxException pse) {
            final LinkedList<String> errors = new LinkedList<String>();
            errors.add("Error while parsing regex-pattern '" + p + "' {");
            errors.add("\t" + pse.getDescription() + " near index " + (pse.getIndex() + 1));
            errors.add("}");
            throw SyntaxErrorException.create(
                errors,
                "1 syntax error encountered."
            );
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("\"");
        sb.append(content);
        sb.append("\"");
    }

    @Override
    public void appendUnquotedString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(content);
    }

    @Override
    public void canonical(final State state, final StringBuilder sb) {
        appendString(state, sb, 0);
    }

    public String getEscapedString() {
        // parse escape sequences
        final int           length  = content.length();
        final StringBuilder sb      = new StringBuilder(length + 8);
        for (int i = 0; i < length; ++i) {
            final char c = content.charAt(i);  // current char
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

    @Override
    public String getUnquotedString() {
        return content.toString();
    }

    @Override
    public SetlString str(final State state) {
        return this;
    }

    /* term operations */

    @Override
    public MatchResult matchesTerm(final State state, final Value other) {
        if (other == IgnoreDummy.ID || this.equals(other)) {
            return new MatchResult(true);
        } else if (other instanceof Term ) {
            final Term o = (Term) other;
            try {
                if (o.functionalCharacter(state).getUnquotedString().equals(StringConstructor.getFunctionalCharacter())
                    && o.size() == 2 && o.firstMember().size() == 1 && o.lastMember().size() == 0
                ) {
                    return matchesTerm(state, o.firstMember().firstMember(state));
                }
            } catch (final SetlException e) { /* just fail in the next line */ }
            return new MatchResult(false);
        } else {
            return new MatchResult(false);
        }
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
        } else if (v instanceof SetlString) {
            return content.toString().compareTo(((SetlString) v).content.toString());
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
        return 600;
    }

    @Override
    public boolean equalTo(final Value v) {
        if (this == v) {
            return true;
        } else if (v instanceof SetlString) {
            final StringBuilder other = ((SetlString) v).content;
            if (content.length() == other.length()) {
                return content.toString().equals(other.toString());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private final static int initHashCode = SetlString.class.hashCode();

    @Override
    public int hashCode() {
        return initHashCode + content.toString().hashCode();
    }
}

