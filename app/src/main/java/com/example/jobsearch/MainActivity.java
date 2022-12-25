package com.example.jobsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewResult;
    private EditText cityField, nameField;
    private Button button;
    private ArrayList<City> cityes;
    String resHH = "";
    String resSJ = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.buttonSearch);
        cityField = findViewById(R.id.nameCity);
        nameField = findViewById(R.id.nameField);
        cityes = TakeHHCityes();



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = cityField.getText().toString();
                String jobName = nameField.getText().toString();


                OkHttpClient client  = new OkHttpClient();
                String url = "https://api.superjob.ru/2.0/vacancies/?not_archive=true&keyword="+jobName+"&town=" + cityName;

                Request request = new  Request.Builder()
                        .url(url)
                        .header("X-Api-App-Id", "InsertYourKey")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){
                            resSJ = response.body().string();
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ShowJobs();
                                }
                            });
                        }
                    }
                });


                String areaId ="";
                for (City city: cityes) {
                    if(city.name.toUpperCase().equals(cityName.toUpperCase())) {
                        areaId = String.valueOf(city.Id);
                        break;
                    }
                }
                url = "https://api.hh.ru/vacancies?text="+jobName+"developer&area="+ areaId;
                request = new  Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){
                            resHH  = response.body().string();
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ShowJobs();
                                }
                            });
                        }
                    }
                });
            }
        });


    }

    protected void ShowJobs(){
        if(resHH != "" && resSJ != ""){
            Intent intent = new Intent(this, ShowActivity.class);
            intent.putExtra("resSJ", resSJ);
            intent.putExtra("resHH", resHH);
            startActivity(intent);
        }
    }

    protected ArrayList<City> TakeHHCityes(){
        ArrayList<City> cityList = new ArrayList<City>();
        OkHttpClient client  = new OkHttpClient();
        String url = "https://api.hh.ru/areas";

        Request request = new  Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String res  = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONArray jsonObject = null;
                            try {
                                jsonObject = new JSONArray(res);
                                JSONObject Rus = jsonObject.getJSONObject(0);
                                JSONArray RusAreas = Rus.getJSONArray("areas");
                                for(int i = 0; i < RusAreas.length(); i++){
                                    JSONObject area = RusAreas.getJSONObject(i);
                                    JSONArray cityes = area.getJSONArray("areas");
                                    for(int j = 0; j < cityes.length(); j++){
                                        JSONObject city = cityes.getJSONObject(j);
                                        String name = city.getString("name");
                                        Integer id = city.getInt("id");
                                        cityList.add(new City(id,name));
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
        url +="?locale=EN";
        request = new  Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String res  = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONArray jsonObject = null;
                            try {
                                jsonObject = new JSONArray(res);
                                JSONObject Rus = jsonObject.getJSONObject(0);
                                JSONArray RusAreas = Rus.getJSONArray("areas");
                                for(int i = 0; i < RusAreas.length(); i++){
                                    JSONObject area = RusAreas.getJSONObject(i);
                                    JSONArray cityes = area.getJSONArray("areas");
                                    for(int j = 0; j < cityes.length(); j++){
                                        JSONObject city = cityes.getJSONObject(j);
                                        String name = city.getString("name");
                                        Integer id = city.getInt("id");
                                        cityList.add(new City(id,name));
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });


        return cityList;
    }

    public class City{
        public  City(Integer id, String name){
            this.name = name;
            this.Id = id;
        }
        String name;
        Integer Id;
    }

}