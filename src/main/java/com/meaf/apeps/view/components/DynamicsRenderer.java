package com.meaf.apeps.view.components;

import com.vaadin.ui.renderers.HtmlRenderer;
import elemental.json.JsonValue;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import static com.meaf.apeps.utils.Formatter.format;

public class DynamicsRenderer extends HtmlRenderer {

  @Override
  public JsonValue encode(String val) {
    if(val.isEmpty())
      return super.encode(" - ");

    String[] data = val.split(" ");

    double fc = Double.parseDouble(data[0]);
    double dynamics = fc - Double.parseDouble(data[1]);
    String month = data[2];
    String year = data[3];

    boolean isPositiveChange = dynamics > 0;
    String colorStyle =
      String.format("style=\"color:%s;\"",
        isPositiveChange
          ? "#0f9427"
          : "#eb4335"
      );

    String value = String.format("<span style=\"pointer-events: none;\">%s <small %s><b>%s%s</b></small> %s %s</span>",
      Jsoup.clean(format(fc), Whitelist.basic()),
      colorStyle,
      isPositiveChange ? "+" : "",
      Jsoup.clean(format(dynamics), Whitelist.basic()),
      Jsoup.clean(month, Whitelist.basic()),
      Jsoup.clean(year, Whitelist.basic())
    );

    return super.encode(value);
  }
}