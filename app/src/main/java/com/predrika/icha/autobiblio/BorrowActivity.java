package com.predrika.icha.autobiblio;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;

public class BorrowActivity extends AppCompatActivity {

    private RecyclerView mBookDetailRV;
    private DatabaseReference mDatabase;
    private DatabaseReference m1Database;
    private DatabaseReference mImageDatabase;
    private FirebaseRecyclerAdapter<BooksSpecification, BorrowActivity.BookDetailViewHolder> mBookDetailRVAdapter;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    // Creating Progress dialog
    ProgressDialog progressDialog;

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

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(BorrowActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);

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
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)  {
                Toast toast = Toast.makeText(getApplicationContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT); toast.show();

                // Hiding the progress dialog.
                progressDialog.dismiss();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BorrowActivity.this);
        alertDialog.setTitle("Verify your password");
        alertDialog.setMessage("Enter Password");

        final EditText input = new EditText(BorrowActivity.this);
        input.setInputType( InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("Verify",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String email = mAuth.getCurrentUser().getEmail();
                        String password = input.getText().toString();
                        // Assign activity this to progress dialog.
                        progressDialog = new ProgressDialog(BorrowActivity.this);
                        progressDialog.setMessage("Processing borrowing request... ");
                        progressDialog.show();
                        progressDialog.setCancelable(false);

                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(BorrowActivity.this,
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(!task.isSuccessful()){
                                            Toast toast = Toast.makeText(getApplicationContext(), "Password is wrong! " , Toast.LENGTH_SHORT); toast.show();
                                            // Hiding the progress dialog.
                                            progressDialog.dismiss();
                                        }else{
                                            //generate QR code
                                            final String uid= mAuth.getCurrentUser().getUid();
                                            TextView post_title =findViewById(R.id.post_title);
                                            final String title= post_title.getText().toString();
                                            TextView bookIdTV = findViewById(R.id.bookId);
                                            final String bookId = bookIdTV.getText().toString();
                                            TextView issuedDateTV = findViewById(R.id.post_issuedDate);
                                            final String issuedDate= issuedDateTV.getText().toString();
                                            TextView maxReturnDateTV = findViewById(R.id.post_maxReturnDate);
                                            final String maxReturnDate= maxReturnDateTV.getText().toString();

                                            DateTime dateTime = new DateTime();
                                            Timestamp timeStamp = new Timestamp(dateTime.getMillis());
                                            final String storageName= uid +"-"+timeStamp.getTime();

                                            final String borrowDetail= uid +";"+ bookId +";"+ title +";"+ issuedDate+";"+ maxReturnDate +";" +storageName ;
                                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                            try{
                                                BitMatrix bitMatrix = multiFormatWriter.encode(borrowDetail, BarcodeFormat.QR_CODE,200,200);
                                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                                //upload to firebase storage
                                                // Create a storage reference from our app
                                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                                StorageReference storageRef = storage.getReferenceFromUrl("gs://autobiblio-c72c0.appspot.com");
                                                final StorageReference borrowRef = storageRef.child("borrowqr/"+storageName+".jpg");

                                                // While the file names are the same, the references point to different files
                                                borrowRef.getName().equals(borrowRef.getName());    // true
                                                borrowRef.getPath().equals(borrowRef.getPath());    // false

                                                // Get the data from an ImageView as bytes
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                byte[] data = baos.toByteArray();

                                                UploadTask uploadTask = borrowRef.putBytes(data);
                                                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                    @Override
                                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                        if (!task.isSuccessful()) {
                                                            throw task.getException();
                                                        }
                                                        return borrowRef.getDownloadUrl();
                                                    }
                                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                        if (task.isSuccessful()) {
                                                            Uri downloadUri = task.getResult();
                                                            //insert into onGoing
                                                            DatabaseReference onGoingRef = FirebaseDatabase.getInstance().getReference();
                                                            OnGoing onGoing= new OnGoing();
                                                            onGoing.setBookIdOnGoing(bookId);
                                                            onGoing.setTitle(title);
                                                            onGoing.setIssuedDate(issuedDate);
                                                            onGoing.setMaxReturnDate(maxReturnDate);
                                                            onGoing.setBorrowQR(downloadUri.toString());
                                                            onGoingRef.child("OnGoing/"+uid).child(storageName).setValue(onGoing);
                                                            finish();

                                                            //Books
                                                            DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference().child("Books/"+bookId.replace(".","-"));
                                                            booksRef.child("availability").setValue("BORROWED");
                                                            finish();
                                                            Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                                                            startActivity(intent);
                                                            // Hiding the progress dialog.
                                                            progressDialog.dismiss();
                                                        } else {
                                                            // Hiding the progress dialog.
                                                            progressDialog.dismiss();
                                                        }
                                                    }
                                                });
                                            }
                                            catch (WriterException e){
                                                e.printStackTrace();
                                            }

                                           //
                                        }
                                    }
                                });
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }
}


