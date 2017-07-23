package com.joeyhe.passwordmanager.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joeyhe.passwordmanager.R;
import com.joeyhe.passwordmanager.db.PasswordNote;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

/**
 * Created by HGY on 2017/6/27.
 */

public class PasswordNoteAdapter extends RecyclerView.Adapter<PasswordNoteAdapter.NoteViewHolder> implements
        StickyHeaderAdapter<PasswordNoteAdapter.HeaderHolder> {

    private NoteClickListener clickListener;
    private List<PasswordNote> dataset;

    public PasswordNoteAdapter(NoteClickListener clickListener) {
        this.clickListener = clickListener;
        this.dataset = new ArrayList<>();

    }

    @Override
    public long getHeaderId(int position) {
        PasswordNote passwordNote = dataset.get(position);
        if (passwordNote.getNotFavorite()) {
            if (passwordNote.getNotLetter()) {
                return 1000;
            } else {
                return passwordNote.getName().toUpperCase().charAt(0);
            }
        } else {
            return 2000;
        }
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_header, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {
        if (getHeaderId(position) == 2000) {
            viewholder.header.setText("☆");
        } else {
            if (getHeaderId(position) == 1000) {
                viewholder.header.setText("#");
            } else {
                viewholder.header.setText(String.format("%s", (char) getHeaderId(position)));
            }
        }
        viewholder.header.setBackgroundColor(getRandomColor());
    }

    private int getRandomColor() {
        SecureRandom random = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                random.nextInt(359), 1, 1
        });
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

    static class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView header;

        public HeaderHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView;
        }
    }
}
