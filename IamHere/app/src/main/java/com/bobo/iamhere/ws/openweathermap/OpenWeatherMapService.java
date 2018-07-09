package com.bobo.iamhere.ws.openweathermap;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapService {

    //http://api.openweathermap.org/data/2.5/forecast

    //Meteo dei prossimi 5 giorni
    @GET("data/2.5/forecast")
    Call<JsonObject> meteoForecast(@Query("lat") double latitudine, @Query("lon") double longitudine, @Query("lang") String lingua, @Query("appid") String apiKey);
}
