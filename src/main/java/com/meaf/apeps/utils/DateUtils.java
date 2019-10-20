package com.meaf.apeps.utils;

import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateUtils {
  private DateUtils(){}

  public static Date zonedTimeStringToInstant(String dateStr){
    ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateStr).withZoneSameInstant(ZoneId.systemDefault());
    Instant instant = zonedDateTime.toInstant();
    return new Date(Date.from(instant).getTime());
  }

  public static java.sql.Date asSqlDate(java.util.Date date) {
    return new Date(date.getTime());
  }
}
