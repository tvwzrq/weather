package com.example.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weather.gson.Forecast;
import com.example.weather.gson.Weather;
import com.example.weather.util.HandleUtil;
import com.example.weather.util.HttpUtil;
import com.google.gson.internal.bind.JsonTreeReader;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Weather_Activity extends AppCompatActivity {
    private ScrollView weatherlayout;
    private TextView titlecity;
    private TextView lastupdatetime;
    private TextView degreeinfo;
    private TextView weatherinfotext;
    private LinearLayout forecastlayout;
    private TextView aqitext;
    private TextView pm25text;
    private TextView comforttext;
    private TextView carwashtext;
    private TextView sporttext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherlayout=findViewById(R.id.weather_layout);
        titlecity=findViewById(R.id.currentcity);
        lastupdatetime=findViewById(R.id.update_time);
        degreeinfo=findViewById(R.id.temperature);
        weatherinfotext=findViewById(R.id.weather_info);
        forecastlayout=findViewById(R.id.forecast_layout);
        aqitext=findViewById(R.id.aqitext);
        pm25text=findViewById(R.id.pm_text);
        comforttext=findViewById(R.id.comfort_sug);
        carwashtext=findViewById(R.id.carwash_sug);
        sporttext=findViewById(R.id.sport_sug);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherstring=sharedPreferences.getString("weather",null);
        if (weatherstring!=null){
            //加载缓存天气
            Weather weather= HandleUtil.HandleWeatherResponse(weatherstring);
            ShowWeather(weather);
        }else {
            String weatherid=getIntent().getStringExtra("weatherid");
            weatherlayout.setVisibility(View.INVISIBLE);
            requestweather(weatherid );
        }


    }
    public  void ShowWeather(Weather weather){
          String cityname=weather.basic.cityname;
          String updatetime=weather.basic.update.updatetime;
          String degree=weather.now.temperature;
         String weatherinfo=weather.now.more.info;
         titlecity.setText(cityname);
         lastupdatetime.setText(updatetime);
         degreeinfo.setText(degree);
         weatherinfotext.setText(weatherinfo);
         forecastlayout.removeAllViews();
         for (Forecast forecast:weather.forecastList){
             View view= LayoutInflater.from(this).inflate(R.layout.forecastitem,forecastlayout,false);
             TextView datetext=view.findViewById(R.id.date_text);
             TextView textinfo=view.findViewById(R.id.text_info);
             TextView maxtem=view.findViewById(R.id.max_tem);
             TextView mintem=view.findViewById(R.id.min_tem);
             datetext.setText(forecast.date);
             textinfo.setText(forecast.more.info);
             maxtem.setText(forecast.temperature.max);
             mintem.setText(forecast.temperature.min);
             forecastlayout.addView(view);
         }
         if (weather.aqi!=null){
             aqitext.setText(weather.aqi.aqiCity.api);
             pm25text.setText(weather.aqi.aqiCity.pm25);
         }
         String comfort="舒适度:"+ weather.suggestion.comfort.info;
         String carwash="洗车指数"+weather.suggestion.carwash.info;
         String sport="运动建议"+weather.suggestion.sport.info;
         comforttext.setText(comfort);
         carwashtext.setText(carwash);
         sporttext.setText(sport);
         weatherlayout.setVisibility(View.VISIBLE);
    }
    public void requestweather(String weatherid){
        String weatherurl="http://guolin.tech/api/weather?cityid="+weatherid+"&key=2cebde5f3e5344dab112ace8e6652882";
        Log.d("requesturl",weatherurl);
        HttpUtil.sendrquest(weatherurl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Weather_Activity.this,"onfailure",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
               final String responsetext=response.body().string();
                Log.d("responsetext",responsetext);
               final Weather weather=HandleUtil.HandleWeatherResponse(responsetext);
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       if (weather!=null){
                           SharedPreferences.Editor editor=PreferenceManager
                                   .getDefaultSharedPreferences(Weather_Activity.this).edit();
                           editor.putString("weather",responsetext);
                           editor.apply();
                           ShowWeather(weather);
                       }else {
                           Toast.makeText(Weather_Activity.this,"error",Toast.LENGTH_SHORT).show();
                       }
                   }
               });
            }
        });
    }
}
