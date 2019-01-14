package com.predrika.icha.autobiblio;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ReturnScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    //Zxing
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

    //Firebase
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;

    // Creating Progress dialog
    ProgressDialog progressDialog;
    //null for firebase key error handler
    private String uid;
    private String title;
    private String bookId;
    private String issuedDate;
    private String maxReturnDate;
    private String storageName;
    private double overdueCost;
    private double damageCost;
    private LocalDate todayDate;

    //check permission
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;

        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                requestPermission();
            }
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(ReturnScanner.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(final Result result) {
        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(ReturnScanner.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        final String myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());

        //split the result
        String[] arrSplit =myResult.split(";");
        if (arrSplit.length==6){
            uid=arrSplit[0];
            bookId=arrSplit[1];
            title=arrSplit[2];
            issuedDate=arrSplit[3];
            maxReturnDate=arrSplit[4];
            storageName=arrSplit[5];
            //check on the database
            mDatabase = FirebaseDatabase.getInstance().getReference().child("OnGoing/"+uid);
            mDatabase.orderByChild("bookIdOnGoing").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //check exist
                    if(dataSnapshot.exists()){
                        Log.d("dataSnapshot- exist", dataSnapshot.toString()) ;
                        Toast toast = Toast.makeText(getApplicationContext(), "exist", Toast.LENGTH_LONG);
                        toast.show();
                        calculateFine();
                        progressDialog.dismiss();
                        //not exist
                    } else {
                        Log.d("dataSnapshot- not exist", dataSnapshot.toString()) ;
                        Toast toast = Toast.makeText(getApplicationContext(), "not exist", Toast.LENGTH_LONG);
                        toast.show();
                        progressDialog.dismiss();
                        showNotExist(result.toString(), 1);

                    }
/*                Intent intent = new Intent( ReturnScanner.this,ScannerActivity.class);
                startActivity(intent);*/
                }

                @Override
                public void onCancelled(DatabaseError databaseError)  {
                    Toast toast = Toast.makeText(getApplicationContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT); toast.show();
                }
            });
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "not exist", Toast.LENGTH_LONG);
            toast.show();
            progressDialog.dismiss();
            showNotExist(result.toString(),2);
        }
    }
    private void showNotExist(String result, int exist){
        if (exist==1){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Longer Exist");
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    scannerView.resumeCameraPreview(ReturnScanner.this);
                }
            });
            builder.setMessage("The Following transaction is no longer exist! \n\n"+"\n"+result );
            AlertDialog alert1 = builder.create();
            alert1.show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Not Exist");
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    scannerView.resumeCameraPreview(ReturnScanner.this);
                }
            });
            builder.setMessage("The Following transaction is not exist! \n\n"+"\n"+result );
            AlertDialog alert1 = builder.create();
            alert1.show();
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
                    scannerView.resumeCameraPreview(ReturnScanner.this);

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
        progressDialog = new ProgressDialog(ReturnScanner.this);
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
                            progressDialog.dismiss();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReturnScanner.this);
        alertDialog.setTitle("Password Verification");
        alertDialog.setMessage("Enter your password");
        final EditText input = new EditText(ReturnScanner.this);
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
                        progressDialog = new ProgressDialog(ReturnScanner.this);
                        progressDialog.setMessage("Verifying user's password... ");
                        progressDialog.show();
                        progressDialog.setCancelable(false);

                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(ReturnScanner.this,
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(!task.isSuccessful()){
                                            Toast toast = Toast.makeText(getApplicationContext(), "Password is wrong! " , Toast.LENGTH_SHORT); toast.show();
                                            // Hiding the progress dialog.
                                            progressDialog.dismiss();
                                            verifyUser(damageCost);
                                        }else{

                                            Log.d("OverdueCost >0", Double.toString(overdueCost)) ;
                                            Log.d("DamageCost >0", Double.toString(damageCost)) ;
                                            //createHistory
                                            if(overdueCost>0 || damageCost>0){
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
                                                finesRef.child("Fines/"+uid).child(storageName).setValue(fines);
                                                finish();

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
                                                complete.setUid(uid);
                                                completeRef.child("Complete/"+uid).child(storageName).setValue(complete);
                                                finish();
                                                progressDialog.dismiss();
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
                                                complete.setUid(uid);
                                                completeRef.child("Complete/"+uid).child(storageName).setValue(complete);
                                                progressDialog.dismiss();
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
                        scannerView.resumeCameraPreview(ReturnScanner.this);
                    }
                });

        alertDialog.show();
    }

    private void deleteOnGoing(){
        progressDialog = new ProgressDialog(ReturnScanner.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        //change book's availability
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference().child("Books/"+bookId.replace(".","-"));
        booksRef.child("availability").setValue("AVAILABLE");
        finish();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("OnGoing").child(uid).child(storageName).removeValue();

        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://autobiblio-c72c0.appspot.com");
        final StorageReference borrowRef = storageRef.child("borrowqr/"+storageName+".jpg");
        // Delete the file
        borrowRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "The book is successfully returned", Toast.LENGTH_LONG).show();
                Intent intent = new Intent( ReturnScanner.this,ScannerActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                progressDialog.dismiss();
                Intent intent = new Intent( ReturnScanner.this,ScannerActivity.class);
                startActivity(intent);
            }
        });
    }
}
