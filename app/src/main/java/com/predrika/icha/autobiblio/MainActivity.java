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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private String email;



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser Users= FirebaseAuth.getInstance().getCurrentUser();

        TextView welcomeTV= findViewById(R.id.welcomeText);
        welcomeTV.setText("Hello, "+ Users.getEmail());

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef= mRef.child("Users/" + Users.getUid());

        //get value from database
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users currentUser = dataSnapshot.getValue(Users.class);
                TextView fullNameTV=findViewById(R.id.fullNameTxt);
                fullNameTV.setText(currentUser.getFullName());

                TextView idCardTV=findViewById(R.id.idCardTxt);
                idCardTV.setText(currentUser.getIdCard());

                TextView pobTV=findViewById(R.id.pobTxt);
                pobTV.setText(currentUser.getPob());

                TextView dobTV=findViewById(R.id.dobTxt);
                dobTV.setText(currentUser.getDob());

                TextView addressTV=findViewById(R.id.addressTxt);
                addressTV.setText(currentUser.getAddress());

                TextView phoneTV=findViewById(R.id.phoneTxt);
                phoneTV.setText(currentUser.getPhone());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    public void profileSettingClick (View view){
        Intent intent = new Intent( MainActivity.this, ProfileSettingActivity.class);
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
}
