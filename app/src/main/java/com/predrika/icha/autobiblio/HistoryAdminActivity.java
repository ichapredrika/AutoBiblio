package com.predrika.icha.autobiblio;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

public class HistoryAdminActivity extends AppCompatActivity {

    private RecyclerView mOnGoingRV;
    private RecyclerView mFinesRV;
    private RecyclerView mCompleteRV;

    private FirebaseAuth mAuth;

    private DatabaseReference mOnGoingDatabase;
    private DatabaseReference mFinesDatabase;
    private DatabaseReference mCompleteDatabase;

    //private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<OnGoing, HistoryAdminActivity.HistoryViewHolder> mOnGoingRVAdapter;
    private FirebaseRecyclerAdapter<Fines, HistoryAdminActivity.FinesViewHolder> mFinesRVAdapter;
    private FirebaseRecyclerAdapter<Complete, HistoryAdminActivity.CompleteViewHolder> mCompleteRVAdapter;

    ProgressDialog progressDialog;

    private String year;
    private int onGoingVal;
    private int[] finesArr = new int[12];
    private int[] completeArr = new int[12];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_admin);

        setTitle("History");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_autobiblio);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //Navigation bar
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_history);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        startActivity(new Intent( HistoryAdminActivity.this, MainActivity.class));
                        break;
                    case R.id.action_collection:
                        startActivity(new Intent( HistoryAdminActivity.this, CollectionActivity.class));
                        break;
                    case R.id.action_scan:
                        startActivity(new Intent( HistoryAdminActivity.this, ScannerActivity.class));
                        break;
                    case R.id.action_history:
                        startActivity(new Intent( HistoryAdminActivity.this, HistoryAdminActivity.class));
                        break;
                    case R.id.action_profile:
                        startActivity(new Intent( HistoryAdminActivity.this, ProfileActivity.class));
                        break;
                }
                return false;
            }
        });

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(HistoryAdminActivity.this);
        progressDialog.setMessage("Loading history...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        mAuth= FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        mOnGoingDatabase = FirebaseDatabase.getInstance().getReference().child("OnGoing");
        mOnGoingDatabase.keepSynced(true);

        mFinesDatabase = FirebaseDatabase.getInstance().getReference().child("Fines");
        mFinesDatabase.keepSynced(true);

        mCompleteDatabase = FirebaseDatabase.getInstance().getReference().child("Complete");
        mCompleteDatabase.keepSynced(true);

        mOnGoingRV = findViewById(R.id.onGoingRecycleView);
        mFinesRV = findViewById(R.id.finesRecycleView);
        mCompleteRV = findViewById(R.id.completeRecycleView);

        DatabaseReference onGoingRef = FirebaseDatabase.getInstance().getReference().child("OnGoing");
        Query onGoingQuery = onGoingRef.orderByKey();

        DatabaseReference finesRef = FirebaseDatabase.getInstance().getReference().child("Fines");
        Query finesQuery = finesRef.orderByKey();

        DatabaseReference completeRef = FirebaseDatabase.getInstance().getReference().child("Complete");
        Query completeQuery = completeRef.orderByKey();

        mOnGoingRV.hasFixedSize();
        mFinesRV.hasFixedSize();
        mCompleteRV.hasFixedSize();

        mOnGoingRV.setLayoutManager(new LinearLayoutManager(this));
        mFinesRV.setLayoutManager(new LinearLayoutManager(this));
        mCompleteRV.setLayoutManager(new LinearLayoutManager(this));


        //on going
        FirebaseRecyclerOptions onGoingOptions = new FirebaseRecyclerOptions.Builder<OnGoing>().setQuery(onGoingQuery, OnGoing.class).build();

        //Adapter>> connect database into view
        mOnGoingRVAdapter = new FirebaseRecyclerAdapter<OnGoing, HistoryAdminActivity.HistoryViewHolder>(onGoingOptions) {
            @Override
            protected void onBindViewHolder(HistoryAdminActivity.HistoryViewHolder holder, final int position, final OnGoing model) {
                holder.setTitle(model.getTitle());
                holder.setBookIdOnGoing(model.getBookIdOnGoing());
                holder.setIssuedDate(model.getIssuedDate());
                holder.setMaxReturnDate(model.getMaxReturnDate());
            }
            //viewholder>> listview
            @Override
            public HistoryAdminActivity.HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.on_going_row, parent, false);
                return new HistoryAdminActivity.HistoryViewHolder(view);
            }
        };
        mOnGoingRV.setAdapter(mOnGoingRVAdapter);
        //add the listener for the single value event that will function
        mOnGoingDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });


        //fines
        FirebaseRecyclerOptions finesOptions = new FirebaseRecyclerOptions.Builder<Fines>().setQuery(finesQuery, Fines.class).build();

        //Adapter>> connect database into view
        mFinesRVAdapter = new FirebaseRecyclerAdapter<Fines, HistoryAdminActivity.FinesViewHolder>(finesOptions) {
            @Override
            protected void onBindViewHolder(HistoryAdminActivity.FinesViewHolder holder, final int position, final Fines model) {
                holder.setTitleFines(model.getTitleFines());
                holder.setBookIdFines(model.getBookIdFines());
                holder.setDamageCost(model.getDamageCost());
                holder.setOverdueCost(model.getOverdueCost());
                holder.setTotalCost(model.getTotalCost());
                holder.setPaidAmount(model.getPaidAmount());
                holder.setPaidOff(model.getPaidOff());
                holder.setRemainingCost(model.getTotalCost()-model.getPaidAmount());
            }
            //viewholder>> listview
            @Override
            public HistoryAdminActivity.FinesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fines_row, parent, false);
                //view.setVisibility(View.GONE);
                return new HistoryAdminActivity.FinesViewHolder(view);
            }
        };
        mFinesRV.setAdapter(mFinesRVAdapter);

        //complete
        final FirebaseRecyclerOptions completeOptions = new FirebaseRecyclerOptions.Builder<Complete>().setQuery(completeQuery, Complete.class).build();

        //Adapter>> connect database into view
        mCompleteRVAdapter = new FirebaseRecyclerAdapter<Complete, HistoryAdminActivity.CompleteViewHolder>(completeOptions) {
            @Override
            protected void onBindViewHolder(HistoryAdminActivity.CompleteViewHolder holder, final int position, final Complete model) {
                holder.setTitleComplete(model.getTitleComplete());
                holder.setBookIdComplete(model.getBookIdComplete());
                holder.setIssuedDateComplete(model.getIssuedDateComplete());
                holder.setReturnedDate(model.getReturnedDate());
                holder.setDamagedYN(model.getDamagedYN());
                holder.setOverdueYN(model.getOverdueYN());
                holder.setPaidOffYN(model.getPaidOffYN());
            }
            //viewholder>> listview
            @Override
            public HistoryAdminActivity.CompleteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.complete_row, parent, false);
                return new HistoryAdminActivity.CompleteViewHolder(view);
            }
        };
        mCompleteRV.setAdapter(mCompleteRVAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mOnGoingRVAdapter.startListening();
        mFinesRVAdapter.startListening();
        mCompleteRVAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mOnGoingRVAdapter.stopListening();
        mFinesRVAdapter.stopListening();
        mCompleteRVAdapter.stopListening();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public HistoryViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        public void setTitle(String title){
            TextView post_title = mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }
        public void setBookIdOnGoing(String bookIdOnGoing){
            TextView post_bookIdOnGoing = mView.findViewById(R.id.post_bookIdOnGoing);
            post_bookIdOnGoing.setText(bookIdOnGoing);
        }
        public void setIssuedDate(String issuedDate){
            TextView post_issuedDate = mView.findViewById(R.id.post_issuedDate);
            post_issuedDate.setText(issuedDate);
        }
        public void setMaxReturnDate( String maxReturnDate){
            TextView post_maxReturnDate = mView.findViewById(R.id.post_maxReturnDate);
            post_maxReturnDate.setText(maxReturnDate);
        }
    }

    public static class FinesViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public FinesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setTitleFines(String titleFines){
            TextView post_titleFines = mView.findViewById(R.id.post_titleFines);
            post_titleFines.setText(titleFines);
        }
        public void setBookIdFines(String bookIdFines){
            TextView post_bookIdFines = mView.findViewById(R.id.post_bookIdFines);
            post_bookIdFines.setText(bookIdFines);
        }
        public void setDamageCost(double damageCost){
            TextView post_damageCost = mView.findViewById(R.id.post_damageCost);
            post_damageCost.setText(Double.toString(damageCost));
        }
        public void setOverdueCost(double overdueCost){
            TextView post_overdueCost = mView.findViewById(R.id.post_overdueCost);
            post_overdueCost.setText(Double.toString(overdueCost));
        }
        public void setTotalCost(double totalCost){
            TextView post_totalCost = mView.findViewById(R.id.post_totalCost);
            post_totalCost.setText(Double.toString(totalCost));
        }
        public void setPaidAmount(double paidAmount){
            TextView post_paidAmount = mView.findViewById(R.id.post_paidAmount);
            post_paidAmount.setText(Double.toString(paidAmount));
        }
        public void setPaidOff(String paidOff){
            TextView post_paidOff = mView.findViewById(R.id.post_paidOff);
            post_paidOff.setText(paidOff);
            if (paidOff.equals("NOT PAID")){
                post_paidOff.setTextColor(Color.parseColor("#7f0000"));
            }else{
                post_paidOff.setTextColor(Color.parseColor("#64ffda"));
            }
        }
        public void setRemainingCost(double remainingCost){
            TextView post_remainingCost = mView.findViewById(R.id.post_remainingCost);
            post_remainingCost.setText(Double.toString(remainingCost));
        }
    }

    public static class CompleteViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public CompleteViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setTitleComplete(String titleComplete){
            TextView post_titleComplete = mView.findViewById(R.id.post_titleComplete);
            post_titleComplete.setText(titleComplete);
        }
        public void setBookIdComplete(String bookIdComplete){
            TextView post_bookIdComplete = mView.findViewById(R.id.post_bookIdComplete);
            post_bookIdComplete.setText(bookIdComplete);
        }
        public void setIssuedDateComplete(String issuedDateComplete){
            TextView post_issuedDateComplete = mView.findViewById(R.id.post_issuedDateComplete);
            post_issuedDateComplete.setText(issuedDateComplete);
        }
        public void setReturnedDate(String returnedDate){
            TextView post_returnedDate = mView.findViewById(R.id.post_returnedDate);
            post_returnedDate.setText(returnedDate);
        }
        public void setDamagedYN(String damagedYN){
            TextView post_damagedYN = mView.findViewById(R.id.post_damagedYN);
            post_damagedYN.setText(damagedYN);
        }
        public void setOverdueYN(String overdueYN){
            TextView post_overdueYN = mView.findViewById(R.id.post_overdueYN);
            post_overdueYN.setText(overdueYN);
        }
        public void setPaidOffYN(String paidOffYN){
            TextView post_paidOffYN = mView.findViewById(R.id.post_paidOffYN);
            post_paidOffYN.setText(paidOffYN);
            if (paidOffYN.equals("NOT PAID")){
                post_paidOffYN.setTextColor(Color.parseColor("#7f0000"));
            }else {
                post_paidOffYN.setTextColor(Color.parseColor("#64ffda"));
            }
        }
    }

    public void onGoingClick(View view) {
        progressDialog = new ProgressDialog(HistoryAdminActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        TextView yearTV= findViewById(R.id.searchYearTxt);
        final String year= yearTV.getText().toString();


        //ongoing
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("OnGoing");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check onGoing
                if(dataSnapshot.exists()){
                    for(DataSnapshot data: dataSnapshot.getChildren()) {
                        Log.d("OnGoing existence", data.toString());
                        OnGoing onGoing = data.getValue(OnGoing.class);

                        onGoingVal+=1;
                        System.out.println(onGoingVal);
                        Log.d("OnGoing", data.toString()) ;
                    }
                    progressDialog.dismiss();
                    Intent i = new Intent(HistoryAdminActivity.this, ReportingActivity.class);
                    i.putExtra("onGoingVal", onGoingVal);
                    startActivity(i);
                } else {
                    progressDialog.dismiss();
                    Toast toast = Toast.makeText(getApplicationContext(), "The On-Going report data is not exist " , Toast.LENGTH_SHORT); toast.show();
                    Log.d("OnGoing", dataSnapshot.toString()) ;
                    Intent i = new Intent(HistoryAdminActivity.this, ReportingActivity.class);
                    i.putExtra("onGoingVal", onGoingVal);
                    startActivity(i);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)  {
                Toast toast = Toast.makeText(getApplicationContext(), "Error on retrieveing data " , Toast.LENGTH_SHORT); toast.show();
                progressDialog.dismiss();
            }
        });
    }

    public void finesClick(View view) {
        progressDialog = new ProgressDialog(HistoryAdminActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        TextView yearTV= findViewById(R.id.searchYearTxt);
        final String year= yearTV.getText().toString();

        if(!year.isEmpty()){
            //fines
            DatabaseReference finesRef = FirebaseDatabase.getInstance().getReference().child("Fines");
            finesRef.orderByChild("year").equalTo(year).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //check fines
                    if(dataSnapshot.exists()){
                        for(DataSnapshot data: dataSnapshot.getChildren()) {
                            Log.d("Fines existence", data.toString());
                            Fines fines = data.getValue(Fines.class);
                            fines.getYear();

                            DateTime dt = new DateTime(fines.getPaymentDate());
                            int month = dt.getMonthOfYear();

                            switch (month) {
                                case 1:  finesArr[0] +=fines.getPaidAmount();
                                    break;
                                case 2:  finesArr[1] +=fines.getPaidAmount();
                                    break;
                                case 3:  finesArr[2] +=fines.getPaidAmount();
                                    break;
                                case 4:  finesArr[3] +=fines.getPaidAmount();
                                    break;
                                case 5:  finesArr[4] +=fines.getPaidAmount();
                                    break;
                                case 6:  finesArr[5] +=fines.getPaidAmount();
                                    break;
                                case 7:  finesArr[6] +=fines.getPaidAmount();
                                    break;
                                case 8:  finesArr[7] +=fines.getPaidAmount();
                                    break;
                                case 9:  finesArr[8] +=fines.getPaidAmount();
                                    break;
                                case 10: finesArr[9] +=fines.getPaidAmount();
                                    break;
                                case 11: finesArr[10] +=fines.getPaidAmount();
                                    break;
                                case 12: finesArr[11] +=fines.getPaidAmount();
                                    break;
                                default: finesArr[12] +=fines.getPaidAmount();
                                    break;
                            }
                            System.out.println(finesArr);
                            Log.d("Fines", data.toString()) ;
                        }

                        progressDialog.dismiss();
                            Intent i = new Intent(HistoryAdminActivity.this, FinesReportActivity.class);
                        i.putExtra("finesArr", finesArr);
                        i.putExtra("year", year);
                        startActivity(i);
                    } else {
                        progressDialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "The Fines report data is not exist " , Toast.LENGTH_SHORT); toast.show();
                        Log.d("Fines", dataSnapshot.toString()) ;
                        Intent i = new Intent(HistoryAdminActivity.this, FinesReportActivity.class);
                        i.putExtra("finesArr", finesArr);
                        i.putExtra("year", year);
                        startActivity(i);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError)  {
                    Toast toast = Toast.makeText(getApplicationContext(), "Error on retrieveing data " , Toast.LENGTH_SHORT); toast.show();
                    progressDialog.dismiss();
                }
            });
        }else{
            progressDialog.dismiss();
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Search term can't be empty! Please input the year");
            alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //write your code here to execute after dialog closed
                        }
                    });
            alertDialog.show();
        }
    }

    public void completeClick(View view) {
        progressDialog = new ProgressDialog(HistoryAdminActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        TextView yearTV= findViewById(R.id.searchYearTxt);
        final String year= yearTV.getText().toString();

        if(!year.isEmpty()){
            //complete
            DatabaseReference finesRef = FirebaseDatabase.getInstance().getReference().child("Complete");
            finesRef.orderByChild("year").equalTo(year).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //check complete
                    if(dataSnapshot.exists()){

                        for(DataSnapshot data: dataSnapshot.getChildren()) {
                            Log.d("Complete existence", data.toString());
                            Complete complete = data.getValue(Complete.class);
                            complete.getYear();

                            DateTime dt = new DateTime(complete.getReturnedDate());
                            int month = dt.getMonthOfYear();

                            switch (month) {
                                case 1:  completeArr[0] +=1;
                                    break;
                                case 2:  completeArr[1] +=1;
                                    break;
                                case 3:  completeArr[2] +=1;
                                    break;
                                case 4:  completeArr[3] +=1;
                                    break;
                                case 5:  completeArr[4] +=1;
                                    break;
                                case 6:  completeArr[5] +=1;
                                    break;
                                case 7:  completeArr[6] +=1;
                                    break;
                                case 8:  completeArr[7] +=1;
                                    break;
                                case 9:  completeArr[8] +=1;
                                    break;
                                case 10: completeArr[9] +=1;
                                    break;
                                case 11: completeArr[10] +=1;
                                    break;
                                case 12: completeArr[11] +=1;
                                    break;
                                default: completeArr[12] +=1;
                                    break;
                            }
                            System.out.println(completeArr);
                            Log.d("Complete", data.toString()) ;

                        }
                        progressDialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "The Complete report data is not exist " , Toast.LENGTH_SHORT); toast.show();
                        Log.d("Complete", dataSnapshot.toString()) ;
                            Intent i = new Intent(HistoryAdminActivity.this, CompleteReportActivity.class);
                        i.putExtra("completeArr", completeArr);
                        i.putExtra("year", year);
                        startActivity(i);
                    } else {
                        progressDialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "The Complete report data is not exist " , Toast.LENGTH_SHORT); toast.show();
                        Log.d("Complete", dataSnapshot.toString()) ;
                        Intent i = new Intent(HistoryAdminActivity.this, CompleteReportActivity.class);
                        i.putExtra("completeArr", completeArr);
                        i.putExtra("year", year);
                        startActivity(i);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError)  {
                    Toast toast = Toast.makeText(getApplicationContext(), "Error on retrieveing data " , Toast.LENGTH_SHORT); toast.show();
                    progressDialog.dismiss();
                }
            });
        }else{
            progressDialog.dismiss();
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Search term can't be empty! Please input the year");
            alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //write your code here to execute after dialog closed
                        }
                    });
            alertDialog.show();
        }
    }
}
