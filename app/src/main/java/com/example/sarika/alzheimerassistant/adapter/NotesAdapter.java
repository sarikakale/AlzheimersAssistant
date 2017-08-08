package com.example.sarika.alzheimerassistant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.Notes;
import com.example.sarika.alzheimerassistant.bean.Patient;
import com.example.sarika.alzheimerassistant.fragments.NoteRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sarika on 5/5/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesHolder> {

    LayoutInflater inflater;
    ArrayList<Notes> notesList = null;
    Context context;
    NoteRow nr = new NoteRow();
    String token;



    public NotesAdapter(Context context, ArrayList<Notes> notesList, String token) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.notesList = notesList;
        this.token = token;
    }

    @Override
    public NotesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_note_row,parent,false);
        NotesAdapter.NotesHolder viewHolder = new NotesAdapter.NotesHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotesHolder holder, int position) {
        Notes note = notesList.get(position);
        Log.d("Note",note.toString());
            holder.NoteText.setText(note.getNote());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nr.deleteNotesRequest(note.getId(), token);
                notesList.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    class NotesHolder extends RecyclerView.ViewHolder {

        TextView NoteText;
        Button delete;

        public NotesHolder(View itemView) {
            super(itemView);
            NoteText = (TextView) itemView.findViewById(R.id.NoteRow);
            delete = (Button) itemView.findViewById(R.id.delete_notes);
        }
    }
}

