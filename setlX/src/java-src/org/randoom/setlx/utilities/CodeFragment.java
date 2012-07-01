package org.randoom.setlx.utilities;

import org.randoom.setlx.types.Value;

public abstract class CodeFragment {

    /* string operations */

    public abstract String toString(final int tabs);

    /* term operations */

    public abstract Value toTerm();
}

