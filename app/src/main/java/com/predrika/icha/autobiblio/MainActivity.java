package com.predrika.icha.autobiblio;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_autobiblio);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser Users= FirebaseAuth.getInstance().getCurrentUser();

        TextView welcomeTV= findViewById(R.id.welcomeText);
        welcomeTV.setText("Hello, "+ Users.getEmail());

        //Navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        startActivity(new Intent( MainActivity.this, MainActivity.class));
                        break;
                    case R.id.action_collection:
                        startActivity(new Intent( MainActivity.this, CollectionActivity.class));
                        break;
                    case R.id.action_scan:
                        startActivity( new Intent( MainActivity.this, ScannerActivity.class));
                        break;
                    case R.id.action_history:
                        startActivity(new Intent( MainActivity.this, HistoryActivity.class));
                        break;
                    case R.id.action_profile:
                        startActivity(new Intent( MainActivity.this, ProfileActivity.class));
                        break;
                }
                return false;
            }
        });
    }

}

