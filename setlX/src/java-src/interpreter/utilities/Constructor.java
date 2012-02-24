package interpreter.utilities;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.CollectionValue;
import interpreter.types.SetlList;
import interpreter.types.Term;
import interpreter.types.Value;

public abstract class Constructor {
    public abstract void        fillCollection(CollectionValue collection) throws SetlException;

    // sets the variables used to construct this list to the variables from the list given as a parameter
    public void setIds(SetlList list) throws SetlException {
        throw new UndefinedOperationException("Error in \"" + this + "\":\n"
                                        +     "Only explicit lists of variables can be used as targets for list assignments.");
    }

    /* String operations */

    public abstract String      toString(int tabs);

    /* term operations */

    public abstract void        addToTerm(CollectionValue collection);

    public static   Constructor CollectionValueToConstructor(CollectionValue value) throws TermConversionException {
        if (value.size() == 1 && value.firstMember() instanceof Term) {
            Term    term    = (Term) value.firstMember();
            String  fc      = term.functionalCharacter().getUnquotedString();
            if (fc == Iteration.FUNCTIONAL_CHARACTER) {
                return Iteration.TermToIteration(term);
            } else if (fc == Range.FUNCTIONAL_CHARACTER) {
                return Range.TermToRange(term);
            } else {
                // assume explicit list of a single term
                return ExplicitList.CollectionValueToExplicitList(value);
            }
        } else {
            // assume explicit list;
            return ExplicitList.CollectionValueToExplicitList(value);
        }
    }
}

