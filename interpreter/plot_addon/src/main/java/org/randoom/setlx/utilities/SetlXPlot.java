package org.randoom.setlx.utilities;


import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;

public interface SetlXPlot {

    public Canvas createCanvas();

    public Graph addGraph(Canvas canvas, SetlString function);

    public Graph addGraph(Canvas canvas, SetlString function, SetlString name);

    public Graph addGraph(Canvas canvas, SetlString function, SetlBoolean plotArea);

    public Graph addGraph(Canvas canvas, SetlString function, SetlString name, SetlBoolean plotArea);

    public Graph addGraph(Canvas canvas, SetlList function);

    public Graph addGraph(Canvas canvas, SetlList function, SetlString name);

    public Graph addParamGraph(Canvas canvas, SetlString xfunction, SetlString yfunction);

    public Graph addParamGraph(Canvas canvas, SetlString xfunction, SetlString yfunction, SetlString name);

    public Graph addParamGraph(Canvas canvas, SetlString xfunction, SetlString yfunction, SetlBoolean plotArea);

    public Graph addParamGraph(Canvas canvas, SetlString xfunction, SetlString yfunction, SetlString name, SetlBoolean plotArea);

    public Graph addChart(Canvas canvas, String chartType, SetlList values);

    public Graph addChart(Canvas canvas, String chartType, SetlList values, String name);

    public void removeGraph(Canvas canvas, Graph graph);

    public void insertLabel(Canvas canvas, String xLabel, String yLabel);

    public void insertTitel(Canvas canvas, SetlString titel);

    public void legendVisible(Canvas canvas, Boolean visible);

    public void modScale(Canvas canvas, SetlList xMinMax, SetlList yMinMax); //SetlList passend?

    public void exportCanvas(Canvas canvas, String path);

    public void modScaleType(SetlString xType, SetlString yType);

    public void addBullet(Canvas canvas, double x, double y);
}
