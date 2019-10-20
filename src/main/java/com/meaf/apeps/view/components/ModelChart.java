package com.meaf.apeps.view.components;

import com.meaf.apeps.calculations.Forecasting;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.examples.AbstractVaadinChartExample;
import com.vaadin.addon.charts.model.*;
import com.vaadin.ui.Component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelChart extends AbstractVaadinChartExample {

  private static DecimalFormat df = new DecimalFormat("#.##");

  private List<Double> input = new ArrayList<>();
  private List<Double> smoothed = new ArrayList<>();
  private List<Double> model = new ArrayList<>();

  public ModelChart(Forecasting method) {
    super();
    if (method == null)
      return;
    this.input = method.getInputData();
    this.smoothed = method.getSmoothedData();
    this.model = method.getFcData();
  }

  @Override
  public String getDescription() {
    return "Basic Line With Data Labels";
  }

  @Override
  protected Component getChart() {
    Chart chart = new Chart();
    chart.setHeight("450px");
    chart.setWidth("100%");

    Configuration configuration = chart.getConfiguration();
    configuration.getChart().setType(ChartType.SPLINE);
    configuration.getChart().setMarginRight(130);
    configuration.getChart().setMarginBottom(25);
    configuration.getChart().setZoomType(ZoomType.X);

    configuration.getTitle().setText("Resulting chart");

    configuration
        .getTooltip()
        .setFormatter(
            "'<b>'+ this.series.name +'</b><br/>'+this.x +': '+ this.y +'°C'");

    PlotOptionsLine plotOptions = new PlotOptionsLine();
    plotOptions.getDataLabels().setEnabled(true);
    configuration.setPlotOptions(plotOptions);

    Legend legend = configuration.getLegend();
    legend.setLayout(LayoutDirection.VERTICAL);
    legend.setAlign(HorizontalAlign.RIGHT);
    legend.setVerticalAlign(VerticalAlign.TOP);
    legend.setBorderWidth(0);

    ListSeries input = new ListSeries();
    input.setName("Input");
    input.setData(asNumbersList(this.input));
    configuration.addSeries(input);

    ListSeries smoothed = new ListSeries();
    smoothed.setName("Smoothed");
    smoothed.setData(asNumbersList(this.smoothed));
    configuration.addSeries(smoothed);

    ListSeries model = new ListSeries();
    model.setName("Model");
    model.setData(asNumbersList(this.model));
    configuration.addSeries(model);

    chart.drawChart(configuration);
    return chart;
  }

  private List<Number> asNumbersList(List<Double> input) {
    return input.stream().map(d -> Double.parseDouble(df.format(d))).map(d -> (Number) d).collect(Collectors.toList());
  }

}