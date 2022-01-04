package com.a7apps.weatherapp.connection;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConectAPI {
    private Context context;
    private RequestQueue mQueue;

    public ConectAPI(Context context) {
        this.context = context;
        mQueue = Volley.newRequestQueue(context);
    }


    public void forecastCurrentRequest(String url, final ArrayList<String> dataCurrent){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject current = response.getJSONObject("current");
                    JSONObject objMinMax = response.getJSONObject("forecast");
                    JSONArray teste = objMinMax.getJSONArray("forecastday");

                    JSONObject object = teste.getJSONObject(0);
                    JSONObject objMaxMin = object.getJSONObject("day");

                    int int_max = objMaxMin.getInt("maxtemp_c");
                    int int_min = objMaxMin.getInt("mintemp_c");
                    int temp_c = current.getInt("temp_c");
                    int hum = current.getInt("humidity");

                    dataCurrent.add(String.valueOf(temp_c));
                    dataCurrent.add(String.valueOf(int_max));
                    dataCurrent.add(String.valueOf(int_min));
                    dataCurrent.add(String.valueOf(hum));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, String.valueOf(e) , Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //printConnectionError(error);
            }
        });
        mQueue.add(request);

    }

    public void printConnectionError(VolleyError objError){
        String message = "";
        if (objError instanceof NetworkError){
            message = "Cannot getPosters to Internet...\nPlease check your connection!(NetworkError)";
        }else if(objError instanceof ServerError){
            message = "The server could not be found. Please try again after some time!!";
        }else if (objError instanceof AuthFailureError){
            message = "Cannot getPosters to Internet...Please check your connection!(AuthFailureError)";
        }else if (objError instanceof ParseError){
            message = "Parsing error! Please try again after some time!!";
        }else if (objError instanceof NoConnectionError){
            message = "Cannot getPosters to Internet...Please check your connection! (NoConnectionError)";
        }else if (objError instanceof TimeoutError){
            message = "Connection TimeOut! Please check your internet connection.";
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
