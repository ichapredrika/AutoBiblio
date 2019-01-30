package com.predrika.icha.autobiblio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ReportingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting);


        Bundle onGoing = getIntent().getExtras();
        int onGoingVal = onGoing.getInt("onGoingVal");


        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("Library Reports ( On-Going)");
        getSupportActionBar().hide();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( ReportingActivity.this, HistoryAdminActivity.class));
            }
        });

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("On-Going");


        //ongoing
        BarChart barChartOnGoing = findViewById(R.id.barchartOnGoing);

        ArrayList<BarEntry> entriesOnGoing = new ArrayList<>();
        entriesOnGoing.add(new BarEntry(onGoingVal, 0));

        BarDataSet bardatasetOnGoing = new BarDataSet(entriesOnGoing, "On-Going Loan");

        BarData dataOnGOing = new BarData(labels, bardatasetOnGoing);
        barChartOnGoing.setData(dataOnGOing); // set the data and list of lables into chart
        barChartOnGoing.setDescription("On-Going Loan Report in a year");  // set the description
        bardatasetOnGoing.setColors(ColorTemplate.COLORFUL_COLORS);
        barChartOnGoing.animateY(5000);

        TextView totalTV= findViewById(R.id.post_total);
        totalTV.setText(Integer.toString(onGoingVal));
    }
}
