package com.appdeb.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.appdeb.weatherapp.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayList<WeatherModel> weatherModelArrayList;
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;

    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // for full screen mode of our application..
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        weatherModelArrayList  =new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this,weatherModelArrayList);
        binding.rvWeather.setAdapter(weatherAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);

        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        cityName = ""+getCityName(location.getLongitude(),location.getLatitude());

//        getWeatherInfo(cityName);
        getWeatherInfo("Delhi");

        binding.imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = binding.edCity.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter city name", Toast.LENGTH_SHORT).show();
                }
                else{
                    binding.tvCityName.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE)
        {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this, "Permission granted..", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Please provide permission!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude) {
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(longitude, latitude, 10);

            for (Address adr: addresses){
                if (adr!= null)
                {
                    String city = adr.getLocality();
                    if(city!=null || !city.equals(""))
                    {
                        cityName = ""+city;

                    }
                    else{
                        Toast.makeText(MainActivity.this, "User city not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  cityName;

    }

    private void getWeatherInfo(String cityName){
        String url="http://api.weatherapi.com/v1/forecast.json?key=864fa858127846f1acd101002222911&q=London&days=1&aqi=no&alerts=no\n";
        binding.tvCityName.setText(cityName);

        binding.pbLoading.setVisibility(View.GONE);
        binding.rlHome.setVisibility(View.VISIBLE);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                weatherModelArrayList.clear();
                try {
                    String temperature = ""+response.getJSONObject("current").getString("temp_c");
                    binding.tvTemperature.setText(temperature +"°C");
                    int isDay = response.getJSONObject("current").getInt("is_Day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");

                    Picasso.get().load("http:".concat(conditionIcon)).into(binding.imgIcon);

                    binding.tvCondition.setText(condition);
                    if (isDay==1){
                        Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS4J5khgovj3m302t2u2k7GpHocWp2UlXgKLA&usqp=CAU").into(binding.imgBackground);
                    }
                    else{
                        Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSaBYawYm4NFtbcRQaNLhBqkaCnNSbcSnNILg&usqp=CAU").into(binding.imgBackground);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecast0.getJSONArray("hour");

                    for (int i=0;i<hourArray.length();i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temp = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");

                        weatherModelArrayList.add(new WeatherModel(time, temp, img, wind));
                    }

                    weatherAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name...", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}