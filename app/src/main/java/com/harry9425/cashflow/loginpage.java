package com.harry9425.cashflow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginpage extends AppCompatActivity {

    EditText emailid,password;
    String email,pass;
    Button next;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        emailid=(EditText) findViewById(R.id.loginemail);
        password=(EditText) findViewById(R.id.loginpass);
        next=(Button) findViewById(R.id.logincontinue);
        firebaseAuth=FirebaseAuth.getInstance();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             email=emailid.getText().toString().trim();
             pass=password.getText().toString().trim();
             if(!pass.isEmpty() && !email.isEmpty()){
                 firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(loginpage.this,"Signed in",Toast.LENGTH_SHORT).show();
                           Intent i =new Intent(loginpage.this,MainActivity.class);
                           startActivity(i);
                           finish();
                       }
                       else {
                           firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent i =new Intent(loginpage.this,MainActivity.class);
                                    startActivity(i);
                                    finish();

                                }
                                else {
                                    Toast.makeText(loginpage.this,"Wrong Id/password",Toast.LENGTH_SHORT).show();
                                    emailid.setError("");
                                    password.setError("");
                                }
                               }
                           });
                       }
                     }
                 });
             }
             else {
                 Toast.makeText(loginpage.this,"Fields can't be empty",Toast.LENGTH_SHORT).show();
                 if(email.isEmpty()){
                     emailid.setError("Empty");
                 }
                 else {
                     password.setError("Empty");
                 }
             }
            }
        });
    }
}