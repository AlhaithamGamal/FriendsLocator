package com.example.friendslocator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.friendslocator.Databases.Databases;
import com.example.friendslocator.common.Common;
import com.example.friendslocator.models.Contacts;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.location.LocationListener;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onurkaganaldemir.ktoastlib.KToast;

import io.paperdb.Paper;

public class HomeMapActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Button showFriend, showAll;
    EditText phoneEdt;
    FirebaseAuth mAuth;
    Switch swt;
    double lng;
    double lat;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference locateR;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Locator");
        setSupportActionBar(toolbar);
        Paper.init(this);
        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        locateR = firebaseDatabase.getReference("Locations");
        showFriend = findViewById(R.id.show_friend);
        showAll = findViewById(R.id.show_all);
        swt = findViewById(R.id.switch_loc);
        phoneEdt = findViewById(R.id.enter_phone);
        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeMapActivity.this, ShowAllActivity.class);
                startActivity(intent);
//                Animatoo.animateCard(getApplicationContext());
            }
        });

        // mGoogleApiClient.disconnect();
        defineGoogleApiClient();
        swt.setChecked(false);
        mGoogleApiClient.disconnect();

        swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    //      defineGoogleApiClient();
                    mGoogleApiClient.disconnect();
                } else {
                    mGoogleApiClient.connect();

                    //Toast.makeText(getApplicationContext(),"Disconnected",Toast.LENGTH_SHORT).show();
                }
            }
        });

        showFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(phoneEdt.length() == 0)) {

                    locateR.child("currentId").child(phoneEdt.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        double lng, lat;
                        String name, imageURL, phone, email, status;

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                lng = Double.parseDouble(dataSnapshot.child("lng").getValue().toString());
                                lat = Double.parseDouble(dataSnapshot.child("lat").getValue().toString());
                                name = dataSnapshot.child("name").getValue().toString();
                                imageURL = dataSnapshot.child("imageURL").getValue().toString();
                                email = dataSnapshot.child("email").getValue().toString();
                                phone = dataSnapshot.child("phone").getValue().toString();
                                status = dataSnapshot.child("status").getValue().toString();

                                new Databases(getApplicationContext()).clearContacts();

                                new Databases(getApplicationContext()).addContact(new Contacts(name, imageURL, email, phone, lng, lat, status));
                                Toast.makeText(getApplicationContext(), "friend saved", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "no data found", Toast.LENGTH_SHORT).show();
                            }

                            Intent intent = new Intent(HomeMapActivity.this, MapsLocateActivity.class);
                            intent.putExtra("lng", lng);
                            intent.putExtra("lat", lat);
                            intent.putExtra("name", name);
                            startActivity(intent);
//                            Animatoo.animateCard(HomeMapActivity.this);


                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {

                    phoneEdt.setError("Please enter phone number");
                }
            }
        });
        ActivityCompat.requestPermissions(HomeMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);


    }

    private void defineGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Animatoo.animateCard(HomeMapActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.log_out) {
            Paper.book().destroy();
            signOut();
            Intent inte = new Intent(HomeMapActivity.this, SignInActivity.class);
            inte.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(inte);
            Animatoo.animateCard(this);
        }
        else
            if(item.getItemId() == R.id.settings){

                Intent intent = new Intent(HomeMapActivity.this,ProfileActivity.class);
                startActivity(intent);
                Animatoo.animateCard(this);
            }
        return super.onOptionsItemSelected(item);

    }

    private void signOut() {

        mAuth.signOut();
    }

    @Override
    public void onLocationChanged(Location location) {
        lng = location.getLongitude();
        lat = location.getLatitude();
        locationFirebaseUpdated();

    }

    private void locationFirebaseUpdated() {
        Common.currentContact.setLat(lat);
        Common.currentContact.setLng(lng);
        locateR.child("currentId").child(Common.currentContact.getPhone().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("currentId").child(Common.currentContact.getPhone()).exists()) {
                    locateR.child("currentId").child(Common.currentContact.getPhone()).removeValue();
                    //update new value
                    locateR.child("currentId").child(Common.currentContact.getPhone()).setValue(Common.currentContact);
                    Toast.makeText(getApplicationContext(), "Loc updated ", Toast.LENGTH_SHORT).show();

                } else {
                    locateR.child("currentId").child(Common.currentContact.getPhone()).setValue(Common.currentContact);
                    Toast.makeText(getApplicationContext(), "Loc updated", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10); // Update location every second
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), "Suspended", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "No connection", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //  mGoogleApiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
}
