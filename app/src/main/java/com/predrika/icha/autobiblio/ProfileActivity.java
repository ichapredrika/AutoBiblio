package com.predrika.icha.autobiblio;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    // Creating Progress dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle("Profile");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_autobiblio);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser Users= FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef= mRef.child("Users/" + Users.getUid());

        //get value from database
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Users currentUser = dataSnapshot.getValue(Users.class);
                    TextView fullNameTV=findViewById(R.id.fullNameTxt);
                    fullNameTV.setText(currentUser.getFullName());

                    TextView univIdTV=findViewById(R.id.univIdTxt);
                    univIdTV.setText(currentUser.getunivId());

                    TextView pobTV=findViewById(R.id.pobTxt);
                    pobTV.setText(currentUser.getPob());

                    TextView dobTV=findViewById(R.id.dobTxt);
                    dobTV.setText(currentUser.getDob());

                    TextView addressTV=findViewById(R.id.addressTxt);
                    addressTV.setText(currentUser.getAddress());

                    TextView phoneTV=findViewById(R.id.phoneTxt);
                    phoneTV.setText(currentUser.getPhone());

                    TextView memberTypeTV=findViewById(R.id.memberTypeTxt);
                    memberTypeTV.setText(currentUser.getMemberType());

                    TextView balanceAmountTV=findViewById(R.id.balanceAmountTxt);
                    balanceAmountTV.setText(Double.toString(currentUser.getBalanceAmount()));

                    // Hiding the progress dialog.
                    progressDialog.dismiss();
                } else  {
                    // Hiding the progress dialog.
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
        });

        //Navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        startActivity(new Intent( ProfileActivity.this, MainActivity.class));
                        break;
                    case R.id.action_collection:
                        startActivity( new Intent( ProfileActivity.this, CollectionActivity.class));
                        break;
                    case R.id.action_scan:
                        startActivity(new Intent( ProfileActivity.this, ScannerActivity.class));
                        break;
                    case R.id.action_history:
                        startActivity(new Intent( ProfileActivity.this, HistoryActivity.class));
                        break;
                    case R.id.action_profile:
                        startActivity(new Intent( ProfileActivity.this, ProfileActivity.class));
                        break;
                }
                return false;
            }
        });
    }

    public void logoutClick(View view){
        try{
            mAuth.signOut();
            Toast.makeText(ProfileActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent( ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void profileSettingClick (View view){
        Intent intent = new Intent( ProfileActivity.this, ProfileSettingActivity.class);
        startActivity(intent);
    }

    public void reminderClick(View view){
        Intent intent = new Intent( ProfileActivity.this, ReminderActivity.class);
        startActivity(intent);
    }
}
