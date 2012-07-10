package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;

public abstract class Constructor {
    public abstract void        fillCollection(final CollectionValue collection) throws SetlException;

    // sets the variables used to construct this list to the variables from the list given as a parameter
    public          void        assignUncloned(final SetlList list) throws SetlException {
        throw new UndefinedOperationException(
            "Error in \"" + this + "\":\n" +
            "Only explicit lists can be used as targets for list assignments."
        );
    }

    /* String operations */

    public abstract void        appendString(final StringBuilder sb);

    public final    String      toString() {
        final StringBuilder sb = new StringBuilder();
        appendString(sb);
        return sb.toString();
    }

    /* term operations */

    public abstract void        addToTerm(final CollectionValue collection);

    public static   Constructor CollectionValueToConstructor(final CollectionValue value) throws TermConversionException {
        if (value.size() == 1 && value.firstMember() instanceof Term) {
            final Term    term    = (Term) value.firstMember();
            final String  fc      = term.functionalCharacter().getUnquotedString();
            if (fc.equals(Iteration.FUNCTIONAL_CHARACTER)) {
                return Iteration.termToIteration(term);
            } else if (fc.equals(Range.FUNCTIONAL_CHARACTER)) {
                return Range.termToRange(term);
            } else {
                // assume explicit list of a single term
                return ExplicitList.collectionValueToExplicitList(value);
            }
        } else {
            // assume explicit list;
            return ExplicitList.collectionValueToExplicitList(value);
        }
    }
}

