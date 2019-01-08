package com.predrika.icha.autobiblio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileSettingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    // Creating Progress dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("Profile Setting");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        getSupportActionBar().hide();

        mAuth= FirebaseAuth.getInstance();
        FirebaseUser Users= FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef= mRef.child("Users/" + Users.getUid());

        //get value from database
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users currentUser = dataSnapshot.getValue(Users.class);

                TextView fullNameTV=findViewById(R.id.fullNameEditText);
                fullNameTV.setText(currentUser.getFullName());

                TextView univIdTV=findViewById(R.id.univIdEditText);
                univIdTV.setText(currentUser.getunivId());

                TextView pobTV=findViewById(R.id.pobEditText);
                pobTV.setText(currentUser.getPob());

                TextView dobTV=findViewById(R.id.dobEditText);
                dobTV.setText(currentUser.getDob());

                TextView addressTV=findViewById(R.id.addressEditText);
                addressTV.setText(currentUser.getAddress());

                TextView phoneTV=findViewById(R.id.phoneEditText);
                phoneTV.setText(currentUser.getPhone());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void submitClick(View view) {
        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(ProfileSettingActivity.this);
        progressDialog.setMessage("Submitting data into database...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        TextView fullNameTV=findViewById(R.id.fullNameEditText);
        TextView univIdTV=findViewById(R.id.univIdEditText);
        TextView pobTV=findViewById(R.id.pobEditText);
        TextView dobTV=findViewById(R.id.dobEditText);
        TextView addressTV=findViewById(R.id.addressEditText);
        TextView phoneTV=findViewById(R.id.phoneEditText);

        DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = mRef.child("Users/" + mAuth.getCurrentUser().getUid());
        usersRef.child("fullName").setValue(fullNameTV.getText().toString());
        usersRef.child("univId").setValue(univIdTV.getText().toString());
        usersRef.child("pob").setValue(pobTV.getText().toString());
        usersRef.child("dob").setValue(dobTV.getText().toString());
        usersRef.child("address").setValue(addressTV.getText().toString());
        usersRef.child("phone").setValue(phoneTV.getText().toString());

        Intent intent = new Intent( ProfileSettingActivity.this, ProfileActivity.class);
        startActivity(intent);

        // Hiding the progress dialog.
        progressDialog.dismiss();
    }
}
