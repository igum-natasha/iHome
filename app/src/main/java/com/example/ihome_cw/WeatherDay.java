package com.example.ihome_cw;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherDay {
  public class WeatherTemp {
    Double temp;
    Double temp_min;
    Double temp_max;
    Double humidity;
  }

  public class WeatherDescription {
    String main;
  }

  @SerializedName("main")
  private WeatherTemp temp;

  @SerializedName("weather")
  private List<WeatherDescription> desctiption;

  @SerializedName("name")
  private String city;

  @SerializedName("dt")
  private long timestamp;

  public WeatherDay(WeatherTemp temp, List<WeatherDescription> desctiption) {
    this.temp = temp;
    this.desctiption = desctiption;
  }

  public String getTemp() {
    return String.valueOf(temp.temp);
  }

  public String getTempMin() {
    return String.valueOf(temp.temp_min);
  }

  public String getTempMax() {
    return String.valueOf(temp.temp_max);
  }

  public String getTempInteger() {
    return String.valueOf(temp.temp.intValue());
  }

  public String getHumidity() {
    return temp.humidity.intValue() + "%";
  }

  public String getTempWithDegree() {
    return temp.temp.intValue() + "°C";
  }

  public String getRainfall() { return desctiption.get(0).main; }

  public String getCity() {
    return city;
  }
}
