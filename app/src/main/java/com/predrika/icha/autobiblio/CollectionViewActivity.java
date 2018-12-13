package com.predrika.icha.autobiblio;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class CollectionViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_view);


        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("Collection Detail");
        getSupportActionBar().hide();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                startActivity(new Intent( CollectionViewActivity.this, CollectionActivity.class));

            }
        });

        Intent intent = getIntent();
        String title = intent.getExtras().getString("title");

        TextView titleTV= findViewById(R.id.titleBanner);
        titleTV.setText(title);
    }

}
