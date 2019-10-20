package com.meaf.apeps.input.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class HttpSender {
  static HttpResponse sendUpdateRequest() {
    HttpURLConnection con = null;
    try {

      URL url = new URL(prepareUrlString());
      con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setDoOutput(true);

      con.setRequestProperty("Content-Type", "application/json");

      return FullResponseBuilder.getFullResponse(con);

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (con != null)
        con.disconnect();
    }
    return null;
  }

  private static String prepareUrlString() throws UnsupportedEncodingException {
    String kievSiteID1 = "3faa-0298-d8db-463f";
    String kievAPIKey1 = "nhaoKZ3xZT1tGQLKhq-ibcWyVRUcHsRh";

    String kievSiteID2 = "a286-d230-60ab-f89a";
    String kievAPIKey2 = "4Og_JK6fbG_RzPVEs1Y40I2p11tC_0aX";


    String resource = "https://api.solcast.com.au/weather_sites/";
    String dataType = "/estimated_actuals";

    String urlString = resource + kievSiteID1 + dataType;

    Map<String, String> parameters = new HashMap<>();
    parameters.put("format", "json");
    parameters.put("api_key", kievAPIKey1);
    return urlString + "?" + ParameterStringBuilder.getParamsString(parameters);
  }

}
