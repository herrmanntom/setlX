package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;

import java.util.List;

public class CheckType {

    public static boolean isCanvas(Object p){
        return (p instanceof Canvas);
    }

    public static boolean isSetlList(Value p){
        return (p.isList().equalTo(SetlBoolean.TRUE));
    }

    public static boolean isSetlListofSetlList(Value p) {
        if (!isSetlList(p)) {
            return false;
        }
        SetlList p1 = (SetlList) p;
        for(Value v: p1){
            if(!isSetlList(v)){
                return false;
            }
        }
        return true;
    }

    public static boolean isSetlListWithNumbers(SetlList l){
        boolean returnValue = isSetlList(l);
        for (Value v : l) {
            if (!(isSetlNumber(v))) {
                returnValue = false;
            }
        }
        return returnValue;
    }

    public static boolean isSetlListWithStrings(SetlList l){
        boolean returnValue = isSetlList(l);
        for (Value v : l) {
            if (!(isSetlString(v))) {
                returnValue = false;
            }
        }
        return returnValue;
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

    public static boolean isSetlListofSetlListWithNumbers(Value value) {
        boolean returnValue = true;
        if (!isSetlList(value)) {
            return false;
        }
        SetlList outerList = (SetlList) value;
        for(Value innerList: outerList){
            if(!isSetlList(innerList)){
                return false;
            }
            SetlList list = (SetlList)innerList;
            for(Value v: list){
                returnValue = isSetlNumber(v);
            }
        }
        return returnValue;
    }
}
