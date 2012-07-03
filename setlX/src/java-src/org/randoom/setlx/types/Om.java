package org.randoom.setlx.types;

public class Om extends Value {

    public final static Om OM = new Om();

    // do not display when printing result after evaluating in interactive mode
    private boolean isHidden  = false;

    private Om() {  }

    public Om clone() {
        // this value is atomic and can not be changed
        return this;
    }

    public boolean isHidden() {
        return isHidden && ((isHidden = false) || true); // reset to false after one query
    }

    public Om hide() {
        isHidden = true;
        return this;
    }

    /* string and char operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        sb.append("om");
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
        if (v == OM) {
            return 0;
        } else if (v instanceof SetlError) {
            // only SetlError is smaller as Om
            return 1;
        } else {
            // everything else is bigger than Om
            return -1;
        }
    }
}

