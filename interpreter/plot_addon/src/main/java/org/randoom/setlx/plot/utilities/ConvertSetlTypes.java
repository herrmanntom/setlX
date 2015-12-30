package org.randoom.setlx.plot.utilities;


import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.NotAnIntegerException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.List;

public class ConvertSetlTypes {

    public static List<Double> convertSetlListToListOfDouble(SetlList setlList, State state) throws SetlException {

        List<Double> returnList = new ArrayList<>();
        Value member;
        for (int i = 1; i < setlList.size() + 1; i++) {
            member = setlList.getMember(i);

            double m = member.toDouble(state).jDoubleValue();

            returnList.add(m);
        }
        return returnList;
    }

    public static List<List<Double>> convertSetlListToListOfListOfDouble(SetlList setlList, State state) throws SetlException {
        List<List<Double>> returnList = new ArrayList<>();
        Value member;
        for (int i = 1; i < setlList.size() + 1; i++) {
            member = setlList.getMember(i);
            if (member instanceof SetlList) {
                returnList.add(convertSetlListToListOfDouble((SetlList) member, state));
            } else {
                throw new IncompatibleTypeException("List of List of numbers expected");
            }
        }
        return returnList;
    }

    public static List<Integer> convertSetlListToListOfInteger(SetlList setlList) throws SetlException {
        List<Integer> returnList = new ArrayList<>();
        Value member;
        for (int i = 1; i < setlList.size() + 1; i++) {
            member = setlList.getMember(i);

            int m = member.jIntValue();

            returnList.add(m);
        }
        return returnList;
    }

    public static List<String> convertSetlListToListOfString(SetlList setlList) throws SetlException {
        List<String> returnList = new ArrayList<>();
        Value member;
        for (int i = 1; i < setlList.size() + 1; i++) {
            member = setlList.getMember(i);

            String m = member.toString().replace("\"", "");

            returnList.add(m);
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
