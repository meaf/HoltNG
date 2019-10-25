package com.meaf.apeps.calculations.aggregate;

import com.meaf.apeps.model.entity.TimelessDate;
import com.meaf.apeps.model.entity.WeatherStateData;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WeatherAggregator {

  public enum EDataSource {
    http(48),
    csv(24);

    final int entriesPerDay;

    EDataSource(int entriesPerDay) {
      this.entriesPerDay = entriesPerDay;
    }
  }

  private WeatherAggregator() {
  }

  public static List<WeatherStateData> hourlyToDaily(List<WeatherStateData> input, EDataSource dataSource) {
    final int dailyEntries = dataSource.entriesPerDay;

    final Map<TimelessDate, List<WeatherStateData>> groupedMap = input
        .stream()
        .collect(Collectors.groupingBy(WeatherStateData::asDayUnit));


    final List<WeatherStateData> avgState = new LinkedList<>();
    groupedMap.entrySet()
        .stream()
        .filter(e -> dailyEntries == e.getValue().size()  // only full day counts
            || dailyEntries == e.getValue().size() + 1   // todo: damn you solcast API for allowing this
            || dailyEntries == e.getValue().size() + 2)   // todo: damn you solcast API for allowing this
        .forEach(e -> avgState.add(e.getValue().stream().reduce(createAccumulator(e), WeatherAggregator::accumulate)));

    return avgState.stream().map(WeatherAggregator::average).collect(Collectors.toList());
  }

  public static List<WeatherStateData> dailyToMonthy(List<WeatherStateData> input) {

    final int acceptableAmount = 31;
    final Map<TimelessDate, List<WeatherStateData>> groupedMap = input
        .stream()
        .collect(Collectors.groupingBy(WeatherStateData::asMonthUnit));

    final List<WeatherStateData> avgState = new LinkedList<>();
    groupedMap.entrySet()
        .stream()
        .filter((e -> acceptableAmount == e.getValue().size()  // only full day counts
            || acceptableAmount == e.getValue().size() + 1
            || acceptableAmount == e.getValue().size() + 2
            || acceptableAmount == e.getValue().size() + 3))
        .forEach(e -> avgState.add(e.getValue().stream().reduce(createAccumulator(e), WeatherAggregator::accumulate)));

    return avgState.stream()
        .map(WeatherAggregator::average)
        .sorted(Comparator.comparing(WeatherStateData::getDate))
            .collect(Collectors.toList());
  }

  private static WeatherStateData createAccumulator(Map.Entry<TimelessDate, List<WeatherStateData>> list) {
    WeatherStateData data = new WeatherStateData();
    data.setUnitsToAggregate(list.getValue().size());
    return data;
  }

  private static WeatherStateData average(WeatherStateData stateData) {
    int dailyEntries = stateData.getUnitsToAggregate();

    stateData.setDhi(stateData.getDhi() / dailyEntries);
    stateData.setDni(stateData.getDni() / dailyEntries);
    stateData.setGhi(stateData.getGhi() / dailyEntries);
    stateData.setEbh(stateData.getEbh() / dailyEntries);
    stateData.setCloudOpacity(stateData.getCloudOpacity() / dailyEntries);
    stateData.setWindSpeed(stateData.getWindSpeed() / dailyEntries);

    return stateData;
  }

  private static WeatherStateData accumulate(WeatherStateData acc, WeatherStateData src) {

    acc.setDate(src.getDate());
    acc.setDhi(Math.addExact(acc.getDhi(), src.getDhi()));
    acc.setDni(Math.addExact(acc.getDni(), src.getDni()));
    acc.setGhi(Math.addExact(acc.getGhi(), src.getGhi()));
    acc.setEbh(Math.addExact(acc.getEbh(), src.getEbh()));
    acc.setCloudOpacity(Double.sum(acc.getCloudOpacity(), src.getCloudOpacity()));
    acc.setWindSpeed(Double.sum(acc.getWindSpeed(), src.getWindSpeed()));

    return acc;
  }

}
