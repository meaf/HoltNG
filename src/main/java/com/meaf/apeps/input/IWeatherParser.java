package com.meaf.apeps.input;

import com.meaf.apeps.model.entity.WeatherStateData;

import java.util.List;

public interface IWeatherParser {
  List<WeatherStateData> parse(String data) throws IllegalArgumentException;
}
