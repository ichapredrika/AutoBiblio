package com.predrika.icha.autobiblio;

import android.app.Dialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private String name;
    private String email;
    private String password;
    private String univId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
    }

    public void registrationClick(View view) {

        EditText idCardEditText =findViewById(R.id.univIdReg);
        univId=idCardEditText.getText().toString();


        EditText nameEditText= findViewById(R.id.nameReg);
        name= nameEditText.getText().toString();

        EditText emailEditText= findViewById(R.id.emailRegistration);
        email= emailEditText.getText().toString();

        EditText passwordEditText =findViewById(R.id.passwordRegistration);
        password=passwordEditText.getText().toString();

        if(!nameEditText.getText().toString().isEmpty() || !emailEditText.getText().toString().isEmpty() || !passwordEditText.getText().toString().isEmpty()){
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(RegistrationActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();
                            }else{
                                DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
                                DatabaseReference usersRef=mRef.child("Users/" +mAuth.getCurrentUser().getUid());

                                EditText editName=findViewById(R.id.nameReg);
                                name= editName.getText().toString();

                                usersRef.child("fullName").setValue(name);
                                usersRef.child("emailAddress").setValue(email);
                                usersRef.child("univId").setValue(univId);
                                usersRef.child("pob").setValue("-");
                                usersRef.child("dob").setValue("-");
                                usersRef.child("address").setValue("-");
                                usersRef.child("phone").setValue("-");
                                usersRef.child("memberType").setValue("Student");

                                Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent( RegistrationActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            //setting Dialog Title
            alertDialog.setTitle("Warning");
            //setting Dialog Message
            alertDialog.setMessage("Name, email, or password can not be empty!");
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
