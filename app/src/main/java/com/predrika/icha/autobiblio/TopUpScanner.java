package com.predrika.icha.autobiblio;

import android.app.ProgressDialog;
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
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.joda.time.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class TopUpScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    //Zxing
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    ProgressDialog progressDialog;

    private double topUpAmount;
    private String qrDate;

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
        new android.support.v7.app.AlertDialog.Builder(TopUpScanner.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(TopUpScanner.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        //progressDialog.setCancelable(false);

        mAuth=FirebaseAuth.getInstance();

        final String myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());

        //split the result
        String[] arrSplit = myResult.split(";");
        if(arrSplit.length==2){
            topUpAmount= Double.valueOf(arrSplit[0]);
            System.out.println(topUpAmount);
            qrDate= arrSplit[1];
            System.out.println(qrDate);

            if (validateDate(qrDate)==true){
                //check if the local date is the same as today's date
                LocalDate todaysDate= new LocalDate();
                LocalDate qrDateL = new LocalDate(qrDate);
                if (todaysDate==qrDateL){
                    progressDialog.dismiss();
                    Toast toast = Toast.makeText(getApplicationContext(), "Verified QRcode", Toast.LENGTH_LONG);
                    toast.show();
                    topUpBalance();
                }else{
                    progressDialog.dismiss();
                    Toast toast = Toast.makeText(getApplicationContext(), "Not Verified QRcode", Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent( TopUpScanner.this,ProfileActivity.class);
                    startActivity(intent);
                }
            }else{
                progressDialog.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(), "Not Verified QRcode", Toast.LENGTH_LONG);
                toast.show();
                Intent intent = new Intent( TopUpScanner.this,ProfileActivity.class);
                startActivity(intent);
            }

        }else{
            progressDialog.dismiss();
            Toast toast = Toast.makeText(getApplicationContext(), "Not Verified QRcode", Toast.LENGTH_LONG);
            toast.show();
            Intent intent = new Intent( TopUpScanner.this,ProfileActivity.class);
            startActivity(intent);
        }

    }

    private void topUpBalance(){
        mAuth =FirebaseAuth.getInstance();
        FirebaseUser Users= FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(TopUpScanner.this);
        progressDialog.setMessage("Please wait... ");
        progressDialog.show();
        progressDialog.setCancelable(false);
        //add balance to current user
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users/"+Users.getUid());
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressDialog.dismiss();
                    Users currentUser = dataSnapshot.getValue(Users.class);
                    double balanceAmount =currentUser.getBalanceAmount();
                    Log.d("balance initial", Double.toString(currentUser.getBalanceAmount()));
                    double balance = topUpAmount + balanceAmount;
                    pushBalance(balance);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast toast = Toast.makeText(getApplicationContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT);
                toast.show();
                progressDialog.dismiss();
                Intent intent = new Intent(TopUpScanner.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
    private void pushBalance(double balance){
        mAuth =FirebaseAuth.getInstance();
        DatabaseReference balanceRef = FirebaseDatabase.getInstance().getReference().child("Users/"+mAuth.getCurrentUser().getUid());
        balanceRef.child("balanceAmount").setValue(balance);
        Toast toast = Toast.makeText(getApplicationContext(), "Your balance is topped up! Total : " +balance, Toast.LENGTH_LONG);
        toast.show();
        Intent intent = new Intent( TopUpScanner.this,ProfileActivity.class);
        startActivity(intent);
    }

    public static boolean validateDate(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(dateString);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }
}