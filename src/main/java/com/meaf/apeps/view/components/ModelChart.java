package com.meaf.apeps.view.components;

import com.meaf.apeps.calculations.HoltWinters;
import com.meaf.apeps.utils.DatedValue;
import com.meaf.apeps.utils.ETargetValues;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.PointClickEvent;
import com.vaadin.addon.charts.examples.AbstractVaadinChartExample;
import com.vaadin.addon.charts.model.*;
import com.vaadin.ui.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class ModelChart extends AbstractVaadinChartExample {

  private ETargetValues targetType = ETargetValues.SOLAR;
  private List<DatedValue> input = new ArrayList<>();
  private List<DatedValue> smoothed = new ArrayList<>();
  private List<DatedValue> model = new ArrayList<>();
  private List<DatedValue> seasonal = new ArrayList<>();
  private List<DatedValue> trend = new ArrayList<>();
  private Consumer<Date> click;

  public ModelChart(HoltWinters method) {
    super();
    if (method == null || method.getOptimalResult() == null)
      return;
    this.input = method.getInputData();
    this.smoothed = method.getSmoothedData();
    this.model = method.getFcData();
    this.seasonal = method.getSeasonalCoeficients();
    this.trend = method.getTrend();
    this.targetType = method.getTargetType();
  }

  public void setClickDateEvent(Consumer<Date> consumer){
    this.click = consumer;
  }

  @Override
  public String getDescription() {
    return "Holt-Winters model";
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

    DataSeries smoothed = new DataSeries();
    smoothed.setName("Smoothed");
    fillDataSeries(this.smoothed, smoothed);

    DataSeries model = new DataSeries();
    model.setName("Model");
    fillDataSeries(this.model, model);

    DataSeries seasonal = new DataSeries();
    seasonal.setName("Seasonal");
    fillDataSeries(this.seasonal, seasonal);

    DataSeries trend = new DataSeries();
    trend.setName("Trend");
    fillDataSeries(this.trend, trend);

    configuration.addSeries(input);
    configuration.addSeries(smoothed);
    configuration.addSeries(model);
//    configuration.addSeries(seasonal);
//    configuration.addSeries(trend);

    chart.drawChart(configuration);

    chart.addPointClickListener(this::clickEvent);
    return chart;
  }

  private void clickEvent(PointClickEvent e) {
    System.out.println(e);
    if(click != null)
      click.accept(new Date());
  }

  private void fillDataSeries(List<DatedValue> srcList, DataSeries ds) {
    srcList.forEach(i -> ds.add(new DataSeriesItem(i.getDate(), pretty(i.getNumber()))));
  }

  private Number pretty(Number number) {
    return new BigDecimal(number.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
  }
}