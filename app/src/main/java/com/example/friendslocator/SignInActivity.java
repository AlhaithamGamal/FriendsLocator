package com.example.friendslocator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.friendslocator.common.Common;
import com.example.friendslocator.models.Contacts;
import com.example.friendslocator.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity {
EditText edtEmail,edtPassword;
TextView forgetPassword;
Button btnSignIn;
CheckBox ckRemember;
    FirebaseDatabase fd;
    public String emailFound = "";
    public String imageFound = "";
    DatabaseReference storagedatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Paper.init(this);
        edtEmail = findViewById(R.id.edtemail);
        forgetPassword = findViewById(R.id.forget_password);
        edtPassword = findViewById(R.id.edtpassword);
        btnSignIn = findViewById(R.id.btnSignin);
        ckRemember = (CheckBox)findViewById(R.id.ckBRemember);
        //init paper to save keys to andoroid memory
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference();
        fd = FirebaseDatabase.getInstance();
        storagedatabaseRef =fd.getReference("imgs");
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectToInternet(getBaseContext())) {
                    final String email= edtEmail.getText().toString();
                    final String password = edtPassword.getText().toString();

                    if (email.length() == 0) {

                        edtEmail.setError("Please Enter Your Phone...");
                    }

                    if (password.length() == 0) {
                        edtPassword.setError("Please Enter Your Password...");

                    }
                    if(ckRemember.isChecked()){

                        Paper.book().write(Common.USER_KEY,edtEmail.getText().toString());
                        Paper.book().write(Common.PWD_KEY,edtPassword.getText().toString());
                    }
                    final ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);
                    progressDialog.setMessage("Please waiting...");
                    progressDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String emailFormat =email.replace(".", ",");
                                final User userF = dataSnapshot.child("User").child("currentId").child(emailFormat).getValue(User.class);
//                                final String emails = userF.getEmail().toString().trim();
                                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
//                                            if(user.isEmailVerified()) {

                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"welcome "+userF.getEmail().toString()+"Check your profile from the settings above to upload your image and update your status",Toast.LENGTH_SHORT).show();
                                                storagedatabaseRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for(DataSnapshot postSnap : dataSnapshot.getChildren()){
                                                            emailFound = postSnap.child("email").getValue().toString();
                                                           imageFound = postSnap.child("photoURL").getValue().toString();
                                                            if(emailFound .equals(email)){

                                                                break;
                                                            }


                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                                    Intent intent = new Intent(SignInActivity.this, HomeMapActivity.class);
                                                    userF.setImageURI(imageFound);
                                                    Common.currentUser = userF;
                                                    Common.currentContact = new Contacts(userF.getName(),userF.getImageURI(),userF.getEmail(),userF.getPhone(),"");
                                                    startActivity(intent);
//                                               Animatoo.animateCard(SignInActivity.this);
                                                    finish();



//                                            else{
//progressDialog.dismiss();
//                                                Toast.makeText(getApplicationContext(),"check your verification",Toast.LENGTH_SHORT).show();
//
//                                            }


                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(),"failed signing in",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                });



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                }


                else {
                    Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void forgetPassword(View view) {
        Intent intent = new Intent(this, ForgetPasswordActivity.class);
        startActivity(intent);
    }
}
