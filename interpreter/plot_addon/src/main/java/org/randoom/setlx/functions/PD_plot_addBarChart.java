package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.*;
import org.randoom.setlx.utilities.Canvas;
import org.randoom.setlx.utilities.ConnectJFreeChart;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.ConvertSetlTypes;

import java.util.HashMap;
import java.util.List;

public class PD_plot_addBarChart extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef VALUES = createParameter("values");
    private final static ParameterDef CATEGORIES = createParameter("categories");
    private final static ParameterDef NAME = createOptionalParameter("name", Rational.ONE);
    public final static PreDefinedProcedure DEFINITION = new PD_plot_addBarChart();

    private PD_plot_addBarChart() {
        super();
        addParameter(CANVAS);
        addParameter(VALUES);
        addParameter(CATEGORIES);
        addParameter(NAME);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        Canvas canvas;
        SetlList values;
        SetlList categories;

        //First Parameter must be a Canvas object
        try {
            canvas = (Canvas) args.get(CANVAS);
        } catch (ClassCastException cce) {
            System.out.println("First parameter has to be a canvas object. (eq. created with plot_createCanvas() )");
            return SetlBoolean.FALSE;
        }

        //second parameter has to be a list
        if (!(args.get(VALUES).isList().equalTo(SetlBoolean.TRUE))) {
            System.out.println("Second parameter values has to be a List. (eq. [1,2,3])");
            return SetlBoolean.FALSE;
        }

        //third parameter has to be a list
        if (!(args.get(CATEGORIES).isList().equalTo(SetlBoolean.TRUE))) {
            System.out.println("Third parameter categories has to be a List. (eq. [\"one\", \"two\", \"three\"])");
            return SetlBoolean.FALSE;
        }

        //cast to right datatype
        values = (SetlList) args.get(VALUES);
        categories = (SetlList) args.get(CATEGORIES);

        //check if datatypes in list are correct
        //for second parameter either double or boolean
        for (Value v : values) {
            if (!((v.isDouble().equalTo(SetlBoolean.TRUE)) || (v.isInteger().equalTo(SetlBoolean.TRUE)))) {
                System.out.println("Members in list of the second parameter have to be Integer or Double values");
                return SetlBoolean.FALSE;
            }
        }
        //for third parameter string
        //skip because everything can be converted to a String
        /*
        for (Value v : values) {
            if (!(categories.isString().equalTo(SetlBoolean.TRUE))) {
                return new SetlString("Members in list of the third parameter have to be String values");
            }
        }
        */

        //convert setllists to native java lists
        List valuesList = ConvertSetlTypes.convertSetlListAsDouble(values);
        List categorieList = ConvertSetlTypes.convertSetlListAsString(categories);

        if(!(valuesList.size() == categorieList.size())){
            System.out.println("The lists in the second and third parameter have to be of equal length");
            return SetlBoolean.FALSE;
        }

        //get forth optional parameter and check if set
        Value name = args.get(NAME);
        if (!name.equalTo(Rational.ONE)) {
            //check if forth parameter is a string
            if (!(name.isString().equalTo(SetlBoolean.TRUE))) {
                System.out.println("Forth parameter name has to be a String. (eq. \"name of the bar chart\" ");
                return SetlBoolean.FALSE;
            }
            SetlString nameSetlString = (SetlString) name;
            String nameString = nameSetlString.toString().replace("\"", "");

            return ConnectJFreeChart.getInstance().addBarChart(canvas, valuesList, categorieList, nameString);
        }

        return ConnectJFreeChart.getInstance().addBarChart(canvas, valuesList, categorieList);
    }
}
