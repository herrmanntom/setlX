package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.plot.types.Canvas;
import org.randoom.setlx.plot.utilities.ConnectJFreeChart;
import org.randoom.setlx.plot.utilities.ConvertSetlTypes;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.plot.utilities.PlotCheckType;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;

import java.util.HashMap;
import java.util.List;

public class PD_plot_addBoxChart extends PreDefinedProcedure {

    private final static ParameterDefinition CANVAS = createParameter("canvas");
    private final static ParameterDefinition VALUES = createParameter("values");
    private final static ParameterDefinition CATEGORIES = createParameter("categories");
    private final static ParameterDefinition NAME = createOptionalParameter("name", Rational.ONE);
    public final static PreDefinedProcedure DEFINITION = new PD_plot_addBoxChart();

    private PD_plot_addBoxChart() {
        super();
        addParameter(CANVAS);
        addParameter(VALUES);
        addParameter(CATEGORIES);
        addParameter(NAME);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        Canvas canvas;
        SetlList values;
        SetlList categories;

        //First Parameter must be a Canvas object
        if (!PlotCheckType.isCanvas(args.get(CANVAS))) {
            throw new UndefinedOperationException("First parameter has to be a canvas object. (eq. created with plot_createCanvas() )");
        }

        //second parameter has to be a list of lists
        if (!(PlotCheckType.isSetlListofSetlList(args.get(VALUES)))) {
            throw new UndefinedOperationException("Second parameter values has to be a List of Lists with Numbers. (eq. [[2],[5.5],[7]] )");
        }

        //third parameter has to be a list
        if (!(PlotCheckType.isSetlList(args.get(CATEGORIES)))) {
            throw new UndefinedOperationException("Third parameter categories has to be a List. (eq. [\"two\", \"five\", \"seven\"])");
        }

        //cast to right datatype
        canvas = (Canvas) args.get(CANVAS);
        values = (SetlList) args.get(VALUES);
        categories = (SetlList) args.get(CATEGORIES);

        //check if datatypes in list are correct
        //for second parameter either double or boolean
        if (!PlotCheckType.isSetlListofSetlListWithNumbers(values)) {
            throw new UndefinedOperationException("Members in list of the second parameter have to be Integer or Double values");
        }

        //for third parameter string
        if (!PlotCheckType.isSetlListWithStrings(categories)) {
            throw new UndefinedOperationException("Members in list of the third parameter have to be String values");
        }

        //convert setllists to native java lists
        List<List<Double>> valuesList = ConvertSetlTypes.convertSetlListToListOfListOfDouble(values, state);
        List<String> categorieList = ConvertSetlTypes.convertSetlListToListOfString(categories);

        if (!(PlotCheckType.sameSize(valuesList, categorieList))) {
            throw new UndefinedOperationException("The (outer) lists in the second and third parameter have to be of equal length (eq: [[1,2,3],[4,5],[6]] <-> [\"one\", \"two\", \"three\"]");
        }

        //get forth optional parameter and check if set
        Value name = args.get(NAME);
        if (!name.equalTo(Rational.ONE)) {
            //check if forth parameter is a string
            if (!(PlotCheckType.isSetlString(name))) {
                throw new UndefinedOperationException("Forth parameter name has to be a String. (eq. \"name of the box chart\" ");
            }
            SetlString nameSetlString = (SetlString) name;
            String nameString = nameSetlString.toString().replace("\"", "");

            return ConnectJFreeChart.getInstance().addBoxChart(canvas, valuesList, categorieList, nameString);
        }

        return ConnectJFreeChart.getInstance().addBoxChart(canvas, valuesList, categorieList);
    }
}

