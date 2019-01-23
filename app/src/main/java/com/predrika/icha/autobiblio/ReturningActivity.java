package com.predrika.icha.autobiblio;

    import android.app.ProgressDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.net.Uri;
    import android.support.annotation.NonNull;
    import android.support.v7.app.AlertDialog;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.support.v7.widget.Toolbar;
    import android.text.InputType;
    import android.text.method.PasswordTransformationMethod;
    import android.util.Log;
    import android.support.v7.widget.LinearLayoutManager;
    import android.support.v7.widget.RecyclerView;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.EditText;
    import android.widget.LinearLayout;
    import android.widget.TextView;
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

    import org.joda.time.LocalDate;

    import java.io.ByteArrayOutputStream;

public class ReturningActivity extends AppCompatActivity {

    private RecyclerView mOnGoingRV;

    private DatabaseReference mDatabase;
    private DatabaseReference m1Database;
    private DatabaseReference mBorrowDatabase;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<OnGoing, OnGoingViewHolder> mOnGoingRVAdapter;

    ProgressDialog progressDialog;
    private String uid;
    private int counter;
    private String link;
    private String info;
    private String title;
    private String bookId;
    private String issuedDate;
    private String maxReturnDate;
    private String storageName;
    private double overdueCost;
    private double damageCost;
    private LocalDate todayDate;
    private String borrowDetail="";

    @Override
    public void onStart() {
        super.onStart();
        mOnGoingRVAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mOnGoingRVAdapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returning);

