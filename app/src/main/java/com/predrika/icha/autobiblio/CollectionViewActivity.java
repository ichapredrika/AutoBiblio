package com.predrika.icha.autobiblio;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class CollectionViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_view);


        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("Collection Detail");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                startActivity(new Intent( CollectionViewActivity.this, CollectionActivity.class));
            }
        });

        getSupportActionBar().hide();
    }

}
