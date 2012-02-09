package interpreter.utilities;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.CollectionValue;
import interpreter.types.SetlList;
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
}

