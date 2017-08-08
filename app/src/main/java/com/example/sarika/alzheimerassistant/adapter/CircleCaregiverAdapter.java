package com.example.sarika.alzheimerassistant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.NavigationItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sarika on 3/27/17.
 */
public class CircleCaregiverAdapter extends RecyclerView.Adapter<CircleCaregiverAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<Caregiver> caregiverList = null;
    Context context;

    public CircleCaregiverAdapter(Context context, ArrayList<Caregiver> caregiverList){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.caregiverList = caregiverList;
    }

    @Override
    public CircleCaregiverAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_notifications,parent,false);
            ViewHolder holder = new ViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(CircleCaregiverAdapter.ViewHolder holder, int position) {
        Caregiver caregiver = caregiverList.get(position);
        String address = null;
        if(caregiver.getDetails() != null) {
            holder.caregiverName.setText(caregiver.getDetails().getName().toString());
            holder.caregiverEmail.setText(caregiver.getDetails().getEmail());
            holder.phone.setText(caregiver.getDetails().getPhoneNumber());
            if(caregiver.getDetails().getAddress()!=null) {
                address = caregiver.getDetails().getAddress().getStreet() + " "
                            + caregiver.getDetails().getAddress().getCity() + " "
                            + caregiver.getDetails().getAddress().getState() +" "
                            + caregiver.getDetails().getAddress().getCountry() + " "
                            + caregiver.getDetails().getAddress().getZipcode();
                holder.address.setText(address);
            }
            Picasso.with(context).load(caregiver.getFacialId().getImageUrl()).resize(64,50).into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return caregiverList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cName)
        TextView caregiverName;
        @BindView(R.id.cEmail)
        TextView caregiverEmail;
        @BindView(R.id.cPhone)
        TextView phone;
        @BindView(R.id.cAddress)
        TextView address;
        @BindView(R.id.cImage)
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

}

