package org.randoom.setlx.utilities;

import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;


public class ConnectJMathPlot implements Plot {
    @Override
    public Canvas createCanvas() {
        return new Canvas();
    }

    @Override
    public Graph addGraph(Canvas canvas, SetlString function) {
        return null;
    }

    @Override
    public Graph addGraph(Canvas canvas, SetlString function, SetlString name) {
        return null;
    }

    @Override
    public Graph addGraph(Canvas canvas, SetlString function, SetlBoolean plotArea) {
        return null;
    }

    @Override
    public Graph addGraph(Canvas canvas, SetlString function, SetlString name, SetlBoolean plotArea) {
        return null;
    }

    @Override
    public Graph addGraph(Canvas canvas, SetlList function) {
        return null;
    }

    @Override
    public Graph addGraph(Canvas canvas, SetlList function, SetlString name) {
        return null;
    }

    @Override
    public Graph addParamGraph(Canvas canvas, SetlString xfunction, SetlString yfunction) {
        return null;
    }

    @Override
    public Graph addParamGraph(Canvas canvas, SetlString xfunction, SetlString yfunction, SetlString name) {
        return null;
    }

    @Override
    public Graph addParamGraph(Canvas canvas, SetlString xfunction, SetlString yfunction, SetlBoolean plotArea) {
        return null;
    }

    @Override
    public Graph addParamGraph(Canvas canvas, SetlString xfunction, SetlString yfunction, SetlString name, SetlBoolean plotArea) {
        return null;
    }

    @Override
    public Graph addChart() {
        return null;
    }

    @Override
    public void removeGraph(Canvas canvas, Graph graph) {

    }

    @Override
    public void insertLabel(Canvas canvas, SetlString xLabel, SetlString yLabel) {

    }

    @Override
    public void inserttitel(Canvas canvas, SetlString titel) {

    }

    @Override
    public void insertLegend(Canvas canvas, Boolean visible) {

    }

    @Override
    public void modScale(Canvas canvas, SetlList xMinMax, SetlList yMinMax) {

    }

    @Override
    public void exportCanvas(Canvas canvas, SetlString path) {

    }

    @Override
    public void modScaleType(SetlString xType, SetlString yType) {

    }
}
