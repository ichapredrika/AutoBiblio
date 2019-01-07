package com.predrika.icha.autobiblio;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.joda.time.LocalDate;

public class LoginActivity extends AppCompatActivity {
    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private String email;
    private String password;

    // Creating Progress dialog
    ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize the FirebaseAuth instance.
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener=new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent= new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };
    }

    public void registerClick(View view){
        Intent intent=new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void loginClick(View view){
        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(LoginActivity.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Please wait...");

        // Showing progress dialog.
        progressDialog.show();

        EditText emailEditText=findViewById(R.id.emailLogin);
        email = emailEditText.getText().toString();

        EditText passwordEditText= findViewById(R.id.passwordLogin);
        password= passwordEditText.getText().toString();

        if(!emailEditText.getText().toString().isEmpty() || !passwordEditText.getText().toString().isEmpty()){
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                                //Intent intent= new Intent(LoginActivity.this, Splash.class);
                                //startActivity(intent);
                            }else{
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            }
                            // Hiding the progress dialog.
                            progressDialog.dismiss();
                        }
                    });
        }else{
            // Hiding the progress dialog.
            progressDialog.dismiss();

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            //setting Dialog Title
            alertDialog.setTitle("Warning");
            //setting Dialog Message
            alertDialog.setMessage("Email or password can not be empty!");
            //setting ok button
            alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //write your code here to execute after dialog closed

                        }
                    });
            //showing alert message
            alertDialog.show();
        }


    }
}
