package org.randoom.setlx.plot.utilities;

import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.plot.types.Graph;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.types.Value;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class DrawFrame extends AbstractFrame {
    private static final long serialVersionUID = 7233915032099797173L;

    private XYPlot plot;

    @Override
    protected Plot getPlot() {
        return this.plot;
    }

    private List<Graph> functions = new ArrayList<>();

    @Override
    protected List<Value> getFunctions() {
        return new ArrayList<Value>(this.functions);
    }

    @Override
    protected void setFunctions(List<Value> fun) {
        List<Graph> functions = new ArrayList<>();
        for (Value value : fun) {
            if (value instanceof Graph) {
                functions.add((Graph) value);
            }
        }
        this.functions = functions;
    }

    public void setxAxis(ValueAxis xAxis) {
        this.xAxis = xAxis;
        if (plot != null) {
            plot.setDomainAxis(xAxis);
        }
    }

    public void modxScale(double x_Min, double x_Max) throws SetlException {
        this.x_Max = x_Max;
        this.x_Min = x_Min;
        ValueAxis axis = plot.getDomainAxis();
        axis.setLowerBound(this.x_Min);
        axis.setUpperBound(this.x_Max);
        remakeFunctions();
    }

    public void modyScale(double y_Min, double y_Max) {
        ValueAxis axis = plot.getRangeAxis();
        axis.setLowerBound(y_Min);
        axis.setUpperBound(y_Max);
    }

    public void setyAxis(ValueAxis yAxis) {
        this.yAxis = yAxis;
        if (plot != null) {
            plot.setRangeAxis(yAxis);
        }
        this.redraw();
    }

    public DrawFrame(String title, double width, double height) {
        super(title, width, height );

        x_Min = -10.0;
        x_Max = 10.0;
        xAxis = new NumberAxis("x");
        yAxis = new NumberAxis("y");
        plot = null;
    }

    @Override
    protected void remakeFunctions() throws SetlException {
        plot = new XYPlot(new XYSeriesCollection(), (ValueAxis)xAxis, (ValueAxis)yAxis, new XYLineAndShapeRenderer());
        this.redraw();
        ArrayList<Graph> func = new ArrayList<>(functions);
        functions.clear();
        for (Graph item : func) {
            if (!item.getFunctionstring().isEmpty()) {
                this.addDataset(item.getTitle(), item.getFunctionstring(), item.getInterpreterState(), item.isArea(), item.getColor());
            } else if (!item.getXfunction().isEmpty()) {
                this.addParamDataset(item.getTitle(), item.getXfunction(), item.getYfunction(), item.getInterpreterState(), item.isArea(), item.getColor(), item.getCoordinates());
            } else if (item.getFunction() != null) {
                if (item.isBullets()) {
                    this.addBulletDataset(item.getTitle(), item.getFunction(), item.getColor(), item.getBulletSize());
                } else {
                    this.addListDataset(item.getTitle(), item.getFunction(), item.isArea(), item.getColor());
                }
            } else if (item.getCoordinates() != null) {
                this.addTextLabel(item.getCoordinates(), item.getTitle());
            } else {
                System.out.println("Something went horribly wrong. \n If you get the error, contact us, get a coffee and wait for a solution");
            }
        }
        this.redraw();
    }

    public Graph addDataset(String title, String function, State interpreterState, boolean area, Color color) throws SetlException {

        Graph plotfun = new Graph(title, area, interpreterState);
        plotfun.setFunctionstring(function);
        plotfun.setColor(color);
        functions.add(plotfun);
        XYSeries series = new XYSeries(title, true, false);
        CalcFunction calc = new CalcFunction(function, interpreterState);
        XYItemRenderer renderer;
        if (area) {
            renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
            ((XYAreaRenderer) renderer).setOutline(true);
        } else {
            renderer = new XYLineAndShapeRenderer(true, false);
        }
        renderer.setSeriesPaint(0, color);
        double x = x_Min;
        double step = (x_Max - x_Min) / 200;
        while (x <= x_Max) {
            series.add(x, calc.calcYfromX(x));
            x += step;
        }
        XYSeriesCollection col = new XYSeriesCollection(series);
        if (plot == null) {
            plot = new XYPlot(col, (ValueAxis)xAxis, (ValueAxis)yAxis, renderer);
        } else {
            plot.setDataset(chartCount, col);
            plot.setRenderer(chartCount, renderer);
        }
        this.redraw();
        chartCount++;
        return plotfun;
    }

    public Graph addListDataset(String title, List<List<Double>> function, boolean area, Color color) {
        Graph plotfun = new Graph(title, area, new State());
        plotfun.setFunction(function);
        plotfun.setBullets(false);
        plotfun.setColor(color);
        functions.add(plotfun);

        XYSeries series = new XYSeries(title, false, true);
        XYItemRenderer renderer;
        if (area) {
            renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
            ((XYAreaRenderer) renderer).setOutline(true);
        } else {
            renderer = new XYLineAndShapeRenderer(true, false);
        }
        renderer.setSeriesPaint(0, color);
        for (List<Double> element : function) {
            series.add(element.get(0), element.get(1));
        }
        XYSeriesCollection col = new XYSeriesCollection(series);

        if (plot == null) {
            plot = new XYPlot(col, (ValueAxis)xAxis, (ValueAxis)yAxis, renderer);
        } else {
            plot.setDataset(chartCount, col);
            plot.setRenderer(chartCount, renderer);
        }
        this.redraw();
        chartCount++;
        return plotfun;
    }

    @Override
    public void setLabel(String xLabel, String yLabel) {
        plot.getDomainAxis().setLabel(xLabel);
        plot.getRangeAxis().setLabel(yLabel);
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
    }

    @Override
    public Value addTextLabel(List<Double> coordinates, String text) {
        Graph labelGraph = new Graph(text, false, new State());
        labelGraph.setCoordinates(coordinates);
        functions.add(labelGraph);
        XYTextAnnotation label = new XYTextAnnotation(text, coordinates.get(0), coordinates.get(1));
        label.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        if (plot == null) {
            plot = new XYPlot();
            plot.setDomainAxis((ValueAxis)xAxis);
            plot.setRangeAxis((ValueAxis)yAxis);
        }
        plot.addAnnotation(label);
        this.redraw();
        chartCount++;
        return labelGraph;
    }

    public Graph addBulletDataset(String title, List<List<Double>> bullets, Color color, Double bulletSize) {
        Graph plotfun = new Graph(title, false, new State());
        plotfun.setFunction(bullets);
        plotfun.setBullets(true);
        plotfun.setColor(color);
        plotfun.setBulletSize(bulletSize);
        functions.add(plotfun);
        XYSeries series = new XYSeries(title, false, true);
        XYDotRenderer renderer = new XYDotRenderer();
        renderer.setDotHeight(bulletSize.intValue());
        renderer.setDotWidth(bulletSize.intValue());
        renderer.setSeriesPaint(0, color);
        renderer.setSeriesVisibleInLegend(0, false);
        for (List<Double> element : bullets) {
            series.add(element.get(0), element.get(1));
        }
        XYSeriesCollection col = new XYSeriesCollection(series);

        if (plot == null) {
            plot = new XYPlot(col, (ValueAxis)xAxis, (ValueAxis)yAxis, renderer);
        } else {
            plot.setDataset(chartCount, col);
            plot.setRenderer(chartCount, renderer);
        }

        this.redraw();
        chartCount++;
        return plotfun;
    }

    public Graph addParamDataset(String title, String xfunction, String yfunction, State interpreterState, boolean area, Color color, List<Double> limits) throws SetlException {
        Graph plotfun = new Graph(title, area, interpreterState);
        plotfun.setXfunction(xfunction);
        plotfun.setYfunction(yfunction);
        plotfun.setCoordinates(limits);
        plotfun.setColor(color);

        functions.add(plotfun);

        XYSeries series = new XYSeries(title, false, true);
        CalcFunction xcalc = new CalcFunction(xfunction, interpreterState);
        CalcFunction ycalc = new CalcFunction(yfunction, interpreterState);

        XYItemRenderer renderer;
        if (area) {
            renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
            ((XYAreaRenderer) renderer).setOutline(true);
        } else {
            renderer = new XYLineAndShapeRenderer(true, false);
        }
        renderer.setSeriesPaint(0, color);
        double step = (limits.get(1) - limits.get(0)) / 200;
        for (double x = limits.get(0); x <= limits.get(1); x += step) {
            series.add(xcalc.calcYfromX(x), ycalc.calcYfromX(x));
        }
        XYSeriesCollection col = new XYSeriesCollection(series);

        if (plot == null) {
            plot = new XYPlot(col, (ValueAxis)xAxis, (ValueAxis)yAxis, renderer);
        } else {
            plot.setDataset(chartCount, col);
            plot.setRenderer(chartCount, renderer);
        }
        this.redraw();
        chartCount++;
        return plotfun;
    }

}

