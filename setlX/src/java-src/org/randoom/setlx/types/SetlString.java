package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.MatchResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SetlString extends CollectionValue {
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

    private StringBuilder mContent;
    // is this strings a clone
    private boolean       mIsCloned;

    public SetlString(){
        mContent    = new StringBuilder();
        mIsCloned   = false; // new strings are not a clone
    }

    public SetlString(final char c){
        mContent    = new StringBuilder();
        mContent.append(c);
        mIsCloned   = false; // new strings are not a clone
    }

    public SetlString(final String string){
        mContent    = new StringBuilder();
        mContent.append(string);
        mIsCloned   = false; // new strings are not a clone
    }

    private SetlString(final StringBuilder content){
        mContent  = content;
        mIsCloned = true;  // strings created from another string ARE a clone (most of the time)
    }

    public static SetlString newSetlStringFromSB(final StringBuilder content){
        final SetlString result = new SetlString(content);
        result.mIsCloned = false; // strings created from a StringBuilder can possibly be not a clone as well
        return result;
    }

    public SetlString clone() {
        /* When cloning, THIS string is marked to be a clone as well.
         *
         * This is done, because even though THIS is the original, it must also be
         * cloned upon modification, otherwise clones which carry the same
         * member characters of THIS string would not notice, e.g.
         * modifications of THIS original would bleed through to the clones.
         */
        mIsCloned = true;
        return new SetlString(mContent);
    }

    /* If the contents of THIS string is modified, the following function MUST
     * be called before the modification. It performs the real cloning,
     * if THIS is actually marked as a clone.
     */

    private void separateFromOriginal() {
        if (mIsCloned) {
            final StringBuilder original = mContent;
            mContent = new StringBuilder();
            mContent.append(original);
            mIsCloned = false;
        }
    }

    private class SetlStringIterator implements Iterator<Value> {
        private final SetlString content;
        private       int        size;
        private       int        position;

        /*package*/ SetlStringIterator(final SetlString content) {
            this.content  = content;
            this.size     = content.mContent.length();
            this.position = 0;
        }

        public boolean hasNext() {
            return position < size;
        }

        public SetlString next() {
            return new SetlString(content.mContent.charAt(position++));
        }

        public void remove() {
            content.separateFromOriginal();
            content.mContent.deleteCharAt(position);
            size = content.mContent.length();
        }
    }

    public Iterator<Value> iterator() {
        return new SetlStringIterator(this);
    }

    /* type checks (sort of boolean operation) */

    public SetlBoolean isString() {
        return SetlBoolean.TRUE;
    }

    /* type conversions */

    public Value toInteger() {
        try {
            final Rational result = new Rational(mContent.toString());
            if (result.isInteger() == SetlBoolean.TRUE) {
                return result;
            } else {
                return Om.OM;
            }
        } catch (NumberFormatException nfe) {
            return Om.OM;
        }
    }

    /*package*/ SetlList toList() {
        SetlList result = new SetlList(size());
        for (Value str : this) {
            result.addMember(str);
        }
        return result;
    }

    public Value toRational() {
        try {
            return new Rational(mContent.toString());
        } catch (NumberFormatException nfe) {
            return Om.OM;
        }
    }

    public Value toReal() {
        try {
            return new Real(mContent.toString());
        } catch (NumberFormatException nfe) {
            return Om.OM;
        }
    }

    /* arithmetic operations */

    public Rational absoluteValue() throws IncompatibleTypeException {
        if (mContent.length() == 1) {
            return new Rational((int) mContent.charAt(0));
        } else {
            throw new IncompatibleTypeException(
                "Operand of 'abs(" + this + ")' is not a singe character."
            );
        }
    }

    public Value multiply(final Value multiplier) throws SetlException {
        if (multiplier instanceof Rational) {
            final int           m   = ((Rational) multiplier).intValue();
            if (m < 0) {
                throw new IncompatibleTypeException(
                    "String multiplier '" + multiplier + "' is negative."
                );
            }
            final StringBuilder sb  = new StringBuilder(mContent.length() * m);
            for (int i = 0; i < m; ++i) {
                sb.append(mContent);
            }
            return newSetlStringFromSB(sb);
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).multiplyFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "String multiplier '" + multiplier + "' is not an integer."
            );
        }
    }

    public Value multiplyAssign(final Value multiplier) throws SetlException {
        if (multiplier instanceof Rational) {
            separateFromOriginal();
            final int    m       = ((Rational) multiplier).intValue();
            if (m < 0) {
                throw new IncompatibleTypeException(
                    "String multiplier '" + multiplier + "' is negative."
                );
            }
            final String current = mContent.toString();
            // clear builder
            mContent.setLength(0);
            mContent.ensureCapacity(current.length() * m);
            for (int i = 0; i < m; ++i) {
                mContent.append(current);
            }
            return this;
        } else if (multiplier instanceof Term) {
            return ((Term) multiplier).multiplyFlipped(this);
        } else {
            throw new IncompatibleTypeException(
                "String multiplier '" + multiplier + "' is not an integer."
            );
        }
    }

    public Value sum(final Value summand) throws IncompatibleTypeException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(this);
        } else if (summand == Om.OM) {
            throw new IncompatibleTypeException(
                "'" + this + " + " + summand + "' is undefined."
            );
        } else  {
            final SetlString result = clone();
            result.separateFromOriginal();
            summand.appendUnquotedString(result.mContent, 0);
            return result;
        }
    }

    public Value sumAssign(final Value summand) throws IncompatibleTypeException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(this);
        } else if (summand == Om.OM) {
            throw new IncompatibleTypeException(
                "'" + this + " += " + summand + "' is undefined."
            );
        } else  {
            separateFromOriginal();
            summand.appendUnquotedString(mContent, 0);
            return this;
        }
    }

    public SetlString sumFlipped(final Value summand) throws IncompatibleTypeException {
        if (summand == Om.OM) {
            throw new IncompatibleTypeException(
                "'" + this + " + " + summand + "' is undefined."
            );
        } else {
            final SetlString result = new SetlString();
            summand.appendUnquotedString(result.mContent, 0);
            result.mContent.append(mContent);
            return result;
        }
    }

    /* operations on collection values (Lists, Sets [, Strings]) */

    public void addMember(final Value element) {
        separateFromOriginal();
        element.appendUnquotedString(mContent, 0);
    }

    public Value collectionAccess(final List<Value> args) throws SetlException {
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

    public SetlBoolean containsMember(final Value element) throws IncompatibleTypeException {
        if (mContent.indexOf(element.getUnquotedString()) >= 0) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }

    public Value firstMember() {
        if (size() > 0) {
            return new SetlString(mContent.charAt(0));
        } else {
            return new SetlString();
        }
    }

    public SetlString getMember(final Value vIndex) throws SetlException {
        int index = 0;
        if (vIndex.isInteger() == SetlBoolean.TRUE) {
            index = ((Rational)vIndex).intValue();
        } else {
            throw new IncompatibleTypeException(
                "Index '" + vIndex + "' is not an integer."
            );
        }
        if (index > mContent.length()) {
            throw new NumberToLargeException(
                "Index '" + index + "' is larger as size '" + mContent.length() + "' of string '" + mContent.toString() + "'."
            );
        }
        if (index < 1) {
            throw new NumberToLargeException(
                "Index '" + index + "' is lower as 1."
            );
        }
        return new SetlString(mContent.substring(index - 1, index));
    }

    public Value getMembers(final Value vLow, final Value vHigh) throws SetlException {
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
        if (high > mContent.length()) {
            throw new NumberToLargeException(
                "Upper bound '" + high + "' is larger as size '" + mContent.length() + "' of string '" + mContent.toString() + "'."
            );
        }
        if (high < low) {
            return new SetlString();
        } else {
            return new SetlString(mContent.substring(low - 1, high));
        }
    }

    public Value lastMember() {
        if (size() > 0) {
            return new SetlString(mContent.charAt(size() - 1));
        } else {
            return new SetlString();
        }
    }

    public Value maximumMember() throws SetlException {
        throw new UndefinedOperationException(
            "'max(" + this + ")' is undefined."
        );
    }

    public Value minimumMember() throws SetlException {
        throw new UndefinedOperationException(
            "'min(" + this + ")' is undefined."
        );
    }

    public Value nextPermutation() throws SetlException {
        final Value p = toList().nextPermutation();
        if (p == Om.OM) {
            return p;
        } else {
            return p.sumMembers(new SetlString());
        }
    }

    public SetlSet permutations() throws SetlException {
        final SetlSet p = toList().permutations();
        if (p.size() == 0) {
            return p;
        } else {
            SetlString neutral = new SetlString();
            SetlSet    result  = new SetlSet();
            for (final Value v : p) {
                result.addMember(v.sumMembers(neutral));
            }
            return result;
        }
    }

    public void removeMember(final Value element) throws IncompatibleTypeException {
        final String needle = element.getUnquotedString();
        final int    pos    = mContent.indexOf(needle);
        if (pos >= 0) {
            separateFromOriginal();
            mContent.delete(pos, needle.length());
        }
    }

    public void removeFirstMember() {
        separateFromOriginal();
        mContent.deleteCharAt(0);
    }

    public void removeLastMember() {
        separateFromOriginal();
        mContent.deleteCharAt(size() - 1);
    }

    public SetlString reverse() {
        final SetlString result = clone();
        result.separateFromOriginal();
        result.mContent.reverse();
        return result;
    }

    public void setMember(final Value vIndex, final Value v) throws SetlException {
        separateFromOriginal();
        int index = 0;
        if (vIndex.isInteger() == SetlBoolean.TRUE) {
            index = ((Rational)vIndex).intValue();
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

        String value = v.getUnquotedString();

        // in java the index is one lower
        index--;

        if (index >= mContent.length()) {
            mContent.ensureCapacity(index + value.length());
            // fill gap from size to index with banks, if necessary
            while (index >= mContent.length()) {
                mContent.append(" ");
            }
        }
        // remove char at index
        mContent.deleteCharAt(index);
        // insert value at index
        mContent.insert(index, value);
    }

    public SetlString shuffle() throws IncompatibleTypeException {
        final List<String> shuffled = Arrays.asList(mContent.toString().split(""));

        Collections.shuffle(shuffled, Environment.getRandom());

        final SetlString result = new SetlString();
        for (int i = 0; i < shuffled.size(); i++) {
            result.mContent.append(shuffled.get(i));
        }
        return result;
    }

    public int size() {
        return mContent.length();
    }

    public SetlString sort() throws IncompatibleTypeException {
        final char[] chars = mContent.toString().toCharArray();
        Arrays.sort(chars);
        return new SetlString(new String(chars));
    }

    public SetlList split(final Value pattern) throws IncompatibleTypeException {
        if ( ! (pattern instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Pattern '" + pattern  + "' is not a string."
            );
        }
        final String       p       = pattern.getUnquotedString();
        final List<String> strings = Arrays.asList(mContent.toString().split(p));
        final SetlList     result  = new SetlList(strings.size());
        for (final String str : strings) {
            result.addMember(new SetlString(str));
        }

        // fix split("foo", "") => ["", "f", "o", "o"], should be ["", "f", "o", "o"]
        if (strings.size() >= 1 && strings.get(0).equals("") && p.equals("")) {
            result.removeFirstMember();
        }
        // fix split(";", ";") => [], should be ["", ""]
        else if (mContent.toString().equals(p)) {
            result.addMember(new SetlString());
            result.addMember(new SetlString());
        }
        // fix split(";f;o;o;", ";") => ["", "f", "o", "o"], should be ["", "f", "o", "o", ""]
        else if (mContent.toString().endsWith(p)) {
            result.addMember(new SetlString());
        }

        return result;
    }

    /* string and char operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        sb.append("\"");
        sb.append(mContent);
        sb.append("\"");
    }

    public void appendUnquotedString(final StringBuilder sb, final int tabs) {
        sb.append(mContent);
    }

    public void canonical(final StringBuilder sb) {
        appendString(sb, 0);
    }

    public String getEscapedString() {
        // parse escape sequences
        final int           length  = mContent.length();
        final StringBuilder sb      = new StringBuilder(length + 8);
        for (int i = 0; i < length; i++) {
            final char c = mContent.charAt(i);  // current char
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

    public String getUnquotedString() {
        return mContent.toString();
    }

    public SetlString str() {
        return this;
    }

    /* term operations */

    public MatchResult matchesTerm(final Value other) {
        if (other == IgnoreDummy.ID || this.equals(other)) {
            return new MatchResult(true);
        } else {
            return new MatchResult(false);
        }
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
    public int compareTo(final Value v){
        if (v instanceof SetlString) {
            return mContent.toString().compareTo(((SetlString) v).mContent.toString());
        } else if (v instanceof SetlSet || v instanceof SetlList || v instanceof Term ||
                   v instanceof ProcedureDefinition || v == Infinity.POSITIVE) {
            // SetlSet, SetlList, Term, ProcedureDefinition and +Infinity are bigger
            return -1;
        } else {
            return 1;
        }
    }

    private final static int initHashCode = SetlString.class.hashCode();

    public int hashCode() {
        return initHashCode + mContent.toString().hashCode();
    }
}

