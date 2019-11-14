package com.meaf.apeps.input.csv;

import com.meaf.apeps.input.IWeatherParser;
import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.utils.DateUtils;
import com.vaadin.server.UploadException;
import com.vaadin.ui.Upload;

import java.io.OutputStream;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

public class CSVFileReceiver implements Upload.Receiver {
  private static final String header = "PeriodEnd,PeriodStart,Period,CloudOpacity,Dhi,Dni,Ebh,Ghi,WindDirection10m,WindSpeed10m";
  private static final int endLineByte = '\n';
  CSVRowParser parser = new CSVRowParser();
  private List<String> rows = new LinkedList<>();
  private java.util.Date filterDate;




  @Override
  public OutputStream receiveUpload(final String filename, final String MIMEType) {
    return new OutputStream() {
      StringBuffer stringBuffer = new StringBuffer();
      boolean headerCorrect = false;
      boolean needToCheckHeader = true;
      int rowCount = 0;

      @Override
      public void write(final int b) {
        if (b != endLineByte) {
          stringBuffer.append((char) b);
          return;
        }
        rowCount++;

        String row = stringBuffer.toString();
        if (needToCheckHeader && !headerCorrect && isIllegalHeader(row)) {
          reset(stringBuffer);
          return;
        }
        if (rowCount == 1) {
          reset(stringBuffer);
          return;
        }

        if (dateComplies(row)) {
          rows.add(row);
        }

        reset(stringBuffer);

      }

      private void reset(StringBuffer stringBuffer) {
        stringBuffer.setLength(0);
        stringBuffer.trimToSize();
      }

      private boolean isIllegalHeader(String row) {
        if (checkHeader(row)) {
          needToCheckHeader = false;
          headerCorrect = true;
          return false;
        } else {
          try {
            throw new UploadException("Illegal file header format, [" + row + "]");
          } catch (UploadException e) {
            e.printStackTrace();
          }
        }
        return true;
      }
    };
  }

  private boolean checkHeader(String row) {
    List<String> headerRows = Arrays.asList(row.split(","));
    List<String> requiredFields = Arrays.asList("PeriodEnd","CloudOpacity","Dhi","Dni","Ebh","Ghi","WindSpeed10m");
    for(String fld : requiredFields) {
      int order = headerRows.indexOf(fld);
      if(order == -1)
        return false;
      parser.addColumn(fld, order);
    }
    return true;
  }

  private boolean dateComplies(String row) {
    return -1 < DateUtils.zonedTimeStringToInstant(row.split(",")[0]).compareTo(filterDate);
  }

  public List<String> getLoadedRows() {
    return rows;
  }

  public List<WeatherStateData> parseRows() {
    return parser.parse(String.join("\n", rows));
  }


  public void setDateScope(Date filterDate) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(filterDate);
    cal.add(Calendar.DAY_OF_YEAR, 1);

    this.filterDate = cal.getTime();
  }

  public void reset() {
    rows.clear();
  }
}
