package com.meaf.apeps.input.http;

import com.meaf.apeps.calculations.aggregate.WeatherAggregator;
import com.meaf.apeps.input.IWeatherParser;
import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.model.repository.LocationKeyRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestHttpUpdate {

  private IWeatherParser httpResponseParser;
  private LocationKeyRepository locationKeyRepository;

  public RequestHttpUpdate(IWeatherParser httpResponseParser, LocationKeyRepository locationKeyRepository) {
    this.httpResponseParser = httpResponseParser;
    this.locationKeyRepository = locationKeyRepository;
  }

  public List<WeatherStateData> requestUpdate(Long locationId) {
    HttpResponse httpResponse = HttpSender.sendUpdateRequest(locationKeyRepository.findKeysForLocation(locationId));
    if (httpResponse == null || httpResponse.isError())
      return null;
    List<WeatherStateData> list = httpResponseParser.parse(httpResponse.getResponse());
    return WeatherAggregator.hourlyToDaily(list, WeatherAggregator.EDataSource.http);
  }

}
