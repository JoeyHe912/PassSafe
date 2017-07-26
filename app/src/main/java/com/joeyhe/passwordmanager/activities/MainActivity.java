package com.joeyhe.passwordmanager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.joeyhe.passwordmanager.PasswordManager;
import com.joeyhe.passwordmanager.R;
import com.joeyhe.passwordmanager.adapters.PasswordNoteAdapter;
import com.joeyhe.passwordmanager.db.DaoSession;
import com.joeyhe.passwordmanager.db.DatabaseHelper;
import com.joeyhe.passwordmanager.db.PasswordNote;
import com.joeyhe.passwordmanager.db.PasswordNoteDao;
import com.joeyhe.passwordmanager.fragments.PromptDialog;

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
    private QuickSideBarView quickSideBarView;
    private QuickSideBarTipsView quickSideBarTipsView;
    private FloatingActionButton fabAdd;

    private PasswordNoteDao noteDao;
    private Query<PasswordNote> notesQuery;
    private List<PasswordNote> notes;
    private PasswordManager pm;
    private boolean isFromIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        isFromIME = getIntent().getBooleanExtra("isFromIME", false);
        pm = (PasswordManager) getApplication();
        DaoSession daoSession = DatabaseHelper.getInstance().getDaoSession();
        noteDao = daoSession.getPasswordNoteDao();
        initView();
        notesQuery = noteDao.queryBuilder().orderAsc(PasswordNoteDao.Properties.NotFavorite,
                PasswordNoteDao.Properties.NotLetter, PasswordNoteDao.Properties.Name).build();
        updateNotes();
        if (isFromIME) {
            reformView();
        }
    }

    private void reformView() {
        setTitle("Select an account.");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        String packageName = getIntent().getStringExtra("packageName");
        if (packageName != null) {
            final List<PasswordNote> notes = noteDao.queryBuilder()
                    .where(PasswordNoteDao.Properties.PackageName.eq(packageName)).build().list();
            if (!notes.isEmpty()) {
                PromptDialog dialog = new PromptDialog();
                dialog.show(notes, getSupportFragmentManager(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PasswordNote note = notes.get(i);
                                pm.setLogin(note.getUserName());
                                pm.setPassword(note.getPassword());
                                MainActivity.this.finish();
                            }
                        });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isFromIME) {
            getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.action_change:
                intent.setClass(MainActivity.this, MasterPasswordSettingActivity.class);
                intent.putExtra("isInitialising", false);
                startActivity(intent);
                return true;
            case R.id.action_enable:
                intent.setClass(MainActivity.this, EnableAutofillActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                if (isFromIME) {
                    pm.setLogin(note.getUserName());
                    pm.setPassword(note.getPassword());
                    note.setPackageName(getIntent().getStringExtra("packageName"));
                    noteDao.update(note);
                    finish();
                } else {
                    intent.setClass(MainActivity.this, ViewActivity.class);
                    intent.putExtra("id", note.getId());
                    startActivityForResult(intent, 0);
                }
            }
        };
        noteAdapter = new PasswordNoteAdapter(noteClickListener);
        rcyNotes.setAdapter(noteAdapter);
        rcyNotes.addItemDecoration(new StickyHeaderDecoration(noteAdapter));
        quickSideBarView = (QuickSideBarView) findViewById(R.id.quickSideBarView);
        quickSideBarTipsView = (QuickSideBarTipsView) findViewById(R.id.quickSideBarTipsView);
        quickSideBarView.setOnQuickSideBarTouchListener(this);
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        if (isFromIME) {
            fabAdd.hide();
        } else {
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
