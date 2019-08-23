package com.example.friendslocator.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendslocator.HomeMapActivity;
import com.example.friendslocator.MapsLocateActivity;
import com.example.friendslocator.R;
import com.example.friendslocator.interfaces.ItemClickListener;
import com.example.friendslocator.models.Contacts;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements View.OnClickListener {
    private Context mContext ;
    private List<Contacts> mData ;
    public ItemClickListener itemClickListener;

    public ContactsAdapter(Context mContext, List<Contacts> mData){
        this.mContext = mContext;
        this.mData = mData;

    }
    @Override
    public void onClick(View v) {


    }

    @NonNull
    @Override
    public ContactsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.contacts_layout,parent,false) ;
        final MyViewHolder viewHolder = new MyViewHolder(view) ;
        viewHolder.viewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,MapsLocateActivity.class);
                // sending data process
                i.putExtra("name",mData.get(viewHolder.getAdapterPosition()).getName().toString());
                i.putExtra("lng",mData.get(viewHolder.getAdapterPosition()).getLng());
                i.putExtra("lat",mData.get(viewHolder.getAdapterPosition()).getLat());

                mContext.startActivity(i);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.MyViewHolder holder, int position) {

        holder.name.setText(String.valueOf(mData.get(position).getName()));
        holder.email.setText(String.valueOf(mData.get(position).getEmail()));
        holder.phone.setText(String.valueOf(mData.get(position).getPhone()));
        holder.status.setText(String.valueOf(mData.get(position).getStatus()));

        // Load Image from the internet and set it into Imageview using Glide

        Glide.with(mContext).load(mData.get(position).getImageURL()).into(holder.img);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public  void setItemClickListener(ItemClickListener itmClickListener){
        this.itemClickListener = itmClickListener;}

  public  static class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
      TextView name ;
      TextView email;
      ImageView img;
      TextView phone;
      TextView status;
      LinearLayout viewContainer;


        MyViewHolder(View itemView){
            super(itemView);
            viewContainer = itemView.findViewById(R.id.container);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
            img = itemView.findViewById(R.id.image_contact);
            status = itemView.findViewById(R.id.status);


        }


      @Override
      public void onClick(View v) {

      }
  }
}