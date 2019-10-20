package com.meaf.apeps.input.csv;

import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.utils.DateUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;

class CSVRowParser {

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
  private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

  static WeatherStateData parse(String row) {
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
