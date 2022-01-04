package com.a7apps.weatherapp.constants;
public class Constants {
    private static final String API_KEY_WEATHERAPI = "52d470b0b871429eb60144422212908";

    public static String getUrlForecast(String local){
        String url = "https://api.weatherapi.com/v1/forecast.json?key="+API_KEY_WEATHERAPI+"&q="+local+"&days=3&aqi=no&alerts=no";
        return url;
    }
}
