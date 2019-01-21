package com.predrika.icha.autobiblio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CollectionSearchActivity extends AppCompatActivity {

    private RecyclerView mCollectionSearchRV;

    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<BooksSpecification, CollectionSearchActivity.CollectionSearchViewHolder> mCollectionSearchRVAdapter;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_search);

        //toolbar
        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("Collection Search");
        getSupportActionBar().hide();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        String searchTxt = intent.getExtras().getString("searchTxt");
        String searchTerm = intent.getExtras().getString("searchTerm");
        String a=searchTerm;
        TextView bannerTV=findViewById(R.id.searchBannerTxt);
        bannerTV.setText(searchTxt);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(CollectionSearchActivity.this);
        progressDialog.setMessage("Loading books from database");
        progressDialog.show();
        progressDialog.setCancelable(false);

        //retrieve database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("BooksSpecification");
        mDatabase.keepSynced(true);

        mCollectionSearchRV = findViewById(R.id.titleRecycleView);

        DatabaseReference collectionSearchRef = FirebaseDatabase.getInstance().getReference().child("BooksSpecification");
        Query collectionSearchQuery = collectionSearchRef.orderByChild(searchTerm).startAt(searchTxt).endAt(searchTxt+"\uf8ff");

        mCollectionSearchRV.hasFixedSize();
        mCollectionSearchRV.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions collectionSearchOptions = new FirebaseRecyclerOptions.Builder<BooksSpecification>().setQuery(collectionSearchQuery, BooksSpecification.class).build();
        //Adapter>> connect database into view
        mCollectionSearchRVAdapter = new FirebaseRecyclerAdapter<BooksSpecification, CollectionSearchActivity.CollectionSearchViewHolder>(collectionSearchOptions) {
            @Override
            protected void onBindViewHolder(CollectionSearchActivity.CollectionSearchViewHolder holder, final int position, final BooksSpecification model) {
                holder.setTitle(model.getTitle());
                holder.setAuthor(model.getAuthor());
                holder.setImage(model.getImage());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String title = model.getTitle();
                        final String author= model.getAuthor();
                        final String publisher = model.getPublisher();
                        final String collectionType= model.getCollectionType();
                        final String location= model.getLocation();
                        final String image= model.getImage();
                        final String isbn= model.getIsbn();
                        Intent intent = new Intent(getApplicationContext(), CollectionViewActivity.class);
                        intent.putExtra("title", title);
                        intent.putExtra("author", author);
                        intent.putExtra("publisher", publisher);
                        intent.putExtra("collectionType", collectionType);
                        intent.putExtra("location", location);
                        intent.putExtra("image", image);
                        intent.putExtra("isbn", isbn);
                        startActivity(intent);
                    }
                });
            }

            //viewholder>> listview
            @Override
            public CollectionSearchActivity.CollectionSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.collection_row, parent, false);
                return new CollectionSearchActivity.CollectionSearchViewHolder(view);
            }
        };

        mCollectionSearchRV.setAdapter(mCollectionSearchRVAdapter);

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
        mCollectionSearchRVAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCollectionSearchRVAdapter.stopListening();
    }

    public static class CollectionSearchViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public CollectionSearchViewHolder(View itemView){
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
        public void setImage( String image){
            ImageView post_image = mView.findViewById(R.id.post_image);
            Picasso.get().load(image).into(post_image);
        }
    }
}
