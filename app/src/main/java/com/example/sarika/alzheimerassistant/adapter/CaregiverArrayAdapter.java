package com.example.sarika.alzheimerassistant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.Patient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sarika on 5/5/17.
 */

public class CaregiverArrayAdapter extends RecyclerView.Adapter<CaregiverArrayAdapter.CaregiverViewHolder> {

    LayoutInflater inflater;
    ArrayList<Caregiver> caregiverList = null;
    final CaregiverArrayAdapter.OnItemClickListener listener;
    Context context;

    public CaregiverArrayAdapter(Context context, ArrayList<Caregiver> caregiverList, CaregiverArrayAdapter.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
        this.caregiverList = caregiverList;
    }

    @Override
    public CaregiverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.caregiver_row,parent,false);
        CaregiverArrayAdapter.CaregiverViewHolder viewHolder = new CaregiverArrayAdapter.CaregiverViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CaregiverViewHolder holder, int position) {
        Caregiver caregiver = caregiverList.get(position);
        String msg = null;
        if(caregiver.getDetails() != null) {
            msg = caregiver.getDetails().getName().toString();

            holder.caregiverName.setText(msg);
            Picasso.with(context).load(caregiver.getFacialId().getImageUrl()).resize(64,50).into(holder.imageView);
            holder.bind(caregiver, listener);
        }

    }

    @Override
    public int getItemCount() {
        return caregiverList.size();
    }

    class CaregiverViewHolder extends RecyclerView.ViewHolder {

    TextView caregiverName;
    ImageView imageView;
    public CaregiverViewHolder(View itemView) {
        super(itemView);
        caregiverName = (TextView) itemView.findViewById(R.id.caregiverRow);
        imageView = (ImageView) itemView.findViewById(R.id.icon);
    }
    public void bind(final Caregiver item, final CaregiverArrayAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item);
            }
        });
    }
}

    public interface OnItemClickListener {

        void onItemClick(Caregiver caregiver);

    }
}

