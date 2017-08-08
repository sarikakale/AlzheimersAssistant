package com.example.sarika.alzheimerassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.activity.RemindersActivity;
import com.example.sarika.alzheimerassistant.bean.Patient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sarika on 4/29/17.
 */

public class PatientArrayAdapter extends RecyclerView.Adapter<PatientArrayAdapter.PatientViewHolder> {

    LayoutInflater inflater;
    ArrayList<Patient> patientList = null;
    final OnItemClickListener listener;
    Context context;
    public PatientArrayAdapter(Context context, ArrayList<Patient> patientList,OnItemClickListener listener){
        this.context = context;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
        this.patientList = patientList;
    }

    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.patient_row,parent,false);
        PatientViewHolder viewHolder = new PatientViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PatientViewHolder holder, int position) {
       Patient patient = patientList.get(position);
       holder.patientName.setText(patient.getDetails().getName().toString());
        holder.bind(patient,listener);

    }


    @Override
    public int getItemCount() {
        return patientList.size();
    }

    class PatientViewHolder extends RecyclerView.ViewHolder{
        TextView patientName;

        public PatientViewHolder(View itemView) {
            super(itemView);
            patientName = (TextView) itemView.findViewById(R.id.patientRow);
        }



        public void bind(final Patient item, final OnItemClickListener listener) {
           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    listener.onItemClick(item);
               }
           });
        }
    }

    public interface OnItemClickListener {

        void onItemClick(Patient patient);

    }
}


