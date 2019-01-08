package com.predrika.icha.autobiblio;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.joda.time.LocalDate;

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

    // Creating Progress dialog
    ProgressDialog progressDialog;

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
    public void handleResult(Result result) {
        // Assign activity this to progress dialog.

        final String myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());

        //split the result
        String[] arrSplit =myResult.split(";");
        for (int i=0; i < arrSplit.length; i++)
        {
            System.out.println(arrSplit[i]);
        }
        final String uid=arrSplit[0];
        final String title=arrSplit[1];
        final String bookId=arrSplit[2];
        final String issuedDate=arrSplit[3];
        final String maxReturnDate=arrSplit[4];
        final String storageName=arrSplit[5];

        //check exist or not!!
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("OnGoing/"+uid);
        mDatabase.orderByChild(storageName).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check exist
                if(dataSnapshot.exists()){

                    Toast toast = Toast.makeText(getApplicationContext(), "exist", Toast.LENGTH_LONG);
                    toast.show();
                    calculateFine(uid, title, bookId, issuedDate, maxReturnDate, storageName);
                    //not exist
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "not exist", Toast.LENGTH_LONG);
                    toast.show();
                }
                Intent intent = new Intent( ReturnScanner.this,ScannerActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)  {
                Toast toast = Toast.makeText(getApplicationContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT); toast.show();
            }
        });
        //calculate fine

    }
    private void calculateFine(final String uid, String  title, final String  bookId, String  issuedDate, String  maxReturnDate, final String  storageName){
        LocalDate todayDate = new LocalDate();
        LocalDate issuedDateD = new LocalDate(issuedDate);
        int totalDay = todayDate.getDayOfMonth() - issuedDateD.getDayOfMonth();
        //10000 for first day, 3000 for next days
        double overdueCost;
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
                    progressDialog = new ProgressDialog(ReturnScanner.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    progressDialog.setCancelable(false);

                    //Book's availability
                    DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference().child("Books/"+bookId.replace(".","-"));
                    booksRef.child("availability").setValue("Available");
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
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                            progressDialog.dismiss();
                        }
                    });

                    //create History
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
        }
    }
}
