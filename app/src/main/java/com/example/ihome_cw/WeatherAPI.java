package com.example.ihome_cw;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class WeatherAPI {
  public static String KEY = "a489972de36b54432056bbefac20242b";
  public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
  private static Retrofit retrofit = null;

  public interface ApiInterface {
    @GET("weather")
    Call<WeatherDay> getToday(
        @Query("lat") Double lat,
        @Query("lon") Double lon,
        @Query("units") String units,
        @Query("appid") String appid);
  }

  public static Retrofit getClient() {
    if (retrofit == null) {
      retrofit =
          new Retrofit.Builder()
              .baseUrl(BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build();
    }
    return retrofit;
  }
}
