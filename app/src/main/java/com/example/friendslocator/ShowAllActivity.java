package com.example.friendslocator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.friendslocator.Databases.Databases;
import com.example.friendslocator.adapters.ContactsAdapter;
import com.example.friendslocator.adapters.CustomAdapter;
import com.example.friendslocator.models.Contacts;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class ShowAllActivity extends AppCompatActivity {
    RecyclerView rcV;
    ListView lstV;
    List<Contacts> contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Locator");
        setSupportActionBar(toolbar);
//        rcV = findViewById(R.id.recView);
        lstV = findViewById(R.id.lstV);
       loadContacts();



    }

    private void loadContacts() {
        contactsList = new ArrayList<>();
        contactsList = new Databases(this).getContacts();
//        ContactsAdapter contactsAdapter = new ContactsAdapter(this,contactsList);
//        rcV.setLayoutManager(new LinearLayoutManager(this));
//        rcV.setAdapter(contactsAdapter);
        CustomAdapter contactsCustom = new CustomAdapter(this,contactsList);
        lstV.setAdapter(contactsCustom);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menutwo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clear) {
            clearList();
            loadContacts();

        }
        return super.onOptionsItemSelected(item);

    }

    private void clearList() {
        new Databases(this).clearContacts();
    }
}
