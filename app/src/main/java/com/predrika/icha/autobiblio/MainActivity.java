package com.predrika.icha.autobiblio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser Users= FirebaseAuth.getInstance().getCurrentUser();

        TextView welcomeTV= findViewById(R.id.welcomeText);
        welcomeTV.setText("Hello, "+ Users.getEmail());
    }

    public void logoutClick(View view){
        try{
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent( MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void profileClick (View view){
        Intent intent = new Intent( MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    public void scanClick (View view){
        Intent intent = new Intent( MainActivity.this, Scanner.class);
        startActivity(intent);
    }

    public void generateClick (View view){
        Intent intent = new Intent( MainActivity.this, GeneratorActivity.class);
        startActivity(intent);
    }

    public void reminderClick(View view){
        Intent intent = new Intent( MainActivity.this, ReminderActivity.class);
        startActivity(intent);
    }
}
