package org.randoom.setlx.utilities;

import org.randoom.setlx.types.Value;

public abstract class CodeFragment {

    /* string operations */

    public abstract void appendString(final StringBuilder sb, final int tabs);

    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        appendString(sb, 0);
        return sb.toString();
    }

    /* term operations */

    public abstract Value toTerm();
}

