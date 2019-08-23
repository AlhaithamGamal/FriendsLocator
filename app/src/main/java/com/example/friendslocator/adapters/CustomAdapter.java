package com.example.friendslocator.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.friendslocator.MapsLocateActivity;
import com.example.friendslocator.R;
import com.example.friendslocator.ShowAllActivity;
import com.example.friendslocator.models.Contacts;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Contacts> {
    List<Contacts> contactList;
    Context context;
    public CustomAdapter(Context context,List<Contacts> contactList) {
        super(context,0, contactList);
        this.contactList = contactList;
        this.context = context;

    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null)
        {
            LayoutInflater layoutInflater =(LayoutInflater) getContext().
                    getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView =layoutInflater.inflate(R.layout.contacts_layout,null,true);
        }

        final Contacts contacts = getItem(position);
        TextView name = convertView.findViewById(R.id.name);
        TextView status = convertView.findViewById(R.id.status);
        TextView email = convertView.findViewById(R.id.email);
        TextView phone = convertView.findViewById(R.id.phone);
        ImageView img = convertView.findViewById(R.id.image_contact);
        name.setText(contacts.getName());
        email.setText(contacts.getEmail());
        phone.setText(contacts.getPhone());
        status.setText(contacts.getStatus());

        Glide.with(context).load(contacts.getImageURL()).into(img);

convertView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent i = new Intent(context, MapsLocateActivity.class);
        // sending data process
        i.putExtra("name",contacts.getName().toString());
        i.putExtra("lng",contacts.getLng());
        i.putExtra("lat",contacts.getLat());

        context.startActivity(i);
    }
});

        return convertView;
    }
}