        //toolbar
        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("Return List");
        getSupportActionBar().hide();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScannerActivity.class);
                startActivity(intent);
            }
        });

        progressDialog = new ProgressDialog(ReturningActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        Intent intent = getIntent();
        uid = intent.getExtras().getString("uid");

        mBorrowDatabase = FirebaseDatabase.getInstance().getReference().child("BorrowQR");
        mBorrowDatabase.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check exist
                if(dataSnapshot.exists()){
                    for(DataSnapshot data1: dataSnapshot.getChildren()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Log.d("BorrowQR- exist", data.toString()) ;
                            BorrowQR borrowQR = data.getValue(BorrowQR.class);
                            link= borrowQR.getLink();
                            info=borrowQR.getInfo();
                            counter=borrowQR.getCounter();
                            Log.d("COUNTER", Integer.toString(counter)) ;
                            Log.d("LINK", link) ;
                            Log.d("INFO", info) ;
                        }
                    }
                } else {
                    Log.d("dataSnapshot- not exist", dataSnapshot.toString()) ;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)  {
                Toast toast = Toast.makeText(getApplicationContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT); toast.show();
            }
        });

        //retrieve database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("OnGoing");
        mDatabase.keepSynced(true);

        mOnGoingRV = findViewById(R.id.onGoingLoanRecycleView);

        DatabaseReference onGoingRef = FirebaseDatabase.getInstance().getReference().child("OnGoing");
        Query onGoingQuery = onGoingRef.orderByChild("uid").equalTo(uid);

        mOnGoingRV.hasFixedSize();
        mOnGoingRV.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions onGoingOptions = new FirebaseRecyclerOptions.Builder<OnGoing>().setQuery(onGoingQuery, OnGoing.class).build();

        //Adapter>> connect database into view
        mOnGoingRVAdapter = new FirebaseRecyclerAdapter<OnGoing, ReturningActivity.OnGoingViewHolder>(onGoingOptions) {
            @Override
            protected void onBindViewHolder(ReturningActivity.OnGoingViewHolder holder, final int position, final OnGoing model) {
                holder.setTitle(model.getTitle());
                holder.setBookIdOnGoing(model.getBookIdOnGoing());
                holder.setIssuedDate(model.getIssuedDate());
                holder.setMaxReturnDate(model.getMaxReturnDate());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        title = model.getTitle();
                        bookId = model.getBookIdOnGoing();
                        issuedDate = model.getIssuedDate();
                        maxReturnDate= model.getMaxReturnDate();

                        m1Database = FirebaseDatabase.getInstance().getReference().child("OnGoing");
                        m1Database.orderByChild("bookIdOnGoing").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot data1: dataSnapshot.getChildren()){
                                        for(DataSnapshot data: dataSnapshot.getChildren()) {
                                            storageName= data.getKey();
                                            Log.d("data.getKey", data.getKey());
                                            calculateFine();
                                        }
                                    }
                                } else {
                                    Log.d("data.getKey", "onGoing not exist");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)  {
                                Toast toast = Toast.makeText(getApplicationContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT); toast.show();
                            }
                        });
                    }
                });
            }
            //viewholder>> listview
            @Override
            public ReturningActivity.OnGoingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.on_going_row, parent, false);
                return new ReturningActivity.OnGoingViewHolder(view);
            }
        };
        mOnGoingRV.setAdapter(mOnGoingRVAdapter);

        //add the listener for the single value event that will function
        mBorrowDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static class OnGoingViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public OnGoingViewHolder(View itemView){
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
    private void calculateFine(){
        todayDate = new LocalDate();
        LocalDate issuedDateD = new LocalDate(issuedDate);
        int totalDay = todayDate.getDayOfMonth() - issuedDateD.getDayOfMonth();

        //10000 for first day, 3000 for next days
        if (totalDay<=7){
            overdueCost=0.0;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Return Book?");
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setNeutralButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //delete OnGoing
                    checkDamage();
                }
            });
            builder.setMessage("The borrowing process detailed below has no fine \n\n"+ totalDay+"\n"+uid +"\n"+ title +"\n"+bookId + "\n"+issuedDate +"\n"+ maxReturnDate);
            AlertDialog alert1 = builder.create();
            alert1.show();
        }else{
            if(totalDay==1){
                overdueCost=10000.0;
            }else{
                overdueCost=(totalDay-1)*3000.0+10000.0;
            }
            checkDamage();
        }
        Log.d("OverdueCost-out ovd", Double.toString(overdueCost)) ;
        Log.d("DamageCost-out ovd", Double.toString(damageCost)) ;
    }

    private void checkDamage(){
        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(ReturningActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Damage or Lost Check");
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verifyUser(damageCost);
            }
        });
        builder.setNeutralButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
        // Assign activity this to progress dialog.
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("BooksSpecification");
                //get value from database
                mRef.orderByChild("title").equalTo(title).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //check unpaid fines
                        if(dataSnapshot.exists()){
                            for(DataSnapshot data1: dataSnapshot.getChildren()){
                                for(DataSnapshot data: dataSnapshot.getChildren()) {
                                    Log.d("book existence", data.toString());
                                    BooksSpecification booksSpecification = data.getValue(BooksSpecification.class);
                                    damageCost = booksSpecification.getPrice();
                                    Log.d("getPrice()", Double.toString(booksSpecification.getPrice()));
                                    Log.d("OverdueCos-on async dmg", Double.toString(overdueCost)) ;
                                    Log.d("DamageCost-on async dmg", Double.toString(damageCost)) ;
                                    progressDialog.dismiss();
                                    verifyUser(damageCost);
                                }
                            }
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "The book data is not exist " , Toast.LENGTH_SHORT); toast.show();
                            progressDialog.dismiss();
                        }
                        Log.d("OverdueCost-out dmg", Double.toString(overdueCost)) ;
                        Log.d("DamageCost-out dmg", Double.toString(damageCost)) ;
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError)  {
                        Toast toast = Toast.makeText(getApplicationContext(), "Error on retrieveing data " , Toast.LENGTH_SHORT); toast.show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
        builder.setMessage("Is the book damaged or lost?\n");
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    private void verifyUser(final double damageCost){
        mAuth= FirebaseAuth.getInstance();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReturningActivity.this);
        alertDialog.setTitle("Password Verification");
        alertDialog.setMessage("Enter your password");

        final EditText input = new EditText(ReturningActivity.this);
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

                        progressDialog = new ProgressDialog(ReturningActivity.this);
                        progressDialog.setMessage("Verifying user's password... ");
                        progressDialog.show();
                        progressDialog.setCancelable(false);

                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(ReturningActivity.this,
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(!task.isSuccessful()){
                                            Toast toast = Toast.makeText(getApplicationContext(), "Password is wrong! " , Toast.LENGTH_SHORT); toast.show();
                                            progressDialog.dismiss();
                                            verifyUser(damageCost);
                                        }else{
                                            //createHistory
                                            if(overdueCost>0 || damageCost>0){
                                                Log.d("OverdueCost >0", Double.toString(overdueCost)) ;
                                                Log.d("DamageCost >0", Double.toString(damageCost)) ;

                                                //create fines
                                                DatabaseReference finesRef = FirebaseDatabase.getInstance().getReference();
                                                Fines fines= new Fines();
                                                fines.setTitleFines(title);
                                                fines.setBookIdFines(bookId);
                                                fines.setDamageCost(damageCost);
                                                fines.setOverdueCost(overdueCost);
                                                fines.setTotalCost(damageCost+overdueCost);
                                                fines.setPaidAmount(0.0);
                                                fines.setPaidOff("NOT PAID");
                                                fines.setUid(uid);
                                                finesRef.child("Fines").child(storageName).setValue(fines);

                                                DatabaseReference completeRef = FirebaseDatabase.getInstance().getReference();
                                                Complete complete= new Complete();
                                                complete.setTitleComplete(title);
                                                complete.setBookIdComplete(bookId);
                                                if (damageCost>0){
                                                    complete.setDamagedYN("DAMAGED");
                                                }else{
                                                    complete.setDamagedYN("NOT DAMAGED");
                                                }
                                                if(overdueCost>0){
                                                    complete.setOverdueYN("OVERDUE");
                                                }else{
                                                    complete.setOverdueYN("NOT OVERDUE");
                                                }
                                                complete.setIssuedDateComplete(issuedDate);
                                                complete.setReturnedDate(todayDate.toString());
                                                complete.setPaidOffYN("NOT PAID");
                                                complete.setYear(Integer.toString(todayDate.getYear()));
                                                complete.setUid(uid);
                                                completeRef.child("Complete").child(storageName).setValue(complete);

                                                progressDialog.dismiss();
                                                Log.d("Delete ongoing", "ovd dmg >0") ;
                                                deleteOnGoing();

                                            }else{
                                                Log.d("OverdueCost =0", Double.toString(overdueCost)) ;
                                                Log.d("DamageCost =0", Double.toString(damageCost)) ;

                                                DatabaseReference completeRef = FirebaseDatabase.getInstance().getReference();
                                                Complete complete= new Complete();
                                                complete.setTitleComplete(title);
                                                complete.setBookIdComplete(bookId);
                                                complete.setDamagedYN("NOT DAMAGED");
                                                complete.setOverdueYN("NOT OVERDUE");
                                                complete.setIssuedDateComplete(issuedDate);
                                                complete.setReturnedDate(todayDate.toString());
                                                complete.setPaidOffYN("NO FINE");
                                                complete.setYear(Integer.toString(todayDate.getYear()));
                                                complete.setUid(uid);
                                                completeRef.child("Complete").child(storageName).setValue(complete);

                                                progressDialog.dismiss();
                                                Log.d("Delete ongoing", "ovd dmg =0") ;
                                                deleteOnGoing();
                                            }
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

    private void deleteOnGoing(){
        progressDialog = new ProgressDialog(ReturningActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        //change book's availability
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference().child("Books/"+bookId.replace(".","-"));
        booksRef.child("availability").setValue("AVAILABLE");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("OnGoing").child(storageName).removeValue();

        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://autobiblio-c72c0.appspot.com");
        final StorageReference borrowRef = storageRef.child("borrowqr/"+info+".jpg");
        Log.d("borrowRef!!!!!", borrowRef.toString()) ;
        // Delete the file
        borrowRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "The book is successfully returned", Toast.LENGTH_LONG).show();
                checkBorrowQR();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressDialog.dismiss();
                checkBorrowQR();
            }
        });
    }

    private void checkBorrowQR(){
        progressDialog = new ProgressDialog(ReturningActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        if(counter<=1){
            Log.d("counter<=1", Integer.toString(counter)) ;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("BorrowQR").child(uid).removeValue();

            Intent intent = new Intent(getApplicationContext(), ReturningActivity.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        }else {
            Log.d("counter>1", Integer.toString(counter)) ;
            String[] arrSplit = info.split(";");
            for (int i = 0; i < arrSplit.length; i++) {
                if (arrSplit[i].equals(storageName)) {
                    arrSplit[i] = "";
                }
            }
            counter-=1;
            borrowDetail=uid+";"+counter;
            Log.d("borrowDetail :", borrowDetail) ;
            for (int i = 2; i < arrSplit.length; i++) {
                borrowDetail = borrowDetail +";"+ arrSplit[i];
            }

            //final String borrowDetail= uid +";"+ bookId +";"+ title +";"+ issuedDate+";"+ maxReturnDate +";" +storageName ;
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                //generate QR code
                BitMatrix bitMatrix = multiFormatWriter.encode(borrowDetail, BarcodeFormat.QR_CODE, 200, 200);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                //upload to firebase storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://autobiblio-c72c0.appspot.com");
                final StorageReference borrowRef = storageRef.child("borrowqr/" + borrowDetail + ".jpg");

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

                            //update qr code link
                            DatabaseReference borrowQRRef = FirebaseDatabase.getInstance().getReference();
                            BorrowQR borrowQR = new BorrowQR();
                            borrowQR.setCounter(counter);
                            borrowQR.setInfo(borrowDetail);
                            borrowQR.setUid(uid);
                            borrowQR.setLink(downloadUri.toString());
                            borrowQRRef.child("BorrowQR/" + uid).setValue(borrowQR);

                            progressDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), ReturningActivity.class);
                            intent.putExtra("uid", uid);
                            startActivity(intent);
                        }else{
                            progressDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), ReturningActivity.class);
                            intent.putExtra("uid", uid);
                            startActivity(intent);
                        }
                    }
                });
            }
            catch (WriterException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent intent = new Intent(getApplicationContext(), ScannerActivity.class);
        startActivity(intent);
    }
}
