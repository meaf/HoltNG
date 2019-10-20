package com.meaf.apeps.input.http;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

class HttpResponse {
  private HttpStatus status;
  private Map<String, List<String>> headers;
  private String response;

  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }

  public Map<String, List<String>> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, List<String>> headers) {
    this.headers = headers;
  }

  public boolean isError() {
    return status.isError();
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }
}
