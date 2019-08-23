package com.example.friendslocator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    EditText emailEdt;
    Button forget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        emailEdt = findViewById(R.id.forget_password);
        forget = findViewById(R.id.btnForget);
        fAuth = FirebaseAuth.getInstance();
    }

    public void forgetPassword(View view) {
        String email = emailEdt.getText().toString();

        fAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), "Password sent", Toast.LENGTH_LONG).show();
                        }
                        else{

                            Toast.makeText(getApplicationContext(),"Exception"+task.getException(),Toast.LENGTH_LONG).show();
                        }

                    }
                });


    }
}
