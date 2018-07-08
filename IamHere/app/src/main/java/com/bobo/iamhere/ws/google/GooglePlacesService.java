package com.bobo.iamhere.ws.google;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface GooglePlacesService {

    //Posti nelle vicinanze
    @GET("maps/api/place/nearbysearch/json")
    Call<JsonObject> nearbyPlaces(@Query("location") String location, @Query("radius") int radius, @Query("type") String type, @Query("key") String apiKey);

}
