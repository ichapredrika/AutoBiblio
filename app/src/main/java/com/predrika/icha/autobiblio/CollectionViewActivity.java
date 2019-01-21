package com.predrika.icha.autobiblio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CollectionViewActivity extends AppCompatActivity {

    private RecyclerView mAvailRV;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Books, CollectionViewActivity.CollectionViewViewHolder> mAvailRVAdapter;

    ProgressDialog progressDialog;

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
                finish();

            }
        });

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(CollectionViewActivity.this);
        progressDialog.setMessage("Loading book from database");
        progressDialog.show();
        progressDialog.setCancelable(false);

        Intent intent = getIntent();
        String title = intent.getExtras().getString("title");
        String author = intent.getExtras().getString("author");
        String publisher = intent.getExtras().getString("publisher");
        String collectionType = intent.getExtras().getString("collectionType");
        String location = intent.getExtras().getString("location");
        String image = intent.getExtras().getString("image");
        String isbn = intent.getExtras().getString("isbn");

        TextView titleTV= findViewById(R.id.post_title);
        titleTV.setText(title);

        TextView authorTV= findViewById(R.id.post_author);
        authorTV.setText(author);

        TextView publisherTV= findViewById(R.id.post_publisher);
        publisherTV.setText(publisher);

        TextView collectionTypeTV= findViewById(R.id.post_collectionType);
        collectionTypeTV.setText(collectionType);

        TextView locationTV= findViewById(R.id.post_location);
        locationTV.setText(location);

        TextView isbnTV= findViewById(R.id.post_isbn);
        isbnTV.setText(isbn);

        ImageView imageV= findViewById(R.id.post_image);
        Picasso.get().load(image).into(imageV);

        //"Collection" here will reflect what you have called in your database in Firebase.
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mDatabase.keepSynced(true);

        mAvailRV = findViewById(R.id.availRecycleView);

        DatabaseReference availRef = FirebaseDatabase.getInstance().getReference().child("Books");
        Query availQuery = availRef.orderByChild("isbn").equalTo(isbn);

        mAvailRV.hasFixedSize();
        mAvailRV.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions availOptions = new FirebaseRecyclerOptions.Builder<Books>().setQuery(availQuery, Books.class).build();

        //Adapter>> connect database into view
        mAvailRVAdapter = new FirebaseRecyclerAdapter<Books, CollectionViewActivity.CollectionViewViewHolder>(availOptions) {
            @Override
            protected void onBindViewHolder(CollectionViewActivity.CollectionViewViewHolder holder, final int position, final Books model) {
                holder.setBookId(model.getBookId());
                holder.setAvailability(model.getAvailability());
            }
            //viewholder>> listview
            @Override
            public CollectionViewActivity.CollectionViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.availability_row, parent, false);
                return new CollectionViewActivity.CollectionViewViewHolder(view);
            }
        };
        mAvailRV.setAdapter(mAvailRVAdapter);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
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

    public static class CollectionViewViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public CollectionViewViewHolder(View itemView){
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
