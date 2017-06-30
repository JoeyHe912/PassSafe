package com.joeyhe.passwordmanager.interfaces;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Toast;

import com.joeyhe.passwordmanager.PasswordManager;
import com.joeyhe.passwordmanager.R;
import com.joeyhe.passwordmanager.models.DaoSession;
import com.joeyhe.passwordmanager.models.PasswordNote;
import com.joeyhe.passwordmanager.models.PasswordNoteDao;

import java.text.DateFormat;
import java.util.Date;

public class ViewPage extends AppCompatActivity {

    private EditText website;
    private EditText login;
    private EditText password;
    private EditText note;
    private EditText created;
    private EditText modified;
    private EditText accessed;
    private PasswordNoteDao noteDao;
    private PasswordNote passNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DaoSession daoSession = ((PasswordManager) getApplication()).getDaoSession();
        noteDao = daoSession.getPasswordNoteDao();
        init();
        setValues();
        updateAccessed();
    }

    private void init() {
        website = (EditText)findViewById(R.id.edt_view_website);
        login = (EditText)findViewById(R.id.edt_view_login);
        password = (EditText)findViewById(R.id.edt_view_password);
        note = (EditText)findViewById(R.id.edt_view_note);
        created = (EditText)findViewById(R.id.edt_view_created);
        modified = (EditText)findViewById(R.id.edt_view_modified);
        accessed = (EditText)findViewById(R.id.edt_view_accessed);
        Intent intent = getIntent();
        long id = intent.getLongExtra("id",1);
        passNote = noteDao.queryBuilder().where(PasswordNoteDao.Properties.Id.eq(id)).build().unique();
    }

    private void setValues() {
        setTitle(passNote.getName());
        website.setText(passNote.getWebSite());
        login.setText(passNote.getUserName());
        password.setText(passNote.getPassword());
        note.setText(passNote.getNote());
        DateFormat dateFormat = DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM,DateFormat.MEDIUM);
        created.setText(dateFormat.format(passNote.getCreatedDate()));
        modified.setText(dateFormat.format(passNote.getModifiedDate()));
        accessed.setText(dateFormat.format(passNote.getAccessedDate()));
    }

    private void updateAccessed() {
        passNote.setAccessedDate(new Date());
        noteDao.update(passNote);
    }

    public void clickOpen(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(URLUtil.guessUrl(passNote.getWebSite()));
        intent.setData(uri);
        startActivity(intent);
    }

    public void clickCopy(View view) {
        ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = null;
        switch (view.getId()) {
            case R.id.btn_copyLogin:
                clipData = ClipData.newPlainText("login", login.getText());
                break;
            case R.id.btn_copyPass:
                clipData = ClipData.newPlainText("pass", password.getText());
                break;
            default:
        }
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this,"Copied!",Toast.LENGTH_SHORT).show();
    }
}
