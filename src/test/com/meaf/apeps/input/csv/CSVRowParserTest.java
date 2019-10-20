package com.meaf.apeps.input.csv;

import com.meaf.apeps.model.entity.WeatherStateData;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;


public class CSVRowParserTest {
//PeriodEnd,PeriodStart,Period,CloudOpacity,Dhi,Dni,Ebh,Ghi,WindDirection10m,WindSpeed10m
  @Test
  public void processOneRowFile(){
    WeatherStateData data = parse("2007-01-01T02:00:00Z,2007-01-01T01:00:00Z,PT60M,0.2,1,2,3,4,271,7.4");

    Assert.assertEquals(new Date(2007,1,1), data.getDate());
    Assert.assertEquals(new Double(0.2), data.getCloudOpacity());
    Assert.assertEquals(new Integer(0), data.getDhi());
    Assert.assertEquals(new Integer(1), data.getDni());
    Assert.assertEquals(new Integer(2), data.getEbh());
    Assert.assertEquals(new Integer(3), data.getGhi());
    Assert.assertEquals(new Double(7.4), data.getWindSpeed());
  }

  public static WeatherStateData parse(String str){
    return CSVRowParser.parse(str);
  }

}
