package com.example.jobsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ObjectInputStream;

public class JobShowActivity extends AppCompatActivity {
    TextView JobName, JobDescription, JobFullPayment, JobAdress;
    Button ShowJobButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_show);
        ShowActivity.JobItem item = getIntent().getParcelableExtra("job");
        JobName = findViewById(R.id.fullJobName);
        JobDescription = findViewById(R.id.fullJobDescription);
        JobAdress = findViewById(R.id.FullJobAdress);
        JobFullPayment = findViewById(R.id.jobFullPayment);
        ShowJobButton = findViewById(R.id.ShowJobButton);


        JobName.setText(item.getName());
        JobAdress.setText(item.getCity());
        JobFullPayment.setText(item.getPaymentFrom() + " - " + item.getPaymentTo());
        JobDescription.setText(Html.fromHtml(item.getDescription()));

        ShowJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectInputStream.GetField jobsItems;
                Intent openPage= new Intent(Intent.ACTION_VIEW, Uri.parse(item.url));
                startActivity(openPage);
            }
        });

    }
}