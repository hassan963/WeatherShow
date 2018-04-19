package com.example.ashrafulhassan.weathershow;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.github.clans.fab.FloatingActionButton;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.ashrafulhassan.weathershow.network.VolleyRequest;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.text.Selection;


import xyz.matteobattilana.library.WeatherView;


public class MainActivity extends AppCompatActivity{

    private TextView dateTextView, locationTextView, weatheverdict, tempTextView, maxTextView,
            minTextView, speedTextView, humidityTextView, degreeTextView, sunriseTextView,
            sunsetTextView, timeTextView;

    private String main, description, temp, pressure, humidity, temp_min, temp_max, speed, deg, sunrise, sunset;
    private FloatingActionButton menu1, menu2;
    private FloatingActionMenu floatingActionMenu;
    private WeatherView mWeatherView;

    private EditText searchEditTextView;
    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().setElevation(0);

        mWeatherView = findViewById(R.id.weather);


        ImageView weatherstatus = findViewById(R.id.weatherstatus);
        dateTextView = findViewById(R.id.dateTextView);
        locationTextView = findViewById(R.id.locationTextView);
        weatheverdict = findViewById(R.id.weatheverdict);
        maxTextView = findViewById(R.id.maxTextView);
        tempTextView = findViewById(R.id.tempTextView);
        minTextView = findViewById(R.id.minTextView);
        speedTextView = findViewById(R.id.speedTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        degreeTextView = findViewById(R.id.degreeTextView);
        sunriseTextView = findViewById(R.id.sunriseTextView);
        sunsetTextView = findViewById(R.id.sunsetTextView);
        timeTextView = findViewById(R.id.timeTextView);

        ImageView searchButton = findViewById(R.id.searchButton);
        ImageView status = findViewById(R.id.status);
        searchEditTextView = findViewById(R.id.searchEditTextView);

        requestVolleyGPS(1.2,1.3);
        requestVolleyTimeGPS(1.2,1.3);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = searchEditTextView.getText().toString();

                requestVolley(location);
                String result = location.substring(location.lastIndexOf(',') + 1).trim();
                requestVolleyTime(result);
                searchEditTextView.setText("");

            }
        });




        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dateTextView.setText(date);

        Glide.with(getApplicationContext())
                .load(R.drawable.weathernew).centerCrop()
                .into(weatherstatus);

        Glide.with(getApplicationContext()).load(R.drawable.weatherguy).centerCrop().into(status);

    }

    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);
        finish();
        System.exit(0);
    }


    public static String convertMillis(long milliseconds) {

        long unixSeconds = milliseconds;
        // convert seconds to milliseconds
        Date date = new java.util.Date(unixSeconds * 1000L);
        // the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+6"));
        String formattedDate = sdf.format(date);


        return formattedDate;


    }


    private void requestVolley(final String cityName) {


        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&units=metric&appid=1efc0fc4375a4a874205759effcc7e36",
                (String) null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("weather");

                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String title = jsonObject.getString("main");
                    description = jsonObject.getString("description");

                    JSONObject jsonObject1 = response.getJSONObject("main");
                    temp = jsonObject1.getString("temp") + "°";
                    pressure = jsonObject1.getString("pressure");
                    humidity = jsonObject1.getString("humidity") + " hPa";
                    temp_min = jsonObject1.getString("temp_min") + "°";
                    temp_max = jsonObject1.getString("temp_max") + "°";

                    JSONObject jsonObject2 = response.getJSONObject("wind");
                    speed = jsonObject2.getString("speed") + "mps";
                    deg = jsonObject2.getString("deg") + "°";

                    JSONObject jsonObject3 = response.getJSONObject("sys");
                    sunrise = jsonObject3.getString("sunrise");
                    sunset = jsonObject3.getString("sunset");

                    Log.v("444 *** ", sunset + "  " + sunrise);

                    StringBuilder mainString = new StringBuilder("");
                    for (int i = 0; i < title.length(); i++) {
                        mainString.append(title.charAt(i)).append("\n");
                    }

                    sunrise = convertMillis(Long.parseLong(sunrise));
                    sunset = convertMillis(Long.parseLong(sunset));

                    locationTextView.setText(cityName);
                    weatheverdict.setText(description);
                    tempTextView.setText(temp);
                    maxTextView.setText(temp_max);
                    minTextView.setText(temp_min);
                    speedTextView.setText(speed);
                    humidityTextView.setText(humidity);
                    degreeTextView.setText(deg);
                    sunriseTextView.setText(sunrise);
                    sunsetTextView.setText(sunset);
                    // status.setText(mainString);


                    for (int i = 0; i < description.length(); i++) {
                        if (description.charAt(i) == 'r') {
                            if (i < description.length() && description.charAt(i + 1) == 'a') {
                                mWeatherView.setWeather(WeatherView.weatherStatus.RAIN);
                                mWeatherView.startAnimation();
                            }
                        }
                    }

                    /*mWeatherView.setWeather(WeatherView.weatherStatus.RAIN);
                    mWeatherView.startAnimation();*/


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleyRequest.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void requestVolleyTime(final String countryCode) {

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "http://api.timezonedb.com/v2/list-time-zone?key=SU17ZY5OYTE7&format=json&country=" + countryCode,
                (String) null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("zones");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String gmtOffset = jsonObject.getString("gmtOffset");
                    String timestamp = jsonObject.getString("timestamp");
                    String countryName = jsonObject.getString("countryName");

                    Long gmt = Long.parseLong(gmtOffset);
                    Long time = Long.parseLong(timestamp);
                    time = time - gmt;
                    //convertMillis(time);
                    timeTextView.setText(String.valueOf(convertMillis(time)));
                    //Toast.makeText(MainActivity.this, ""+convertMillis(time), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleyRequest.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void requestVolleyGPS( double lat,  double lon) {

        lat = 23.8848663;  lon = 90.3896801;

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://api.openweathermap.org/data/2.5/weather?lat="+String.valueOf(lat)+"&lon="+String.valueOf(lon)+"&units=metric&appid=1efc0fc4375a4a874205759effcc7e36",
                (String) null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("weather");

                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String title = jsonObject.getString("main");
                    description = jsonObject.getString("description");

                    JSONObject jsonObject1 = response.getJSONObject("main");
                    temp = jsonObject1.getString("temp") + "°";
                    pressure = jsonObject1.getString("pressure");
                    humidity = jsonObject1.getString("humidity") + " hPa";
                    temp_min = jsonObject1.getString("temp_min") + "°";
                    temp_max = jsonObject1.getString("temp_max") + "°";

                    JSONObject jsonObject2 = response.getJSONObject("wind");
                    speed = jsonObject2.getString("speed") + "mps";
                    deg = jsonObject2.getString("deg") + "°";

                    JSONObject jsonObject3 = response.getJSONObject("sys");
                    sunrise = jsonObject3.getString("sunrise");
                    sunset = jsonObject3.getString("sunset");



                    sunrise = convertMillis(Long.parseLong(sunrise));
                    sunset = convertMillis(Long.parseLong(sunset));

                    locationTextView.setText("dhaka");
                    weatheverdict.setText(description);
                    tempTextView.setText(temp);
                    maxTextView.setText(temp_max);
                    minTextView.setText(temp_min);
                    speedTextView.setText(speed);
                    humidityTextView.setText(humidity);
                    degreeTextView.setText(deg);
                    sunriseTextView.setText(sunrise);
                    sunsetTextView.setText(sunset);
                    // status.setText(mainString);


                    for (int i = 0; i < description.length(); i++) {
                        if (description.charAt(i) == 'r') {
                            if (i < description.length() && description.charAt(i + 1) == 'a') {
                                mWeatherView.setWeather(WeatherView.weatherStatus.RAIN);
                                mWeatherView.startAnimation();
                            }
                        }
                    }

                    /*mWeatherView.setWeather(WeatherView.weatherStatus.RAIN);
                    mWeatherView.startAnimation();*/


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleyRequest.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void requestVolleyTimeGPS(double lat, double lng) {
        lat = 23.8848663; lng = 90.3896801;

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "http://api.timezonedb.com/v2/get-time-zone?key=SU17ZY5OYTE7&format=json&by=position&lat=" + lat + "&lng=" + lng,
                (String) null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String time = response.getString("formatted");


                    if(time.contains(" ")){
                        String s = time.substring(0, time.indexOf(" ")); dateTextView.setText(s);
                    }

                    String result = time.substring(time.lastIndexOf(' ') + 1).trim();

                    timeTextView.setText(result);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleyRequest.getInstance().addToRequestQueue(jsonObjectRequest);
    }



}
