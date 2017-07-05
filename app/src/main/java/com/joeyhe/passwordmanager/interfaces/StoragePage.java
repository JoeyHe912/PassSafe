package com.joeyhe.passwordmanager.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.joeyhe.passwordmanager.PasswordManager;
import com.joeyhe.passwordmanager.R;
import com.joeyhe.passwordmanager.models.DaoSession;
import com.joeyhe.passwordmanager.models.PasswordNote;
import com.joeyhe.passwordmanager.models.PasswordNoteDao;

import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StoragePage extends AppCompatActivity {

    private String masterPass;
    private TextView name;
    private TextView website;
    private TextView login;
    private TextView pass;
    private TextView note;
    private PasswordNoteDao noteDao;
    private PasswordNote passNote;
    private boolean isEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_page);
        init();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        DaoSession daoSession = ((PasswordManager) getApplication()).getDaoSession();
        noteDao = daoSession.getPasswordNoteDao();
        Intent intent = getIntent();
        isEdit = intent.getBooleanExtra("isEdit", false);
        masterPass = intent.getStringExtra("masterPass" );
        if (isEdit) {
            setTitle("Editing" );
            long id = intent.getLongExtra("id", 1);
            passNote = noteDao.load(id);
            setValues();
        } else {
            setTitle("Adding" );
            passNote = new PasswordNote();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_storage_page, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                if (name.getText().toString().isEmpty()) {
                    new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops..." )
                            .setContentText("Please give a name for this note" )
                            .show();
                } else {
                    passNote.setName(name.getText().toString());
                    passNote.setWebSite(website.getText().toString());
                    passNote.setUserName(login.getText().toString());
                    passNote.setPassword(pass.getText().toString());
                    passNote.setNote(note.getText().toString());
                    passNote.setModifiedDate(new Date());
                    if (isEdit) {
                        noteDao.update(passNote);
                    } else {
                        passNote.setCreatedDate(new Date());
                        passNote.setAccessedDate(new Date());
                        noteDao.insert(passNote);
                    }
                    setResult(0);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        name = (TextView)findViewById((R.id.edt_storage_name));
        website = (TextView)findViewById((R.id.edt_storage_website));
        login = (TextView)findViewById((R.id.edt_storage_login));
        pass = (TextView) findViewById(R.id.edt_storage_password);
        note = (TextView)findViewById((R.id.edt_storage_note));
    }

    private void setValues() {
        name.setText(passNote.getName());
        website.setText(passNote.getWebSite());
        login.setText(passNote.getUserName());
        pass.setText(passNote.getPassword());
        note.setText(passNote.getNote());
    }

    public void clickDice(View view) {
        Intent intent = new Intent();
        intent.setClass(StoragePage.this, PasswordGeneratorPage.class);
        intent.putExtra("MasterPassword", masterPass);
        intent.putExtra("Pass", pass.getText().toString());
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case 0:
                pass.setText(data.getStringExtra("Pass"));
                break;
            default:
        }
    }

    @Override
    public void onBackPressed(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("You will exit without saving." )
                .setConfirmText("Yes" )
                .setCancelText("No" )
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        StoragePage.this.setResult(-1);
                        StoragePage.this.finish();
                    }
                })
                .show();
    }
}