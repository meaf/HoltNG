package com.meaf.apeps.utils;

import com.meaf.apeps.model.entity.WeatherStateData;

import java.util.function.Function;

public enum ETargetValues {
  WIND("m/s", WeatherStateData::getWindSpeed),
  SOLAR("Wh/m²", WeatherStateData::getGhi);

  public final String units;
  public final Function<WeatherStateData, Number> mapper;

  ETargetValues(String units, Function<WeatherStateData, Number> mapper) {
    this.units = units;
    this.mapper = mapper;
  }
}
