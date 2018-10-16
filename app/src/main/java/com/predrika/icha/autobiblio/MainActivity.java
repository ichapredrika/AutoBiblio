package com.predrika.icha.autobiblio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private String email;



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser Users= FirebaseAuth.getInstance().getCurrentUser();

        TextView welcomeTV= findViewById(R.id.welcomeText);
        welcomeTV.setText("Hello "+ Users.getEmail());

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
}
