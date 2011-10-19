package interpreter.utilities;

import interpreter.exceptions.SetlException;
import interpreter.types.CollectionValue;
import interpreter.types.Value;

public abstract class Constructor {
    public abstract void        fillCollection(CollectionValue collection) throws SetlException;

    public abstract String      toString(int tabs);
}


