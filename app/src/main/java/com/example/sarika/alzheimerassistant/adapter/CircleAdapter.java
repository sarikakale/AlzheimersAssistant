package com.example.sarika.alzheimerassistant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.Circle;
import com.example.sarika.alzheimerassistant.fragments.CircleRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Siddhi Pai on 10-05-2017.
 */

public class CircleAdapter extends RecyclerView.Adapter<CircleAdapter.CircleViewHolder> {

    LayoutInflater inflater;
    ArrayList<Circle> caregiverList = null;
    Context context;
    final CircleAdapter.OnItemClickListener listener;
    String token;
    ArrayList<Caregiver> circleCaregivers = new ArrayList<>();


    public CircleAdapter(Context context, ArrayList<Circle> caregiverList, CircleAdapter.OnItemClickListener listener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.caregiverList = caregiverList;
        this.listener = listener;
    }

    @Override
    public CircleAdapter.CircleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_circle_row,parent,false);
        CircleAdapter.CircleViewHolder viewHolder = new CircleAdapter.CircleViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CircleAdapter.CircleViewHolder holder, int position) {
        Circle circle = caregiverList.get(position);
        String msg = null;
        Log.d("circle",circle.getCircleName().toString());
        holder.caregiverName.setText(circle.getCircleName().toString());
        holder.bind(circle,listener);
    }

    @Override
    public int getItemCount() {
        return caregiverList.size();
    }

    class CircleViewHolder extends RecyclerView.ViewHolder {

        TextView caregiverName;


        public CircleViewHolder(View itemView) {
            super(itemView);
            caregiverName = (TextView) itemView.findViewById(R.id.circleRow);
        }
        public void bind(final Circle circleName, final CircleAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(circleName);
                }
            });
        }
    }

    public interface OnItemClickListener {

        void onItemClick(Circle circleName);

    }
}
