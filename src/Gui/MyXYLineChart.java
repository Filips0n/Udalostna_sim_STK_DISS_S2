package Gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;

public class MyXYLineChart {

    private JFreeChart chart;
    private XYSeriesCollection dataset;
    private XYSeries series;
    private XYPlot plot;

    public MyXYLineChart(String title, String xAxisLabel, String yAxisLabel, String seriesLabel) {
        this.series = new XYSeries(seriesLabel);
        this.dataset = new XYSeriesCollection(this.series);
        this.chart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, this.dataset);
        this.plot = this.chart.getXYPlot();
        initiateChart();
    }

    public void addSeries(String seriesName, double[] xData, double[] yData) {
        XYSeries series = new XYSeries(seriesName);
        for (int i = 0; i < xData.length; i++) {
            series.add(xData[i], yData[i]);
        }
        dataset.addSeries(series);
    }

    public void updateSeries(String seriesName, double[] xData, double[] yData) {
        int seriesIndex = dataset.getSeriesIndex(seriesName);
        XYSeries series = dataset.getSeries(seriesIndex);
        series.clear();
        for (int i = 0; i < xData.length; i++) {
            series.add(xData[i], yData[i]);
        }
    }

    public JFreeChart getChart() {
        return chart;
    }

    private void initiateChart() {
        final org.jfree.chart.axis.NumberAxis domainAxis = (org.jfree.chart.axis.NumberAxis) plot.getDomainAxis();
        domainAxis.setAutoRange(true);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);

        final org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRange(true);
        plot.getRangeAxis().setAutoRange(true);                            // uncomment
        ((org.jfree.chart.axis.NumberAxis)plot.getRangeAxis()).setAutoRangeIncludesZero(false); // add
        final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(1.5f));
    }

    public void addPoint(double value) {
        this.series.add(this.series.getItemCount(), value);

        setAxis();
    }

    private void setAxis() {
        final double maxY = this.series.getMaxY();
        final double minY = this.series.getMinY();
        final NumberAxis rangeAxis = (NumberAxis) this.chart.getXYPlot().getRangeAxis();
        if (maxY != minY) {
            rangeAxis.setRange(minY, maxY);
        }
    }

    public void addPoint(int xValue, double yValue) {
        this.series.add(xValue, yValue);

        setAxis();
    }

    public int getItemCount() {
        return this.series.getItemCount();
    }

    public void clearSeries() {
        this.series.clear();
    }
}

