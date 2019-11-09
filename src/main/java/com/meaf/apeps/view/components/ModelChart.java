package com.meaf.apeps.view.components;

import com.meaf.apeps.calculations.HoltWinters;
import com.meaf.apeps.utils.DatedValue;
import com.meaf.apeps.utils.ETargetValues;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.examples.AbstractVaadinChartExample;
import com.vaadin.addon.charts.model.*;
import com.vaadin.ui.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ModelChart extends AbstractVaadinChartExample {

  private static DecimalFormat df = new DecimalFormat("#.##");

  private ETargetValues targetType = ETargetValues.SOLAR;
  private List<DatedValue> input = new ArrayList<>();
  private List<DatedValue> smoothed = new ArrayList<>();
  private List<DatedValue> model = new ArrayList<>();

  public ModelChart(HoltWinters method) {
    super();
    if (method == null || method.getOptimalResult() == null)
      return;
    this.input = method.getInputData();
    this.smoothed = method.getSmoothedData();
    this.model = method.getFcData();
    this.targetType = method.getTargetType();
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

    PlotOptionsLine plotOptions = new PlotOptionsLine();
    plotOptions.getDataLabels().setEnabled(true);
    configuration.setPlotOptions(plotOptions);

    YAxis yAxis = configuration.getyAxis();
    yAxis.setTitle(String.format("Power potential (%s)", targetType.units));
//    yAxis.setFloor(0);

    XAxis xAxis = configuration.getxAxis();
    xAxis.setType(AxisType.DATETIME);
    xAxis.setTitle("Date");

    Tooltip tooltip = configuration.getTooltip();
    tooltip.setEnabled(true);
    tooltip.setDateTimeLabelFormats(new DateTimeLabelFormats());
    tooltip.setAnimation(true);
    tooltip.setValueSuffix(" " + targetType.units);

    Legend legend = configuration.getLegend();
    legend.setLayout(LayoutDirection.VERTICAL);
    legend.setAlign(HorizontalAlign.RIGHT);
    legend.setVerticalAlign(VerticalAlign.TOP);
    legend.setBorderWidth(0);

    DataSeries input = new DataSeries();
    fillDataSeries(this.input, input);
    input.setName("Input");
    configuration.addSeries(input);

    DataSeries smoothed = new DataSeries();

    smoothed.setName("Smoothed");
    fillDataSeries(this.smoothed, smoothed);
    configuration.addSeries(smoothed);

    DataSeries model = new DataSeries();
    model.setName("Model");
    fillDataSeries(this.model, model);
    configuration.addSeries(model);

    chart.drawChart(configuration);
    return chart;
  }

  private void fillDataSeries(List<DatedValue> srcList, DataSeries ds) {
    srcList.forEach(i -> ds.add(new DataSeriesItem(i.getDate(), pretty(i.getNumber()))));
  }

  private Number pretty(Number number) {
    return new BigDecimal(number.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
  }
}