package com.a7apps.weatherapp.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SavedLocal {
    private Context context;
    public static final String SHAREDPREF_NAME = "localization";
    public static final String KEY_LOCAL = "local";
    public SharedPreferences sharedPreferences;

    public SavedLocal(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHAREDPREF_NAME, Context.MODE_PRIVATE);
    }

    public String sharedPrefLocal(){
        String local = sharedPreferences.getString(KEY_LOCAL,null);
        return local;
    }

    public void savePref(String local){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LOCAL,local);
        editor.apply();
    }
}
