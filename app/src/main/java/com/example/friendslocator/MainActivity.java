package com.example.friendslocator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.friendslocator.common.Common;
import com.example.friendslocator.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
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

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    Button signIn, signUp;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);
        signIn = findViewById(R.id.btnSignin);
        signUp = findViewById(R.id.btnSignup);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference();
signUp.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);

    }
});
signIn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this,SignInActivity.class);
        startActivity(intent);
//        Animatoo.animateCard(getApplicationContext());
    }
});


        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if (user != null && pwd != null) {

            if (!user.isEmpty() && !pwd.isEmpty()) {
                login(user, pwd);
            }
        }
    }

    private void login(final String user, final String pwd) {

        if (Common.isConnectToInternet(getBaseContext())) {
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please waiting...");
            progressDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final User userF = dataSnapshot.child("User").child(user).getValue(User.class);
                    final String emails = userF.getEmail().toString().trim();
                    Toast.makeText(getApplicationContext(), "Email is:" + emails, Toast.LENGTH_LONG).show();
                    mAuth.signInWithEmailAndPassword(user, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user.isEmailVerified()) {

                                    progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Signed In Successfully", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(MainActivity.this, HomeMapActivity.class);
                                        Common.currentUser = userF;
                                        startActivity(intent);
//                                    Animatoo.animateCard(getApplicationContext());

                                    finish();



                                }
                                else{

                                    Toast.makeText(getApplicationContext(), "User not verified", Toast.LENGTH_LONG).show();

                                }


                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed Sign In try again ...with email " + emails + task.getException(), Toast.LENGTH_LONG).show();
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
    }