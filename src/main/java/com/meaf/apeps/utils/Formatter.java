package com.meaf.apeps.utils;

import java.text.DecimalFormat;

public class Formatter {
  private static DecimalFormat df = new DecimalFormat("#.##");

  public static String format(Number number) {
    if(number instanceof Integer || number instanceof Long)
      return number.toString();
    if(number == null)
      return " - ";
    return df.format(number.doubleValue());
  }

  public static String format(String number) {
    if(number == null)
      return " - ";
    return number;
  }

}