package interpreter.types;

// This class represents a single parameter of a function definition
public class SetlDefinitionParameter {
    public final static int READ_ONLY   = 0;
    public final static int READ_WRITE  = 1;

    private String mId;
    private int    mType;

    public SetlDefinitionParameter(String id) {
        this(id, READ_ONLY);
    }

    public SetlDefinitionParameter(String id, int type) {
        mId   = id;
        mType = type;
    }

    public String getId() {
        return mId;
    }

    public int getType() {
        return mType;
    }

    public String toString() {
        String result = "";
        if (mType == READ_WRITE) {
            result += "rw ";
        }
        result += mId;
        return result;
    }
}

