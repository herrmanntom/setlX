package org.randoom.setlx.utilities;


import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;

import java.util.List;

public interface SetlXPlot {

    public Canvas createCanvas();

    public Graph addGraph(Canvas canvas, String function);

    public Graph addGraph(Canvas canvas, String function, String name);

    public Graph addGraph(Canvas canvas, String function, boolean plotArea);

    public Graph addGraph(Canvas canvas, String function, String name, boolean plotArea);

    public Graph addGraph(Canvas canvas, List<List<Double>> function);

    public Graph addGraph(Canvas canvas, List<List<Double>> function, String name);

    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction);

    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name);

    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, Boolean plotArea);

    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, Boolean plotArea);

    public Graph addChart(Canvas canvas, String chartType, List values);

    public Graph addChart(Canvas canvas, String chartType, List values, String name);

    public void removeGraph(Canvas canvas, Graph graph);

    public void insertLabel(Canvas canvas, String xLabel, String yLabel);

    public void insertTitel(Canvas canvas, String titel);

    public void legendVisible(Canvas canvas, Boolean visible);

    public void modScale(Canvas canvas, double xMin, double xMax, double yMin, double yMax);

    public void exportCanvas(Canvas canvas, String path);

    public void modScaleType(Canvas canvas, String xType, String yType);

    public void addBullet(Canvas canvas, double x, double y);
}
