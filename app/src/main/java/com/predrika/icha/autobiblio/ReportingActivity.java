package com.predrika.icha.autobiblio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        BarChart barChart = findViewById(R.id.barchartOnGoing);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(8, 0));
        entries.add(new BarEntry(2, 1));
        entries.add(new BarEntry(5, 2));
        entries.add(new BarEntry(20, 3));
        entries.add(new BarEntry(15, 4));
        entries.add(new BarEntry(19, 5));
        entries.add(new BarEntry(19, 6));
        entries.add(new BarEntry(8, 7));
        entries.add(new BarEntry(5, 8));
        entries.add(new BarEntry(0, 9));
        entries.add(new BarEntry(0, 10));
        entries.add(new BarEntry(0, 11));

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
