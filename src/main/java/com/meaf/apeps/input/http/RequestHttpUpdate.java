package com.meaf.apeps.input.http;

import com.meaf.apeps.calculations.aggregate.WeatherAggregator;
import com.meaf.apeps.input.IWeatherParser;
import com.meaf.apeps.model.entity.WeatherStateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestHttpUpdate {

  @Autowired
  private IWeatherParser httpResponseParser;

  public List<WeatherStateData> requestUpdate(){
    HttpResponse httpResponse = HttpSender.sendUpdateRequest();
    if(httpResponse == null || httpResponse.isError())
      return null;
    List<WeatherStateData> list = httpResponseParser.parse(httpResponse.getResponse());
    return WeatherAggregator.hourlyToDaily(list, WeatherAggregator.EDataSource.http);
  }

}
