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

    private RecyclerView mBookDetailRV;
    private DatabaseReference mDatabase;
    private DatabaseReference m1Database;
    private FirebaseRecyclerAdapter<BooksSpecification, BorrowActivity.BookDetailViewHolder> mBookDetailRVAdapter;
    private String isbn;

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
        //final String isbn="9780552779739";

        //check the existence of the book
        m1Database = FirebaseDatabase.getInstance().getReference().child("Books");
        m1Database.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check exist
                if(dataSnapshot.exists()){
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        String avail=data.child("availability").getValue().toString();
                        //if book is available
                        if(avail.equals("Available")){
                            TextView availTV = findViewById(R.id.availability);
                            availTV.setText("The book is available");

                            String keys=data.child("bookId").getValue().toString();
                            TextView bookIdTV = findViewById(R.id.post_bookId);
                            bookIdTV.setText(keys);
                        }
                        //book is borrowed
                        else {
                            TextView availTV = findViewById(R.id.availability);
                            availTV.setText("The book is temporarily unavailable");

                            String keys=data.child("bookId").getValue().toString();
                            TextView bookIdTV = findViewById(R.id.post_bookId);
                            bookIdTV.setText(keys);
                        }

                        isbn= data.child("isbn").getValue().toString();
                        TextView isbnTV = findViewById(R.id.post_isbn);
                        isbnTV.setText(isbn);
                    }

                    Toast toast = Toast.makeText(getApplicationContext(), "exist", Toast.LENGTH_LONG);
                    toast.show();
                //not exist
                } else {
                    TextView availTV = findViewById(R.id.availability);
                    availTV.setText("The book is not exist in the library");
                    Toast toast = Toast.makeText(getApplicationContext(), "not exist", Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)  {
                Toast toast = Toast.makeText(getApplicationContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT); toast.show();
            }
        });

        // check ongoing fines

        //get database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("BooksSpecification/"+isbn);
        mDatabase.keepSynced(true);

        mBookDetailRV = findViewById(R.id.bookDetailRecycleView);

        DatabaseReference BookDetailRef = FirebaseDatabase.getInstance().getReference().child("BooksSpecification/"+isbn);
        Query BookDetailQuery = BookDetailRef.orderByKey();


        mBookDetailRV.hasFixedSize();
        mBookDetailRV.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions BookDetailOptions = new FirebaseRecyclerOptions.Builder<BooksSpecification>().setQuery(BookDetailQuery, BooksSpecification.class).build();

        //Adapter>> connect database into view
        mBookDetailRVAdapter = new FirebaseRecyclerAdapter<BooksSpecification, BorrowActivity.BookDetailViewHolder>(BookDetailOptions) {
            @Override
            protected void onBindViewHolder(BorrowActivity.BookDetailViewHolder holder, final int position, final BooksSpecification model) {
                holder.setBookId(model.getBookId());
                holder.setAuthor(model.getAuthor());
                holder.setPublisher(model.getPublisher());
                holder.setCollectionType(model.getCollectionType());
                holder.setLocation(model.getLocation());
            }

            //viewholder>> listview
            @Override
            public BorrowActivity.BookDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.borrow_book_detail_row, parent, false);

                return new BorrowActivity.BookDetailViewHolder(view);
            }
        };

        mBookDetailRV.setAdapter(mBookDetailRVAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBookDetailRVAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBookDetailRVAdapter.stopListening();
    }

    public static class BookDetailViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public BookDetailViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        public void setBookId(String bookId){
            TextView post_bookId = mView.findViewById(R.id.post_bookId);
            post_bookId.setText(bookId);
        }
        public void setAuthor(String author){
            TextView post_author = mView.findViewById(R.id.post_author);
            post_author.setText(author);
        }
        public void setPublisher(String publisher){
            TextView post_publisher = mView.findViewById(R.id.post_publisher);
            post_publisher.setText(publisher);
        }
        public void setCollectionType(String collectionType){
            TextView post_collectionType = mView.findViewById(R.id.post_collectionType);
            post_collectionType.setText(collectionType);
        }
        public void setLocation(String location){
            TextView post_location = mView.findViewById(R.id.post_location);
            post_location.setText(location);
        }
    }
}
