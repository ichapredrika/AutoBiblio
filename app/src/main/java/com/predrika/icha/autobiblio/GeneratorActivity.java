package com.predrika.icha.autobiblio;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.joda.time.LocalDate;

public class GeneratorActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);
    }

    public void generateClick(View view) {
        progressDialog = new ProgressDialog(GeneratorActivity.this);
        progressDialog.setMessage("Generating QR code...");
        progressDialog.show();

        TextView amountTV=findViewById(R.id.amount);
        String amount= amountTV.getText().toString();
        LocalDate todayDate = new LocalDate();
        //LocalDate todayDate = new LocalDate("2014-12-12"); //testing
        ImageView image = findViewById(R.id.image);
        String compilation = amount+ ";"+todayDate;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(compilation, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            image.setImageBitmap(bitmap);
            progressDialog.dismiss();
        }
        catch (WriterException e){
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }
}
