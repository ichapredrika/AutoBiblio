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

public class CompleteReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_report);

        Bundle years = getIntent().getExtras();
        String year= years.getString("year");

        Bundle complete = getIntent().getExtras();
        int[] completeArr = complete.getIntArray("completeArr");

        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("Library Completed Reports (" +year + ")");
        getSupportActionBar().hide();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( CompleteReportActivity.this, HistoryAdminActivity.class));
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

        //Complete
        BarChart barChartComplete = findViewById(R.id.barchartComplete);

        ArrayList<BarEntry> entriesComplete = new ArrayList<>();
        entriesComplete.add(new BarEntry(completeArr[0], 0));
        entriesComplete.add(new BarEntry(completeArr[1], 1));
        entriesComplete.add(new BarEntry(completeArr[2], 2));
        entriesComplete.add(new BarEntry(completeArr[3], 3));
        entriesComplete.add(new BarEntry(completeArr[4], 4));
        entriesComplete.add(new BarEntry(completeArr[5], 5));
        entriesComplete.add(new BarEntry(completeArr[6], 6));
        entriesComplete.add(new BarEntry(completeArr[7], 7));
        entriesComplete.add(new BarEntry(completeArr[8], 8));
        entriesComplete.add(new BarEntry(completeArr[9], 9));
        entriesComplete.add(new BarEntry(completeArr[10], 10));
        entriesComplete.add(new BarEntry(completeArr[11], 11));

        BarDataSet bardatasetComplete = new BarDataSet(entriesComplete, "Completed Loan");

        BarData dataComplete = new BarData(labels, bardatasetComplete);
        barChartComplete.setData(dataComplete); // set the data and list of lables into chart
        barChartComplete.setDescription("Completed Loan Report in a year");  // set the description
        bardatasetComplete.setColors(ColorTemplate.COLORFUL_COLORS);
        barChartComplete.animateY(2000);

        int total=0;
        for(int i=0;i<=completeArr.length-1;i++){
            total+=completeArr[i];
        }
        TextView totalTV= findViewById(R.id.post_total);
        totalTV.setText(Integer.toString(total));
    }
}
