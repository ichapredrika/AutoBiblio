package com.predrika.icha.autobiblio;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    //Zxing
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

    //Firebase
    private DatabaseReference m1Database;
    private String isbn;
    private String bookId;
    //private int availability=0; //0>> not exist, 1>> avail, 2>> borrowed
    private int codeFormat; //1>> barcode, 2>>  QR code
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
        new android.support.v7.app.AlertDialog.Builder(Scanner.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());

        if (result.getBarcodeFormat() == BarcodeFormat.QR_CODE){
            codeFormat = 2;
            //split the result
            String strReplace = myResult.replaceAll("\n","");
            String[] arrSplit = strReplace.split(",");
            String id= arrSplit[arrSplit.length-1];
            String[] arrId = id.split(" ");
            bookId=arrId[arrId.length-1];
        }else {
            codeFormat=1;
            bookId =myResult;
        }
        //check the existence of the book
        m1Database = FirebaseDatabase.getInstance().getReference().child("Books");
        m1Database.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check exist
                if(dataSnapshot.exists()){
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        isbn= data.child("isbn").getValue().toString();
                        String avail=data.child("availability").getValue().toString();
                        //if book is available
                        if(avail.equals("Available")){

                            int availability=1;
                            bookId=data.child("bookId").getValue().toString();
                            showAvail(availability, bookId, myResult,isbn);
                        }
                        //book is borrowed
                        else {
                            int availability=2;
                            bookId=data.child("bookId").getValue().toString();
                            showAvail(availability, bookId, myResult, isbn);
                        }

                    }
                    Toast toast = Toast.makeText(getApplicationContext(), "exist", Toast.LENGTH_LONG);
                    toast.show();
                    //not exist
                } else {
                    int availability=0;
                    showAvail(availability, bookId, myResult, isbn);
                    Toast toast = Toast.makeText(getApplicationContext(), "not exist", Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)  {
                Toast toast = Toast.makeText(getApplicationContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT); toast.show();
            }

        });
    }
    private void showAvail(int availability, final String bookId, String myResult, final String isbn){
        if (availability==1){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Proceed to borrowing process?");
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scannerView.resumeCameraPreview(Scanner.this);
                }
            });
            builder.setNeutralButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //go to borrowing process
                    Intent intent = new Intent(getApplicationContext(), BorrowActivity.class);
                    intent.putExtra("bookId", bookId);
                    intent.putExtra("isbn", isbn);
                    startActivity(intent);
                }
            });
            builder.setMessage("The book with following detail is available\n\n"+ myResult);
            AlertDialog alert1 = builder.create();
            alert1.show();
        }else if(availability==2){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("The book is not available");
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scannerView.resumeCameraPreview(Scanner.this);
                }
            });
            builder.setMessage("The book with following detail is not available\n\n"+ myResult);
            AlertDialog alert1 = builder.create();
            alert1.show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("The book is not exist in the library");
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scannerView.resumeCameraPreview(Scanner.this);
                }
            });
            builder.setMessage("The book with following detail is not exist in the library\n\n"+ myResult);
            AlertDialog alert1 = builder.create();
            alert1.show();
        }
    }
}