package com.meaf.apeps.view.components;

import com.meaf.apeps.calculations.HoltWinters;
import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.utils.ETargetValues;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.examples.AbstractVaadinChartExample;
import com.vaadin.addon.charts.model.*;
import com.vaadin.ui.Component;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ModelChart extends AbstractVaadinChartExample {

  private static DecimalFormat df = new DecimalFormat("#.##");

  private ETargetValues targetType;
  private List<WeatherStateData> input = new ArrayList<>();
  private List<Double> smoothed = new ArrayList<>();
  private List<Double> model = new ArrayList<>();

  public ModelChart(HoltWinters method) {
    super();
    if (method == null)
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

    XAxis xAxis = configuration.getxAxis();
    xAxis.setType(AxisType.DATETIME);

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
    fillInput(input);
    input.setName("Input");
    configuration.addSeries(input);

    DataSeries smoothed = new DataSeries();

    smoothed.setName("Smoothed");
    smoothed.setData(); //asNumbersList(this.smoothed)
    configuration.addSeries(smoothed);

    DataSeries model = new DataSeries();
    model.setName("Model");
    model.setData(); //asNumbersList(this.model)
    configuration.addSeries(model);

    chart.drawChart(configuration);
    return chart;
  }

  private void fillInput(DataSeries input) {
    this.input.stream().forEach(i -> input.add(new DataSeriesItem(i.getDate(), targetType.mapper.apply(i))));
  }

  private Instant dateOf(WeatherStateData i) {
    return new Date(i.getDate().getTime()).toInstant();
  }

  private List<Number> asNumbersList(List<Double> input) {
    return input.stream().map(d -> Double.parseDouble(df.format(d))).map(d -> (Number) d).collect(Collectors.toList());
  }

}