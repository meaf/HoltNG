package com.meaf.apeps.input.csv;

import com.meaf.apeps.input.IWeatherParser;
import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
class CSVRowParser implements IWeatherParser {

// 0  - PeriodEnd
// 1  - PeriodStart
// 2  - Period
// 3  - CloudOpacity
// 4  - Dhi
// 5  - Dni
// 6  - Ebh
// 7  - Ghi
// 8  - WindDirection10m
// 9  - WindSpeed10m
//ZonedDateTime.parse(rowData[0]).withZoneSameInstant()

  private HashMap<String, Integer> columnOrder = new HashMap<>();

  public void addColumn(String name, Integer order){
    columnOrder.put(name, order);
  }

  public List<WeatherStateData> parse(String data) {
    return Arrays.stream(data.split("\n")).map(this::parseRow).collect(Collectors.toList());
  }

  private WeatherStateData parseRow(String row) {
    String[] rowAttr = row.split(",");
    WeatherStateData weatherStateData = new WeatherStateData();

    weatherStateData.setDate(DateUtils.zonedTimeStringToInstant(rowAttr[columnOrder.get("PeriodEnd")]));
    weatherStateData.setCloudOpacity(Double.parseDouble(rowAttr[columnOrder.get("CloudOpacity")]));
    weatherStateData.setDni(Integer.parseInt(rowAttr[columnOrder.get("Dni")]));
    weatherStateData.setGhi(Integer.parseInt(rowAttr[columnOrder.get("Dhi")]));
    weatherStateData.setEbh(Integer.parseInt(rowAttr[columnOrder.get("Ebh")]));
    weatherStateData.setGhi(Integer.parseInt(rowAttr[columnOrder.get("Ghi")]));
    weatherStateData.setWindSpeed(Double.parseDouble(rowAttr[columnOrder.get("WindSpeed10m")]));
    return weatherStateData;
  }

}
