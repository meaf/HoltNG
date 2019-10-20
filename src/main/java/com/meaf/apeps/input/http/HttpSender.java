package com.meaf.apeps.input.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpSender {
//
//  public static void main(String[] args) {
//    try {
//      new HttpSender().sendUpdateRequest();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }

  public static void sendUpdateRequest() throws IOException {
    String resultUrl = prepareUrlString();

    URL url = new URL(resultUrl);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    con.setDoOutput(true);

    con.setRequestProperty("Content-Type", "application/json");

    String fullResponse = FullResponseBuilder.getFullResponse(con);

    con.disconnect();

//    return fullResponse;
  }

  private static String prepareUrlString() throws UnsupportedEncodingException {
    String resource = "https://api.solcast.com.au/weather_sites/";
    String siteID = "a286-d230-60ab-f89a";
    String dataType = "/estimated_actuals";

    String urlString = resource + siteID + dataType;

    Map<String, String> parameters = new HashMap<>();
    parameters.put("format", "json");
//    parameters.put("api_key", "4Og_JK6fbG_RzPVEs1Y40I2p11tC_0aX");
    parameters.put("api_key", "nhaoKZ3xZT1tGQLKhq-ibcWyVRUcHsRh");
    return urlString + "?" + ParameterStringBuilder.getParamsString(parameters);
  }

}
