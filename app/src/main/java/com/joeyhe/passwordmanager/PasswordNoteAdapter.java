package com.joeyhe.passwordmanager;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joeyhe.passwordmanager.models.PasswordNote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HGY on 2017/6/27.
 */

public class PasswordNoteAdapter extends RecyclerView.Adapter<PasswordNoteAdapter.NoteViewHolder> {

    private NoteClickListener clickListener;
    private List<PasswordNote> dataset;

    public interface NoteClickListener {
        void onNoteClick(int position);
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView login;

        public NoteViewHolder(View itemView, final NoteClickListener clickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_name);
            login = itemView.findViewById(R.id.txt_login);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onNoteClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public PasswordNoteAdapter(NoteClickListener clickListener) {
        this.clickListener = clickListener;
        this.dataset = new ArrayList<>();
    }

    public void setNotes(@NonNull List<PasswordNote> notes) {
        dataset = notes;
        notifyDataSetChanged();
    }

    public PasswordNote getNote(int position) {
        return dataset.get(position);
    }

    @Override
    public PasswordNoteAdapter.NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(PasswordNoteAdapter.NoteViewHolder holder, int position) {
        PasswordNote note = dataset.get(position);
        holder.name.setText(note.getName());
        holder.login.setText(note.getUserName());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
