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

public class FinesReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fines_report);

        Bundle years = getIntent().getExtras();
        String year= years.getString("year");

        Bundle fines = getIntent().getExtras();
        int[] finesArr = fines.getIntArray("finesArr");


        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("Library Fines Reports (" +year + ")");
        getSupportActionBar().hide();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( FinesReportActivity.this, HistoryAdminActivity.class));
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

        //Fines
        BarChart barChartFines = findViewById(R.id.barchartFines);

        ArrayList<BarEntry> entriesFines = new ArrayList<>();
        entriesFines.add(new BarEntry(finesArr[0], 0));
        entriesFines.add(new BarEntry(finesArr[1], 1));
        entriesFines.add(new BarEntry(finesArr[2], 2));
        entriesFines.add(new BarEntry(finesArr[3], 3));
        entriesFines.add(new BarEntry(finesArr[4], 4));
        entriesFines.add(new BarEntry(finesArr[5], 5));
        entriesFines.add(new BarEntry(finesArr[6], 6));
        entriesFines.add(new BarEntry(finesArr[7], 7));
        entriesFines.add(new BarEntry(finesArr[8], 8));
        entriesFines.add(new BarEntry(finesArr[9], 9));
        entriesFines.add(new BarEntry(finesArr[10], 10));
        entriesFines.add(new BarEntry(finesArr[11], 11));

        BarDataSet bardatasetFines = new BarDataSet(entriesFines, "On-Going Loan");

        BarData dataFines = new BarData(labels, bardatasetFines);
        barChartFines.setData(dataFines); // set the data and list of lables into chart
        barChartFines.setDescription("On-Going Loan Report in a year");  // set the description
        bardatasetFines.setColors(ColorTemplate.COLORFUL_COLORS);
        barChartFines.animateY(5000);
    }
}
