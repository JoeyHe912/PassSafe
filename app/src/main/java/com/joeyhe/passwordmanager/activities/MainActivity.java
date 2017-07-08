package com.joeyhe.passwordmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.joeyhe.passwordmanager.R;
import com.joeyhe.passwordmanager.adapters.PasswordNoteAdapter;
import com.joeyhe.passwordmanager.db.DaoSession;
import com.joeyhe.passwordmanager.db.DatabaseHelper;
import com.joeyhe.passwordmanager.db.PasswordNote;
import com.joeyhe.passwordmanager.db.PasswordNoteDao;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

public class MainActivity extends AppCompatActivity implements OnQuickSideBarTouchListener {

    private final HashMap<String, Integer> letters = new HashMap<>();
    private final ArrayList<String> customLetters = new ArrayList<>();
    private RecyclerView rcyNotes;
    private PasswordNoteAdapter noteAdapter;
    private PasswordNoteDao noteDao;
    private Query<PasswordNote> notesQuery;
    private QuickSideBarView quickSideBarView;
    private QuickSideBarTipsView quickSideBarTipsView;
    private FloatingActionButton fabAdd;
    private List<PasswordNote> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        DaoSession daoSession = DatabaseHelper.getInstance().getDaoSession();
        noteDao = daoSession.getPasswordNoteDao();
        initView();
        notesQuery = noteDao.queryBuilder().orderAsc(PasswordNoteDao.Properties.NotFavorite,
                PasswordNoteDao.Properties.NotLetter, PasswordNoteDao.Properties.Name).build();
        updateNotes();
    }

    private void updateNotes() {
        notes = notesQuery.list();
        new Thread(new Runnable() {
            @Override
            public void run() {
                letters.clear();
                customLetters.clear();
                for (PasswordNote passwordNote : notes) {
                    String letter;
                    if (passwordNote.getNotFavorite()) {
                        if (passwordNote.getNotLetter()) {
                            letter = "#";
                        } else {
                            letter = passwordNote.getName().toUpperCase().charAt(0) + "";
                        }
                    } else {
                        letter = "☆";
                    }
                    if (!letters.containsKey(letter)) {
                        letters.put(letter, notes.indexOf(passwordNote));
                        customLetters.add(letter);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noteAdapter.setNotes(notes);
                        quickSideBarView.setLetters(customLetters);
                    }
                });
            }
        }).start();
    }

    protected void initView() {
        rcyNotes = (RecyclerView) findViewById(R.id.rcy_notes);
        rcyNotes.setLayoutManager(new LinearLayoutManager(this));
        PasswordNoteAdapter.NoteClickListener noteClickListener = new PasswordNoteAdapter.NoteClickListener() {
            @Override
            public void onNoteClick(int position) {
                PasswordNote note = noteAdapter.getNote(position);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ViewActivity.class);
                intent.putExtra("id", note.getId());
                startActivityForResult(intent, 0);
            }
        };
        noteAdapter = new PasswordNoteAdapter(noteClickListener);
        rcyNotes.setAdapter(noteAdapter);
        rcyNotes.addItemDecoration(new StickyHeaderDecoration(noteAdapter));
        quickSideBarView = (QuickSideBarView) findViewById(R.id.quickSideBarView);
        quickSideBarTipsView = (QuickSideBarTipsView) findViewById(R.id.quickSideBarTipsView);
        quickSideBarView.setOnQuickSideBarTouchListener(this);
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        rcyNotes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && fabAdd.isShown()) {
                    fabAdd.hide();
                }
                if (dy < 0 && !fabAdd.isShown()) {
                    fabAdd.show();
                }
            }
        });
    }

    public void clickAdd(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, StorageActivity.class);
        intent.putExtra("isEdit", false);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                updateNotes();
                break;
            default:
        }
    }

    @Override
    public void onLetterChanged(String letter, int position, float y) {
        quickSideBarTipsView.setText(letter, position, y);
        //有此key则获取位置并滚动到该位置
        if (letters.containsKey(letter)) {
            rcyNotes.scrollToPosition(letters.get(letter));
            LinearLayoutManager mLayoutManager =
                    (LinearLayoutManager) rcyNotes.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(letters.get(letter), 0);
        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
        quickSideBarTipsView.setVisibility(touching ? View.VISIBLE : View.INVISIBLE);
    }
}
