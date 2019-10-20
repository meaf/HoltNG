package com.meaf.apeps.input.http;

import com.meaf.apeps.input.IWeatherParser;
import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.utils.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

class HttpResponseParser implements IWeatherParser {
  public List<WeatherStateData> parse(String responseStr) throws IllegalArgumentException {
    try {
      final JSONObject obj = new JSONObject(responseStr);
      final JSONArray geodata = obj.getJSONArray("estimated_actuals");
      final int n = geodata.length();

      List<WeatherStateData> weatherStateDataList = new LinkedList<>();

      for (int i = 0; i < n; ++i) {
        final JSONObject jsonObject = geodata.getJSONObject(i);
        weatherStateDataList.add(parseRow(jsonObject));
      }

      return weatherStateDataList;
    } catch (JSONException e) {
      throw new IllegalArgumentException(e);
    }

  }

  private WeatherStateData parseRow(JSONObject jsonObject) throws JSONException {
    WeatherStateData data = new WeatherStateData();
    data.setDni(jsonObject.getInt("dni"));
    data.setDhi(jsonObject.getInt("dhi"));
    data.setGhi(jsonObject.getInt("ghi"));
    data.setEbh(jsonObject.getInt("ebh"));
    data.setCloudOpacity(jsonObject.getDouble("cloud_opacity"));
    data.setDate(DateUtils.zonedTimeStringToInstant(jsonObject.getString("period_end")));
    return data;
  }

}
