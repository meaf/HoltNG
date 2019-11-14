package com.meaf.apeps.input.http;

import com.meaf.apeps.calculations.aggregate.WeatherAggregator;
import com.meaf.apeps.input.IWeatherParser;
import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.model.repository.LocationKeyRepository;
import com.meaf.apeps.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    return WeatherAggregator.hourlyToDaily(list, WeatherAggregator.EDataSource.http)
        .stream()
        .peek(d -> d.setDate(DateUtils.asSqlDate(new Date(d.getDate().getTime() + 1000 * 60 * 60 * 6))))
        .collect(Collectors.toList());
  }

}
