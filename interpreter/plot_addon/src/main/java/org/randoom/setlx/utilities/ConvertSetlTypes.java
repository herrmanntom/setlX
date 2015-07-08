package org.randoom.setlx.utilities;


import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NotAnIntegerException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.List;

public class ConvertSetlTypes {

    public static List convertSetlListAsDouble(SetlList setlList) throws SetlException {

        List returnList = new ArrayList();
        Value member;
        for (int i = 1; i < setlList.size() + 1; i++) {
            member = setlList.getMember(i);
            if (member instanceof SetlList) {
                returnList.add(convertSetlListAsDouble((SetlList) member));
            } else {

                double m = member.toDouble(new State()).jDoubleValue();

                returnList.add(m);
            }
        }
        return returnList;
    }

    public static List convertSetlListAsInteger(SetlList setlList) throws SetlException {
        List returnList = new ArrayList();
        Value member;
        for (int i = 1; i < setlList.size() + 1; i++) {
            member = setlList.getMember(i);
            if (member instanceof SetlList) {
                returnList.add(convertSetlListAsInteger((SetlList) member));
            } else {

                int m = member.jIntValue();

                returnList.add(m);
            }
        }
        return returnList;
    }

    public static List convertSetlListAsString(SetlList setlList) throws SetlException {
        List returnList = new ArrayList();
        Value member;
        for (int i = 1; i < setlList.size() + 1; i++) {
            member = setlList.getMember(i);
            if (member instanceof SetlList) {
                returnList.add(convertSetlListAsString((SetlList) member));
            } else {

                String m = member.toString().replace("\"", "");

                returnList.add(m);
            }
        }
        return returnList;
    }

    public static double convertNumberToDouble(Value v) throws IncompatibleTypeException, NumberToLargeException, NotAnIntegerException {
        if(v.isDouble().equalTo(SetlBoolean.TRUE)){
            return v.jDoubleValue();
        }else{
            return v.jIntValue();
        }
    }

}
