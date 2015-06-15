package org.randoom.setlx.utilities;

import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;

import java.util.List;

public class CheckType {

    public static boolean isCanvas(Object p){
        return (p instanceof Canvas);
    }

    public static boolean isSetlList(Value p){
        return (p.isList().equalTo(SetlBoolean.TRUE));
    }

    public static boolean isSetlNumber(Value p){
        return ((p.isDouble().equalTo(SetlBoolean.TRUE)) || (p.isInteger().equalTo(SetlBoolean.TRUE)));
    }

    public static boolean isSetlString(Value p){
        return (p.isString().equalTo(SetlBoolean.TRUE));
    }

    public static boolean sameSize(List one, List two){
        return (one.size() == two.size());
    }
}
