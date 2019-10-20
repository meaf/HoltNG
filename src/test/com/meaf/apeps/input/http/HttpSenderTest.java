package com.meaf.apeps.input.http;

import com.meaf.apeps.calculations.aggregate.WeatherAggregator;
import com.meaf.apeps.input.IWeatherParser;
import com.meaf.apeps.model.entity.WeatherStateData;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class HttpSenderTest {
  @Test
  public void testConnection() throws JSONException {
    HttpResponse response = HttpSender.sendUpdateRequest();

    Assert.assertNotNull(response);
    Assert.assertFalse(response.isError());
    Assert.assertFalse(response.getHeaders().isEmpty());
    Assert.assertFalse(response.getResponse().isEmpty());

    String responseContent = response.getResponse();

    IWeatherParser parser = new HttpResponseParser();
    List<WeatherStateData> dataList = parser.parse(responseContent);

    List<WeatherStateData> weatherStateDataList = WeatherAggregator.hourlyToDaily(dataList, WeatherAggregator.EDataSource.http);


  }
}
