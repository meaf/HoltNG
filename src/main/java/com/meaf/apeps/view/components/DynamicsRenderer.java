package com.meaf.apeps.view.components;

import com.vaadin.ui.renderers.HtmlRenderer;
import elemental.json.JsonValue;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class DynamicsRenderer extends HtmlRenderer {

  @Override
  public JsonValue encode(String val) {
    if(val.isEmpty())
      return super.encode("N/A");

    String[] nums = val.split(" ");

    double fc = Double.parseDouble(nums[0]);
    double dynamics = fc - Double.parseDouble(nums[1]);

    boolean isPositiveChange = dynamics > 0;
    String colorStyle =
      String.format("style=\"color:%s;\"",
        isPositiveChange
          ? "#77FF77"
          : "#FF7777"
      );

    String value = String.format("<span style=\"pointer-events: none;\">%s <small %s>%s%s</small></span>",
      Jsoup.clean(Double.toString(fc), Whitelist.basic()),
      colorStyle,
      isPositiveChange ? "+" : "-",
      Jsoup.clean(Double.toString(dynamics), Whitelist.basic())
    );

    return super.encode(value);
  }
}