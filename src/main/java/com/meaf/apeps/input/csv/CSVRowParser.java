package com.meaf.apeps.input.csv;

import com.meaf.apeps.input.IWeatherParser;
import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
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

  public List<WeatherStateData> parse(String data) {
    return Arrays.stream(data.split("\n")).map(this::parseRow).collect(Collectors.toList());
  }

  private WeatherStateData parseRow(String row) {
    String[] rowAttr = row.split(",");
    WeatherStateData weatherStateData = new WeatherStateData();

    weatherStateData.setDate(DateUtils.zonedTimeStringToInstant(rowAttr[0]));
    weatherStateData.setCloudOpacity(Double.parseDouble(rowAttr[3]));
    weatherStateData.setDhi(Integer.parseInt(rowAttr[4]));
    weatherStateData.setDni(Integer.parseInt(rowAttr[5]));
    weatherStateData.setEbh(Integer.parseInt(rowAttr[6]));
    weatherStateData.setGhi(Integer.parseInt(rowAttr[7]));
    weatherStateData.setWindSpeed(Double.parseDouble(rowAttr[9]));
    return weatherStateData;
  }

}
