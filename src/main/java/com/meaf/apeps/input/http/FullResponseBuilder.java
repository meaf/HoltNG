package com.meaf.apeps.input.http;

import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;

class FullResponseBuilder {


  static HttpResponse getFullResponse(HttpURLConnection con) throws IOException {
    HttpResponse response = new HttpResponse();

    response.setStatus(HttpStatus.resolve(con.getResponseCode()));
    response.setHeaders(con.getHeaderFields());

    Reader streamReader = new InputStreamReader(isError(con) ? con.getErrorStream() : con.getInputStream());

    BufferedReader in = new BufferedReader(streamReader);
    String inputLine;
    StringBuilder content = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();


    response.setResponse(content.toString());
    return response;
  }

  private static boolean isError(HttpURLConnection con) throws IOException {
    return con.getResponseCode() > 299;
  }
}
