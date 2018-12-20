package com.predrika.icha.autobiblio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class BorrowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);

        Intent intent = getIntent();
        String bookId = intent.getExtras().getString("bookId");

        TextView bookIdTV = findViewById(R.id.bookId);
        bookIdTV.setText(bookId);
    }
}
