package com.joeyhe.passwordmanager.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.joeyhe.passwordmanager.PasswordManager;
import com.joeyhe.passwordmanager.PasswordNoteAdapter;
import com.joeyhe.passwordmanager.R;
import com.joeyhe.passwordmanager.models.DaoSession;
import com.joeyhe.passwordmanager.models.PasswordNote;
import com.joeyhe.passwordmanager.models.PasswordNoteDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

public class MainPage extends AppCompatActivity {

    private RecyclerView rcyNotes;
    private PasswordNoteAdapter noteAdapter;
    private PasswordNoteDao noteDao;
    private Query<PasswordNote> notesQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DaoSession daoSession = ((PasswordManager) getApplication()).getDaoSession();
        noteDao = daoSession.getPasswordNoteDao();

        init();

        notesQuery = noteDao.queryBuilder().orderAsc(PasswordNoteDao.Properties.Name).build();
        updateNotes();
    }

    private void updateNotes() {
        List<PasswordNote> notes = notesQuery.list();
        Log.d("DaoExample", ""+notes.size());
        noteAdapter.setNotes(notes);
    }

    protected void init() {
        rcyNotes = (RecyclerView) findViewById(R.id.rcy_notes);
//        rcyNotes.setHasFixedSize(true);
        rcyNotes.setLayoutManager(new LinearLayoutManager(this));

        noteAdapter = new PasswordNoteAdapter(noteClickListener);
        rcyNotes.setAdapter(noteAdapter);
    }

    PasswordNoteAdapter.NoteClickListener noteClickListener = new PasswordNoteAdapter.NoteClickListener() {
        @Override
        public void onNoteClick(int position) {
            PasswordNote note = noteAdapter.getNote(position);
            Intent intent = new Intent();
            intent.setClass(MainPage.this,ViewPage.class);
            intent.putExtra("id",note.getId());
            startActivity(intent);
            updateNotes();
        }
    };

    public void clickAdd(View view){
        Intent intent = new Intent();
        intent.setClass(MainPage.this, StoragePage.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case 0:
                updateNotes();
                break;
            default:
        }
    }
}
