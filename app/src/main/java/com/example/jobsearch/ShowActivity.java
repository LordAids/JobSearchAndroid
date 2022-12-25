package com.example.jobsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity {

    private ListView jobList;
    private ArrayList<JobItem> jobsItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        String resSJ = getIntent().getStringExtra("resSJ");
        String resHH = getIntent().getStringExtra("resHH");

        jobList = findViewById(R.id.jobList);
        JSONArray jobs = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject(resSJ);
            jobs = jsonObject.getJSONArray("objects");
            jobsItems = new ArrayList<JobItem>();
            for (int i = 0; i < jobs.length(); i++) {
                JSONObject json_data = jobs.getJSONObject(i);
                String name = json_data.getString("profession");
                String city = json_data.getString("address");
                if(city.equals("null"))
                    city = "";
                String payment_from = "\tот " + json_data.getString("payment_from");
                if( json_data.getString("payment_from") == "0")
                    payment_from = "";
                String payment_to = "  до  " + json_data.getString("payment_to");
                if( json_data.getString("payment_to") == "0")
                    payment_to = "";
                Integer id = json_data.getInt("id");
                String url = json_data.getString("link");
                String desc = json_data.getString("vacancyRichText");
                jobsItems.add(new JobItem(id, name, city, payment_from, payment_to, url, "SUPERJOB", desc));
            }

                JSONObject jobsHHJSON = new JSONObject(resHH);
                JSONArray jobsHH = jobsHHJSON.getJSONArray("items");
                for(int i = 0; i < jobsHH.length(); i++){
                    JSONObject json_data = jobsHH.getJSONObject(i);
                    String name = json_data.getString("name");
                    JSONObject adress;
                    String city = "";
                    if(json_data.optJSONObject("address")!=null){
                        adress = json_data.getJSONObject("address");
                        city = adress.getString("raw");
                        if(city.equals(null))
                            city = "";
                    }
                    String payment_from = "";
                    String payment_to = "";

                    if(json_data.optJSONObject("salary")!=null){
                        JSONObject salary = json_data.getJSONObject("salary");
                        if(!salary.getString("from").equals(null)){
                            payment_from = "\tот " + salary.getString("from");
                        }
                        if(!salary.getString("to").equals(null)){
                            payment_to = "  до  " + salary.getString("to");
                        }
                    }
                    JSONObject description = json_data.getJSONObject("snippet");
                    String desc = description.getString("responsibility");
                    Integer id = json_data.getInt("id");
                    String url = json_data.getString("url");
                    jobsItems.add(new JobItem(id, name, city, payment_from, payment_to, url, "HEADHUNTER", desc));
                }






        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            JobAdapter adapter = new JobAdapter(this, R.layout.job_item, jobsItems);
            jobList.setAdapter(adapter);
            jobList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent openPage= new Intent(Intent.ACTION_VIEW, Uri.parse(jobsItems.get(position).url));
//                    startActivity(openPage);
                    ShowJob(jobsItems.get(position));
                }
            });
        }
        JSONArray jobsHh = new JSONArray();
        //jobStr.setText(res);
    }

    private void ShowJob(JobItem jobItem){
        Intent i = new Intent(this, JobShowActivity.class);
        i.putExtra("job", jobItem);
        startActivity(i);
    }

    public static class JobItem implements Parcelable {
        JobItem(Integer id, String name, String city, String payment_from, String payment_to, String url, String type, String description) {
            this.id = id;
            this.city = city;
            this.name = name;
            this.payment_from = payment_from;
            this.payment_to = payment_to;
            this.url = url;
            this.type = type;
            this.description = description;
        }

        public Integer id;
        public String name;
        public String city;
        public String payment_from;
        public String payment_to;
        public String url;
        public String type;
        public String description;

        protected JobItem(Parcel in) {
            if (in.readByte() == 0) {
                id = null;
            } else {
                id = in.readInt();
            }
            name = in.readString();
            city = in.readString();
            payment_from = in.readString();
            payment_to = in.readString();
            url = in.readString();
            type = in.readString();
            description = in.readString();
        }

        public static final Creator<JobItem> CREATOR = new Creator<JobItem>() {
            @Override
            public JobItem createFromParcel(Parcel in) {
                return new JobItem(in);
            }

            @Override
            public JobItem[] newArray(int size) {
                return new JobItem[size];
            }
        };

        public String getName(){
            return this.name;
        }
        public String getCity(){
            return this.city;
        }
        public String getPaymentFrom(){
            return this.payment_from;
        }
        public String getPaymentTo(){
            return this.payment_to;
        }
        public String getType() {return  this.type;}
        public String getDescription() {return  this.description;}

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (id == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(id);
            }
            dest.writeString(name);
            dest.writeString(city);
            dest.writeString(payment_from);
            dest.writeString(payment_to);
            dest.writeString(url);
            dest.writeString(type);
            dest.writeString(description);
        }
    }


    public class JobAdapter extends ArrayAdapter<JobItem> {

        private LayoutInflater inflater;
        private int layout;
        private List<JobItem> jobs;

        public JobAdapter(Context context, int resource, List<JobItem> jobs) {
            super(context, resource, jobs);
            this.jobs = jobs;
            this.layout = resource;
            this.inflater = LayoutInflater.from(context);
        }
        public View getView(int position, View convertView, ViewGroup parent) {

            View view=inflater.inflate(this.layout, parent, false);

            TextView nameView = view.findViewById(R.id.jobName);
            TextView cityView = view.findViewById(R.id.jobCity);
            TextView payment_from = view.findViewById(R.id.jobPaymentFrom);
            TextView payment_to = view.findViewById(R.id.jobPaymentTo);
            TextView type = view.findViewById(R.id.type);

            JobItem job = jobs.get(position);

            nameView.setText(job.getName());
            cityView.setText(job.getCity());
            payment_from.setText(job.getPaymentFrom());
            payment_to.setText(job.getPaymentTo());
            type.setText(job.getType());


            return view;
        }
    }
}
