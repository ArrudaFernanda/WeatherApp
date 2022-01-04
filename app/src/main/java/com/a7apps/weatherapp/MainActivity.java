package com.a7apps.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.a7apps.weatherapp.connection.ConectAPI;
import com.a7apps.weatherapp.constants.Constants;
import com.a7apps.weatherapp.preferences.SavedLocal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener{
    private Button btnAtt;
    private TextView txtCity, txtHumidity, txtTemp, txtMinMax;
    private LocationManager locationManager;
    private SavedLocal savedLocal;
    ConectAPI conectAPI;
    ArrayList<String> dataCurrent = new ArrayList<>();
    String teste = "Teste";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        grantPermission();

        btnAtt = findViewById(R.id.btnAtt);
        txtCity = findViewById(R.id.city);
        txtMinMax = findViewById(R.id.minMax);
        txtHumidity = findViewById(R.id.humidity);
        txtTemp = findViewById(R.id.temp);
        savedLocal = new SavedLocal(this);
        conectAPI = new ConectAPI(this);

        conect();
        btnAtt.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               conect();
           }
       });
    }

    public void conect(){
        if (savedLocal.sharedPrefLocal() == null){
            txtCity.setText("...");
            checkLocationIsEnableOrNot();
            getLocation();
        }

        if (savedLocal.sharedPrefLocal() != null){
            txtCity.setText(savedLocal.sharedPrefLocal());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    conectAPI.forecastCurrentRequest(Constants.getUrlForecast(savedLocal.sharedPrefLocal()),dataCurrent);
                    try {
                        Thread.sleep(12000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtTemp.setText(dataCurrent.get(0)+"°C");
                            txtMinMax.setText(dataCurrent.get(2)+"°/"+dataCurrent.get(1)+"°");
                            txtHumidity.setText("Humidity: "+dataCurrent.get(3)+"%");
                        }
                    });
                }
            }).start();
        }
    }

    public void grantPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
         && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    private void checkLocationIsEnableOrNot(){
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnable = false;
        boolean networkEnable = false;

        try {
            gpsEnable = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            networkEnable = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (!gpsEnable && !networkEnable){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Enable GPS service")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //this intent redirect us to the location settings, if gps is disabled this dialog will be show
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton("Cancel",null)
                    .show();
        }

    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500,5,(LocationListener) this);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            txtCity.setText(addresses.get(0).getSubAdminArea());
            savedLocal.savePref(addresses.get(0).getSubAdminArea());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }
}