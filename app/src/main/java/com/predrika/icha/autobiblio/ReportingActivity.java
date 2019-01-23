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

        Bundle tahun = getIntent().getExtras();
        String year= tahun.getString("tahunqu");

        Bundle nomor = getIntent().getExtras();
        int[] hahaha = nomor.getIntArray("numberqu");

        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("Library Reports (" +year + ")");
        getSupportActionBar().hide();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        BarChart barChart = findViewById(R.id.barchartOnGoing);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(hahaha[0], 0));
        entries.add(new BarEntry(hahaha[1], 1));
        entries.add(new BarEntry(hahaha[2], 2));
        entries.add(new BarEntry(hahaha[3], 3));
        entries.add(new BarEntry(hahaha[4], 4));
        entries.add(new BarEntry(hahaha[5], 5));
        entries.add(new BarEntry(hahaha[6], 6));
        entries.add(new BarEntry(hahaha[7], 7));
        entries.add(new BarEntry(hahaha[8], 8));
        entries.add(new BarEntry(hahaha[9], 9));
        entries.add(new BarEntry(hahaha[10], 10));
        entries.add(new BarEntry(hahaha[11], 11));

        BarDataSet bardataset = new BarDataSet(entries, "On-Going Loan");

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

        BarData data = new BarData(labels, bardataset);
        barChart.setData(data); // set the data and list of lables into chart

        barChart.setDescription("On-Going Loan Report in a year");  // set the description

        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);

        barChart.animateY(5000);

    }

}
