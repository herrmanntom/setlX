package org.randoom.setlx.utilities;


import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.List;

public class ConvertSetlTypes {

    public static List convertSetlList(SetlList setlList) throws SetlException {

        List returnList = new ArrayList();
        Value member;
        for(int i =1; i<setlList.size()+1; i++){
            member = setlList.getMember(i);
            if(member instanceof SetlList){
                returnList.add(convertSetlList((SetlList) member));
            }else {

                double m = member.toDouble(new State()).jDoubleValue();

                returnList.add(m);
            }
        }
        return returnList;
    }

}
