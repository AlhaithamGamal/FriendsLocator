package com.example.friendslocator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.friendslocator.common.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
boolean flag = false;
    private static final int RC_PHOTO_PICKER = 1;
    StorageReference ref;
    StorageReference photoRef;
    DatabaseReference storagedatabaseRef;
    Uri imageUri;
    DatabaseReference locateR;
    public String  downloadUri = "";
    ProgressDialog progressDialogImage;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseDatabase storageDatabase;
    DatabaseReference table_user;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.profile_status)
    EditText profileStatus;
    @BindView(R.id.profile_name)
    EditText profileName;
    @BindView(R.id.update_profile)
    Button update;
    @OnClick(R.id.update_profile) void update(){
        updateAll();


    }



    @OnClick(R.id.profile_image) void selectImage(){
    Intent galleryIntent = new Intent();
    galleryIntent.setAction(Intent.ACTION_PICK);
    galleryIntent.setType("image/*");
    startActivityForResult(galleryIntent, RC_PHOTO_PICKER);

}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        progressDialogImage = new ProgressDialog(this);
        progressDialogImage.setMessage("please wait");
        ButterKnife.bind(this);
    //    checkAll();
//        profileName.setText(Common.currentUser.getName());
//        profileName.setEnabled(false);
        Toast.makeText(getApplicationContext(),"status:"+Common.currentContact.getStatus(),Toast.LENGTH_LONG).show();
        profileStatus.setText(Common.currentContact.getStatus());
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storageDatabase = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        locateR = database1.getReference("Profile");
        photoRef = FirebaseStorage.getInstance().getReference();
        storagedatabaseRef = storageDatabase.getReference("imgs");

    }

    private void checkAll() {

                      locateR.child("currentId").child(Common.currentContact.getPhone()).addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              if(dataSnapshot.exists() && dataSnapshot.hasChild("status")){
                                  String status = dataSnapshot.child("status").getValue().toString();
                                  profileStatus.setText(status);


                              }
                                  if(dataSnapshot.exists() && dataSnapshot.hasChild("imageURL")){
                                      Picasso.get().load(Common.currentContact.getImageURL()).into(profileImage);

                                  }
                                      if(dataSnapshot.exists() && dataSnapshot.hasChild("name")){

                                          String name = dataSnapshot.child("name").getValue().toString();
                                          profileName.setText(name);
                                      }

                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      });








    }


    public void updateAll() {
        Common.currentContact.setStatus(profileStatus.getText().toString());

      uploadProfileImage();
           locateR.child("currentId").child(Common.currentContact.getPhone().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if (dataSnapshot.child("currentId").child(Common.currentContact.getPhone()).exists()) {
                       locateR.child("currentId").child(Common.currentContact.getPhone()).removeValue();
                       //update new value
                       locateR.child("currentId").child(Common.currentContact.getPhone()).setValue(Common.currentContact);
                       Toast.makeText(getApplicationContext(), "Profile updated ", Toast.LENGTH_SHORT).show();

                   } else {
                       locateR.child("currentId").child(Common.currentContact.getPhone()).setValue(Common.currentContact);
                       Toast.makeText(getApplicationContext(), "Profile updated", Toast.LENGTH_SHORT).show();

                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });


    }

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
                                    Common.currentContact.setImageURL(downloadUri);
                                    locateR.child("currentId").child(Common.currentContact.getPhone().toString()).child("imageURL").setValue(downloadUri);
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
