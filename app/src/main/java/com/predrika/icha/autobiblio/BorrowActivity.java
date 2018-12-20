package com.predrika.icha.autobiblio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Iterator;

public class BorrowActivity extends AppCompatActivity {

    private RecyclerView mAvailRV;
    private DatabaseReference mDatabase;
    private DatabaseReference m1Database;
    private FirebaseRecyclerAdapter<Books, BorrowActivity.AvailViewHolder> mAvailRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);


        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("Borrow Page");
        getSupportActionBar().hide();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();

            }
        });

        Intent intent = getIntent();
        final String bookId = intent.getExtras().getString("bookId");
        final String isbn="9780552779739";

        TextView bookIdTV = findViewById(R.id.tv1);
        bookIdTV.setText(bookId);

        m1Database = FirebaseDatabase.getInstance().getReference().child("Books");

        m1Database.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String keys=data.child("bookId").getValue().toString();
                    TextView bookIdTV2 = findViewById(R.id.tv2);
                    bookIdTV2.setText(keys);
                    String isbn= data.child("bookId").getValue().toString();
                }
                if(dataSnapshot.exists()){
                    Toast toast = Toast.makeText(getApplicationContext(), "exist", Toast.LENGTH_LONG);
                    toast.show();

                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "not exist", Toast.LENGTH_LONG);
                    toast.show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)  {
                Toast toast = Toast.makeText(getApplicationContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT); toast.show();
            }
        });


        //check whether the book is exist or not
        // check ongoing fines
        //check ongoing borrowing;

        //"Collection" here will reflect what you have called in your database in Firebase.
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Books/"+isbn);
        mDatabase.keepSynced(true);

        mAvailRV = findViewById(R.id.collectionRecycleView);

        DatabaseReference availRef = FirebaseDatabase.getInstance().getReference().child("Books/"+isbn);
        Query availQuery = availRef.orderByKey();


        mAvailRV.hasFixedSize();
        mAvailRV.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions availOptions = new FirebaseRecyclerOptions.Builder<Books>().setQuery(availQuery, Books.class).build();

        //Adapter>> connect database into view
        mAvailRVAdapter = new FirebaseRecyclerAdapter<Books, BorrowActivity.AvailViewHolder>(availOptions) {
            @Override
            protected void onBindViewHolder(BorrowActivity.AvailViewHolder holder, final int position, final Books model) {
                holder.setBookId(model.getBookId());
                holder.setAvailability(model.getAvailability());
            }

            //viewholder>> listview
            @Override
            public BorrowActivity.AvailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.availability_row, parent, false);

                return new BorrowActivity.AvailViewHolder(view);
            }
        };

        mAvailRV.setAdapter(mAvailRVAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAvailRVAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAvailRVAdapter.stopListening();
    }

    public static class AvailViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public AvailViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        public void setBookId(String bookId){
            TextView post_bookId = mView.findViewById(R.id.post_bookId);
            post_bookId.setText(bookId);
        }
        public void setAvailability(String availability){
            TextView post_availability = mView.findViewById(R.id.post_availability);
            post_availability.setText(availability);
        }
    }
}
