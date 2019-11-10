package com.meaf.apeps.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DatedValue {
  private final Date date;
  private final Number number;

  public DatedValue(Date date, Number number) {
    this.date = date;
    this.number = number;
  }

  public DatedValue(LocalDate date, Number number) {
    this.date = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    this.number = number;
  }

  public Date getDate() {
    return date;
  }

  public Number getNumber() {
    return number;
  }

  public double asDouble() {
    return number.doubleValue();
  }
}
