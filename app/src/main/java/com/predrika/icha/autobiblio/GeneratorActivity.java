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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GeneratorActivity extends AppCompatActivity {

    // Creating Progress dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

    }

    public void generateClick(View view) {
        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(GeneratorActivity.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Generating QR code...");

        // Showing progress dialog.
        progressDialog.show();

        TextView amountTV=findViewById(R.id.amount);
        String amount= amountTV.getText().toString();
        ImageView image = findViewById(R.id.image);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(amount, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            image.setImageBitmap(bitmap);

            // Hiding the progress dialog.
            progressDialog.dismiss();

        }
        catch (WriterException e){
            e.printStackTrace();

            // Hiding the progress dialog.
            progressDialog.dismiss();
        }
    }
}
