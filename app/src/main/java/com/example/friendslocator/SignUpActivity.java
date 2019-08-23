package com.example.friendslocator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.example.friendslocator.common.Common;
import com.example.friendslocator.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    Button signup;
    EditText phone, name, password, email;
    String emailFormat = "";
    String downloadUri="";
    private static final int RC_PHOTO_PICKER = 1;
    StorageReference ref;
    StorageReference photoRef;
    DatabaseReference storagedatabaseRef;
    Uri imageUri;
    public      User usertwo;
    CircleImageView imgV;
    ProgressDialog progressDialog;
    ProgressDialog progressDialogImage;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseDatabase storageDatabase;
    DatabaseReference table_user;

    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @OnClick(R.id.profile_image) void selectImage(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, RC_PHOTO_PICKER);


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        progressDialogImage = new ProgressDialog(this);
        progressDialogImage.setMessage("please wait");
        signup = findViewById(R.id.btnSignup);
        phone = findViewById(R.id.edtphone);
        email = findViewById(R.id.edtemail);
        name = findViewById(R.id.edtname);
        password = findViewById(R.id.edtpassword);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storageDatabase = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");
        photoRef = FirebaseStorage.getInstance().getReference();
        storagedatabaseRef = storageDatabase.getReference("imgs");
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectToInternet(getBaseContext())) {
                    final String Email = email.getText().toString();
                    String Password = password.getText().toString();
                    String Name = name.getText().toString();
                    String Phone = phone.getText().toString();
                    if (Email.length() == 0) {

                        email.setError("Please Enter Your Email...");
                    }

                    if (Password.length() == 0) {
                        password.setError("Please Enter Your Password...");

                    }
                    if (Phone.length() == 0) {
                        phone.setError("Please Enter Your Phone...");

                    }
                    if (Name.length() == 0) {
                        name.setError("Please Enter Your Password...");

                    } else {
                        progressDialog = new ProgressDialog(SignUpActivity.this);
                        progressDialog.setTitle("Wait");
                        progressDialog.setMessage("Please waiting...");
                        progressDialog.show();
                        mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                   // user.sendEmailVerification();
                                    table_user.addValueEventListener(new ValueEventListener() {


                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            progressDialog.dismiss();
                                //            uploadProfileImage();

                                                usertwo = new User(email.getText().toString(), name.getText().toString(), password.getText().toString(), phone.getText().toString());
                                               emailFormat = Email.replace(".", ",");
                                                table_user.child("currentId").child(emailFormat).setValue(usertwo);

                                                Toast.makeText(getApplicationContext(), "Account created successfully ", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                                startActivity(intent);
//                                            Animatoo.animateCard(getApplicationContext());


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "faile creating accont", Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                    }


                } else {
                    Toast.makeText(getApplicationContext(), "check internet connection", Toast.LENGTH_LONG).show();

                }
            }

        });
    }


    public void uploadImage(View view) {
    }



    //======================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            this.imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }

    }

    public boolean uploadProfileImage() {


        if (imageUri != null) {

            progressDialogImage.show();
            ref = photoRef.child("photos/*" + System.currentTimeMillis());
            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialogImage.dismiss();


                    // Toast.makeText(getApplicationContext(),"Uploaded Successfully",Toast.LENGTH_LONG).show();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUri = String.valueOf(uri);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("photoURL", uri.toString());
                                    storagedatabaseRef.push().setValue(map);
                                    progressDialogImage.dismiss();
                                  usertwo.setImageURI(downloadUri);
                              //      Common.currentContact.setImageURL(downloadUri);
                                    table_user.child("currentId").child(emailFormat).child("imageURI").setValue(downloadUri);
                                    Toast.makeText(getApplicationContext(), "uploaded successfully ", Toast.LENGTH_LONG).show();


                                }
                            });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {progressDialogImage.dismiss();
                    Toast.makeText(getApplicationContext(), "failed uploading", Toast.LENGTH_LONG).show();

                }
            });
            return true;} else {progressDialogImage.dismiss();
            Toast.makeText(getApplicationContext(), "no image uploaded", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
