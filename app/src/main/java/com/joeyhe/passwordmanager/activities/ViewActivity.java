package com.joeyhe.passwordmanager.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Toast;

import com.joeyhe.passwordmanager.PasswordManager;
import com.joeyhe.passwordmanager.R;
import com.joeyhe.passwordmanager.db.DaoSession;
import com.joeyhe.passwordmanager.db.DatabaseHelper;
import com.joeyhe.passwordmanager.db.PasswordNote;
import com.joeyhe.passwordmanager.db.PasswordNoteDao;

import java.text.DateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ViewActivity extends AppCompatActivity {

    private EditText website;
    private EditText login;
    private EditText password;
    private EditText note;
    private EditText created;
    private EditText modified;
    private EditText accessed;
    private PasswordNoteDao noteDao;
    private PasswordNote passNote;
    private boolean notFavorite;
    private int resultCode = RESULT_CANCELED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        DaoSession daoSession = DatabaseHelper.getInstance().getDaoSession();
        noteDao = daoSession.getPasswordNoteDao();
        initView();
        setValues();
        updateAccessed();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(notFavorite ? android.R.drawable.btn_star_big_off : android.R.drawable.btn_star_big_on);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notFavorite = !notFavorite;
                                fab.setImageResource(notFavorite ? android.R.drawable.btn_star_big_off : android.R.drawable.btn_star_big_on);
                                passNote.setNotFavorite(notFavorite);
                                noteDao.update(passNote);
                                resultCode = RESULT_OK;
                            }
                        });
                    }
                }).start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent();
                intent.setClass(ViewActivity.this, StorageActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("id", passNote.getId());
                startActivityForResult(intent, 0);
                return true;
            case R.id.action_delete:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("You will delete this note permanently.")
                        .setConfirmText("Yes,delete it!")
                        .setCancelText("No,cancel plx!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                noteDao.delete(passNote);
                                resultCode = RESULT_OK;
                                onBackPressed();
                            }
                        })
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            this.resultCode = resultCode;
            ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(passNote.getName());
            website.setText(passNote.getWebSite());
            login.setText(passNote.getUserName());
            password.setText(passNote.getPassword());
            note.setText(passNote.getNote());
            DateFormat dateFormat = DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, DateFormat.MEDIUM);
            modified.setText(dateFormat.format(passNote.getModifiedDate()));
        }
    }

    private void initView() {
        website = (EditText) findViewById(R.id.edt_view_website);
        login = (EditText) findViewById(R.id.edt_view_login);
        password = (EditText) findViewById(R.id.edt_view_password);
        note = (EditText) findViewById(R.id.edt_view_note);
        created = (EditText) findViewById(R.id.edt_view_created);
        modified = (EditText) findViewById(R.id.edt_view_modified);
        accessed = (EditText) findViewById(R.id.edt_view_accessed);
        Intent intent = getIntent();
        long id = intent.getLongExtra("id", 1);
        passNote = noteDao.load(id);
        notFavorite = passNote.getNotFavorite();
    }

    private void setValues() {
        setTitle(passNote.getName());
        website.setText(passNote.getWebSite());
        login.setText(passNote.getUserName());
        password.setText(passNote.getPassword());
        note.setText(passNote.getNote());
        DateFormat dateFormat = DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, DateFormat.MEDIUM);
        created.setText(dateFormat.format(passNote.getCreatedDate()));
        modified.setText(dateFormat.format(passNote.getModifiedDate()));
        accessed.setText(dateFormat.format(passNote.getAccessedDate()));
    }

    private void updateAccessed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                passNote.setAccessedDate(new Date());
                noteDao.update(passNote);
            }
        }).start();
    }

    public void clickOpen(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(URLUtil.guessUrl(passNote.getWebSite()));
        intent.setData(uri);
        startActivity(intent);
    }

    public void clickCopy(View view) {
        PasswordManager pm = (PasswordManager) getApplication();
        switch (view.getId()) {
            case R.id.btn_copyLogin:
                pm.setLogin(login.getText().toString());
                break;
            case R.id.btn_copyPass:
                pm.setPassword(password.getText().toString());
                break;
            default:
        }
        Toast.makeText(this, "Copied to PassSafe Secure Keyboard!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        setResult(resultCode);
        finish();
    }
}
