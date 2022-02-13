package com.example.weather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.weather.db.Citydb;
import com.example.weather.db.Countydb;
import com.example.weather.db.Provincedb;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

public class HandleUtil {
    public static boolean HandleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            Log.d("HANDLE","handleprovince");
            try{
                JSONArray jsonArray=new JSONArray(response);
                Log.d("HANDLE",jsonArray.length()+"");
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    Provincedb provincedb=new Provincedb();
                    provincedb.setProvince(jsonObject.getString("name"));
                    provincedb.setProvinceid(jsonObject.getInt("id"));
                    provincedb.save();
                }return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }return false;
    }
    public static boolean HandleCityResponse(String response,int provinceid){
        if (!TextUtils.isEmpty(response)){
           try {
               JSONArray jsonArray=new JSONArray(response);
               for (int i=0;i< jsonArray.length();i++){
                   JSONObject jsonObject=jsonArray.getJSONObject(i);
                   Citydb citydb=new Citydb();
                   citydb.setProvinceid(provinceid);
                   citydb.setCity(jsonObject.getString("name"));
                   citydb.setCityid(jsonObject.getInt("id"));
                   citydb.save();
               }return true;
           }catch (JSONException e){
               e.printStackTrace();
           }
        }return false;
    }
    public static boolean HandleCountyResponse(String response,int cityid){
        Log.d("response",response);
        if (!TextUtils.isEmpty(response)){
            Log.d("isempty","false");
            try {
                JSONArray jsonArray=new JSONArray(response);
                Log.d("length",jsonArray.length()+"");
                for (int i=0; i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    Countydb countydb=new Countydb();
                    countydb.setCityid(cityid);
                    countydb.setCounty(jsonObject.getString("name"));
                    countydb.setWeatherid(jsonObject.getString("weather_id"));
                    Log.d("weather",jsonObject.getString("weather_id"));
                    countydb.save();
                }return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        Log.d("ERROR","none");
        return false;
    }
}
