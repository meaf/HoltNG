package com.meaf.apeps.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Formatter {
  private static DecimalFormat df = new DecimalFormat("#.##");
  private static SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");

  public static String format(Number number) {
    if(number instanceof Integer || number instanceof Long)
      return number.toString();
    if(number == null)
      return " - ";
    return df.format(number.doubleValue());
  }

  public static String nameMonth(Date date) {
    return date == null ? " - " : sdf.format(date);
  }

  public static String format(String number) {
    if(number == null)
      return " - ";
    return number;
  }

}
