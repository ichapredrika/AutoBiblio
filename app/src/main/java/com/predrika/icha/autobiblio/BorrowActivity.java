package com.predrika.icha.autobiblio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class BorrowActivity extends AppCompatActivity {

    private RecyclerView mBookDetailRV;
    private DatabaseReference mDatabase;
    private DatabaseReference m1Database;
    private FirebaseRecyclerAdapter<BooksSpecification, BorrowActivity.BookDetailViewHolder> mBookDetailRVAdapter;
    private FirebaseAuth mAuth;

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
        String bookId = intent.getExtras().getString("bookId");
        String isbn = intent.getExtras().getString("isbn");

        TextView bookIdpTV = findViewById(R.id.bookId);
        bookIdpTV.setText(bookId);

        TextView bookIdTV = findViewById(R.id.post_bookIdOnGoing);
        bookIdTV.setText(bookId);

        TextView isbnTV = findViewById(R.id.isbn);
        isbnTV.setText(isbn);

        TextView availTV = findViewById(R.id.availability);
        availTV.setText("The book is available");

        Button borrowBtn = findViewById(R.id.borrowBtn);
        borrowBtn.setVisibility(View.GONE);

        // check ongoing fines
        mAuth= FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        //refresh
        m1Database = FirebaseDatabase.getInstance().getReference().child("Fines/"+uid);
        m1Database.keepSynced(true);

        m1Database = FirebaseDatabase.getInstance().getReference().child("Fines/"+uid);
        m1Database.orderByChild("paidOff").equalTo("Not Fully Paid").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check unpaid fines

                if(dataSnapshot.exists()){
                    for(DataSnapshot data1: dataSnapshot.getChildren()){
                        for(DataSnapshot data: dataSnapshot.getChildren()) {
                            Fines fines = data.getValue(Fines.class);
                            double totalCost = fines.getTotalCost();
                            double paidAmount = fines.getPaidAmount();
                            double totalFines = totalCost - paidAmount;

                            TextView onGoingFinesTV = findViewById(R.id.onGoingFines);
                            onGoingFinesTV.setText("You have an outstanding fines");
                            TextView finesTV = findViewById(R.id.post_onGoingFines);
                            finesTV.setText(Double.toString(totalFines));


                            TextView eligibilityTV = findViewById(R.id.eligibility);
                            eligibilityTV.setText("You are not eligible to make this transaction");
                        }
                    }

                } else {
                    TextView onGoingFinesTV = findViewById(R.id.onGoingFines);
                    onGoingFinesTV.setText("You don't have outstanding fines");
                    TextView finesTV = findViewById(R.id.post_onGoingFines);
                    finesTV.setText("-");

                    TextView eligibilityTV = findViewById(R.id.eligibility);
                    eligibilityTV.setText("You are eligible to make this transaction");

                    Button borrowBtn = findViewById(R.id.borrowBtn);
                    borrowBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)  {
                Toast toast = Toast.makeText(getApplicationContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT); toast.show();
            }

        });
        //get database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("BooksSpecification");
        mDatabase.keepSynced(true);

        mBookDetailRV = findViewById(R.id.bookDetailRecycleView);

        DatabaseReference BookDetailRef = FirebaseDatabase.getInstance().getReference().child("BooksSpecification");
        Query BookDetailQuery = BookDetailRef.orderByChild("isbn").equalTo(isbn);

        mBookDetailRV.hasFixedSize();
        mBookDetailRV.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions BookDetailOptions = new FirebaseRecyclerOptions.Builder<BooksSpecification>().setQuery(BookDetailQuery, BooksSpecification.class).build();

        //Adapter>> connect database into view
        mBookDetailRVAdapter = new FirebaseRecyclerAdapter<BooksSpecification, BorrowActivity.BookDetailViewHolder>(BookDetailOptions) {
            @Override
            protected void onBindViewHolder(BorrowActivity.BookDetailViewHolder holder, final int position, final BooksSpecification model) {
                holder.setTitle(model.getTitle());
                holder.setAuthor(model.getAuthor());
                holder.setPublisher(model.getPublisher());
                holder.setCollectionType(model.getCollectionType());
                holder.setLocation(model.getLocation());
                holder.setImage(model.getImage());

                TextView titleTV = findViewById(R.id.post_title);
                titleTV.setText(model.getTitle());

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

        // setup objects' time
        LocalDate issuedDate = new LocalDate();
        LocalDate maxReturnDate = issuedDate.plusDays(7);

        TextView issuedDateTV = findViewById(R.id.post_issuedDate);
        issuedDateTV.setText(issuedDate.toString());//
        TextView maxReturnDateTV = findViewById(R.id.post_maxReturnDate);
        maxReturnDateTV.setText(maxReturnDate.toString());//

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
        public void setTitle(String title){
            TextView post_title = mView.findViewById(R.id.post_title);
            post_title.setText(title);
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
        public void setImage( String image){
            ImageView post_image = mView.findViewById(R.id.post_image);
            Picasso.get().load(image).into(post_image);
        }
    }

    public void borrowClick(View view){
        mAuth= FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference onGoingRef = FirebaseDatabase.getInstance().getReference();

        TextView post_title =findViewById(R.id.post_title);
        TextView bookIdpTV = findViewById(R.id.bookId);
        String bookId = bookIdpTV.getText().toString();
        TextView issuedDateTV = findViewById(R.id.post_issuedDate);
        TextView maxReturnDateTV = findViewById(R.id.post_maxReturnDate);

        //onGoing
        OnGoing onGoing= new OnGoing();
        onGoing.setUid(onGoingRef.child("OnGoing/"+uid).push().getKey());
        onGoing.setBookIdOnGoing(bookIdpTV.getText().toString());
        onGoing.setTitle(post_title.getText().toString());
        onGoing.setIssuedDate(issuedDateTV.getText().toString());
        onGoing.setMaxReturnDate(maxReturnDateTV.getText().toString());
        onGoingRef.child("OnGoing/"+uid).child(onGoing.getUid()).setValue(onGoing);
        finish();

        //Books
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference().child("Books/"+bookId.replace(".","-"));
        booksRef.child("availability").setValue("Borrowed");
        finish();
        Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
        startActivity(intent);
    }
}


