package com.meaf.apeps.model;

import java.sql.Date;
import java.util.Calendar;
import java.util.Objects;

public class TimelessDate extends Date {

  private EDateType format;
  public enum EDateType{
    DAILY(Calendar.DAY_OF_YEAR),
    MONTHY(Calendar.MONTH);
    private final int dateUnit;
    EDateType(int dateUnit) {
        this.dateUnit = dateUnit;
    }
  }

  public TimelessDate(EDateType type, long date) {
    super(date);
    this.format = type;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof java.util.Date){
      Calendar cal1 = Calendar.getInstance();
      Calendar cal2 = Calendar.getInstance();
      cal1.setTime((java.util.Date) obj);
      cal2.setTime(this);
      return cal1.get(this.format.dateUnit) == cal2.get(this.format.dateUnit) &&
          cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }
    return false;
  }

  @Override
  public int hashCode() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(this);
    return Objects.hash(cal.get(this.format.dateUnit), cal.get(Calendar.YEAR));
  }
}
