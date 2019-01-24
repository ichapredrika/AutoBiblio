package com.predrika.icha.autobiblio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

        Bundle years = getIntent().getExtras();
        String year= years.getString("year");

        Bundle onGoing = getIntent().getExtras();
        int[] onGoingArr = onGoing.getIntArray("onGoingArr");


        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("Library Reports (" +year + ")");
        getSupportActionBar().hide();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( ReportingActivity.this, HistoryAdminActivity.class));
            }
        });

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Jan");
        labels.add("Feb");
        labels.add("Mar");
        labels.add("Apr");
        labels.add("May");
        labels.add("Jun");
        labels.add("Jul");
        labels.add("Aug");
        labels.add("Sep");
        labels.add("Oct");
        labels.add("Nov");
        labels.add("Dec");

        //ongoing
        BarChart barChartOnGoing = findViewById(R.id.barchartOnGoing);

        ArrayList<BarEntry> entriesOnGoing = new ArrayList<>();
        entriesOnGoing.add(new BarEntry(onGoingArr[0], 0));
        entriesOnGoing.add(new BarEntry(onGoingArr[1], 1));
        entriesOnGoing.add(new BarEntry(onGoingArr[2], 2));
        entriesOnGoing.add(new BarEntry(onGoingArr[3], 3));
        entriesOnGoing.add(new BarEntry(onGoingArr[4], 4));
        entriesOnGoing.add(new BarEntry(onGoingArr[5], 5));
        entriesOnGoing.add(new BarEntry(onGoingArr[6], 6));
        entriesOnGoing.add(new BarEntry(onGoingArr[7], 7));
        entriesOnGoing.add(new BarEntry(onGoingArr[8], 8));
        entriesOnGoing.add(new BarEntry(onGoingArr[9], 9));
        entriesOnGoing.add(new BarEntry(onGoingArr[10], 10));
        entriesOnGoing.add(new BarEntry(onGoingArr[11], 11));

        BarDataSet bardatasetOnGoing = new BarDataSet(entriesOnGoing, "On-Going Loan");

        BarData dataOnGOing = new BarData(labels, bardatasetOnGoing);
        barChartOnGoing.setData(dataOnGOing); // set the data and list of lables into chart
        barChartOnGoing.setDescription("On-Going Loan Report in a year");  // set the description
        bardatasetOnGoing.setColors(ColorTemplate.COLORFUL_COLORS);
        barChartOnGoing.animateY(5000);
    }
}
