package com.meaf.apeps.model.entity;

import java.sql.Date;
import java.util.Calendar;
import java.util.Objects;

public class TimelessDate extends Date {


  public TimelessDate(int year, int month, int day) {
    super(year, month, day);
  }

  public TimelessDate(long date) {
    super(date);
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof java.util.Date){
      Calendar cal1 = Calendar.getInstance();
      Calendar cal2 = Calendar.getInstance();
      cal1.setTime((java.util.Date) obj);
      cal2.setTime(this);
      return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
          cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }
    return false;
  }

  @Override
  public int hashCode() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(this);
    return Objects.hash(
    cal.get(Calendar.DAY_OF_YEAR),
        cal.get(Calendar.YEAR)
    );
  }
}
