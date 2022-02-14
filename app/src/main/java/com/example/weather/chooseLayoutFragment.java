package com.example.weather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.db.Citydb;
import com.example.weather.db.Countydb;
import com.example.weather.db.Provincedb;
import com.example.weather.util.HandleUtil;
import com.example.weather.util.HttpUtil;

import org.litepal.LitePal;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import javax.crypto.Cipher;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class chooseLayoutFragment extends Fragment {
    public static final int LEVEL_PROVINCE=0;
    public  static final int LEVEL_CITY=1;
    public  static final int LEVEL_COUNTY=2;
    private TextView areaname;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button back;
    private  int current_level;
    private Provincedb selectedprovince;
    private Citydb selectedcity;
    private Countydb selectedcounty;
    List<String> areas=new ArrayList<>();
    private AreaAdapter adapter;
    private List<Provincedb> provincedbs;
    private List<Citydb> citydbs;
    private List<Countydb> countydbs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.chooselayout,container,false);
        areaname=view.findViewById(R.id.titletext);
        recyclerView=view.findViewById(R.id.arealist);
        progressBar=view.findViewById(R.id.progressbar);
        back=view.findViewById(R.id.back);
        adapter=new AreaAdapter(areas);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        current_level=0;
        progressBar.setVisibility(View.GONE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_level==LEVEL_CITY){
                    queryprovinces();
                }
                if (current_level==LEVEL_COUNTY){
                    querycityies();
                }
            }
        });
        queryprovinces();
         return view;
    }

    public void queryprovinces(){
        areaname.setText("CHINA");
        back.setVisibility(View.GONE);
        provincedbs= LitePal.findAll(Provincedb.class);
        if (provincedbs.size()>0){
            Log.d("province",String.valueOf(provincedbs.size()));
            areas.clear();
            for (Provincedb provincedb:provincedbs){
                areas.add(provincedb.getProvince());
            }
            adapter.notifyDataSetChanged();
            current_level=LEVEL_PROVINCE;
            recyclerView.setSelected(false);
        } else {
            String adress="http://guolin.tech/api/china";
            Log.d("REQUEST","request"+adress);
            queryfromsever(adress,"province");
        }

    }
    public void querycityies(){
        areaname.setText(selectedprovince.getProvince());
        back.setVisibility(View.VISIBLE);
        citydbs=LitePal.where("provinceid=?",String.valueOf(selectedprovince.getProvinceid())).find(Citydb.class);
        if (citydbs.size()>0){
            areas.clear();
            for (Citydb citydb:citydbs){
                areas.add(citydb.getCity());
            }
            adapter.notifyDataSetChanged();
            current_level=LEVEL_CITY;
            recyclerView.setSelected(false);
        }
        else {
            String adress="http://guolin.tech/api/china/"+selectedprovince.getProvinceid();
            queryfromsever(adress,"city");
        }
    }
    public void querycounties(){
        areaname.setText(selectedcity.getCity());
        back.setVisibility(View.VISIBLE);
        countydbs=LitePal.where("cityid=?",String.valueOf(selectedcity.getCityid())).find(Countydb.class);
        if (countydbs.size()>0){
            areas.clear();
            for (Countydb countydb:countydbs){
                areas.add(countydb.getCounty());
            }
            adapter.notifyDataSetChanged();
            current_level=LEVEL_COUNTY;
        }else {
            String adress="http://guolin.tech/api/china/"+selectedprovince.getProvinceid()+"/"+selectedcity.getCityid();
            Log.d("request",adress);
            queryfromsever(adress,"county");
        }
    }
   public void queryfromsever(String adress ,String type){
        progressBar.setVisibility(View.VISIBLE);
       HttpUtil.sendrquest(adress, new Callback() {
           @Override
           public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.d("ERROR",e.getMessage());
              getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(),"ERROR",Toast.LENGTH_SHORT).show();
                    }
                });

           }

           @Override
           public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
               String responsetext=response.body().string();
               Log.d("RESPONSE",responsetext);
               boolean result=false;
                    if (type.equals("province")){
                        result=HandleUtil.HandleProvinceResponse(responsetext);
                        Log.d("RESULT",responsetext);
                    }else if (type.equals("city")){
                        result=HandleUtil.HandleCityResponse(responsetext,selectedprovince.getProvinceid());
                    }else if (type.equals("county")){
                        result=HandleUtil.HandleCountyResponse(responsetext,selectedcity.getCityid());
                    }
                    if (result){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                if (type.equals("province")){
                                    queryprovinces();
                                }else if (type.equals("city")){
                                   querycityies();
                                }else if (type.equals("county")){
                                    querycounties();
                                }
                            }
                        });
                    }else {
                        Log.d("ERROE","unknown error");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
           }
       });

   }
    public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.ViewHolder> {
        private List<String> arealist;

        public class ViewHolder extends RecyclerView.ViewHolder{
            private View view;
            private TextView textView;
            public ViewHolder(View v){
                super(v);
                view=v;
                textView=v.findViewById(R.id.areaitem);
            }
        }
        public AreaAdapter(List<String> mlist){
            arealist=mlist;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.areaitem,parent,false);
           ViewHolder holder=new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String area = arealist.get(position);
            holder.textView.setText(area);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Level",current_level+"");
                    if (current_level==LEVEL_PROVINCE){
                        Log.d("Level","position"+holder.getBindingAdapterPosition());
                        selectedprovince=provincedbs.get(holder.getBindingAdapterPosition());
                        querycityies();
                    } else if (current_level==LEVEL_CITY){
                        Log.d("currentlevel",current_level+"");
                        selectedcity=citydbs.get(holder.getBindingAdapterPosition());
                        querycounties();
                    }else if (current_level==LEVEL_COUNTY){
                        Intent intent=new Intent(getActivity(),Weather_Activity.class);
                        String weatherid=countydbs.get(holder.getBindingAdapterPosition()).getWeatherid();
                        intent.putExtra("weatherid",weatherid);
                        startActivity(intent);
                        getActivity().finish();
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return arealist.size();
        }

    }

}


