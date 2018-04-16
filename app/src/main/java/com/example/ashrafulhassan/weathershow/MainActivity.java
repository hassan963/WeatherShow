package com.example.ashrafulhassan.weathershow;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.ashrafulhassan.weathershow.network.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import xyz.matteobattilana.library.WeatherView;


public class MainActivity extends AppCompatActivity {

    private TextView dateTextView, locationTextView, weatheverdict, tempTextView, maxTextView,
            minTextView, speedTextView, humidityTextView, degreeTextView, sunriseTextView,
            sunsetTextView, status;

    private String main, description, temp, pressure, humidity, temp_min, temp_max, speed, deg, sunrise, sunset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        WeatherView mWeatherView = findViewById(R.id.weather);
        mWeatherView.setWeather(WeatherView.weatherStatus.RAIN);
        mWeatherView.startAnimation();

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
        status = findViewById(R.id.status);


        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dateTextView.setText(date);

        Glide.with(getApplicationContext())
                .load(R.drawable.rainning).centerCrop()
                .into(weatherstatus);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                requestVolley("dhaka");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String convertMillis(long milliseconds){

        long unixSeconds = milliseconds;
        // convert seconds to milliseconds
        Date date = new java.util.Date(unixSeconds*1000L);
        // the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+6"));
        String formattedDate = sdf.format(date);

        return formattedDate;


    }

    private void requestVolley(final String cityName){


        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://api.openweathermap.org/data/2.5/weather?q="+ cityName + ",bd&units=metric&appid=1efc0fc4375a4a874205759effcc7e36",
                (String) null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("weather");

                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String title = jsonObject.getString("main");
                    description = jsonObject.getString("description");

                    JSONObject jsonObject1 = response.getJSONObject("main");
                    temp = jsonObject1.getString("temp")+"°";
                    pressure = jsonObject1.getString("pressure");
                    humidity = jsonObject1.getString("humidity")+" hPa";
                    temp_min = jsonObject1.getString("temp_min")+"°";
                    temp_max = jsonObject1.getString("temp_max")+"°";

                    JSONObject jsonObject2 = response.getJSONObject("wind");
                    speed = jsonObject2.getString("speed")+"mps";
                    deg = jsonObject2.getString("deg")+"°";

                    JSONObject jsonObject3 = response.getJSONObject("sys");
                    sunrise = jsonObject3.getString("sunrise");
                    sunset = jsonObject3.getString("sunset");

                    Log.v("444 *** ",sunset+"  "+sunrise);

                    StringBuilder mainString = new StringBuilder("");
                    for (int i = 0; i<title.length(); i++){
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
                    status.setText(mainString);


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
