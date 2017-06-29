package com.joeyhe.passwordmanager.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.joeyhe.passwordmanager.PasswordManager;
import com.joeyhe.passwordmanager.R;
import com.joeyhe.passwordmanager.models.DaoSession;
import com.joeyhe.passwordmanager.models.PasswordNote;
import com.joeyhe.passwordmanager.models.PasswordNoteDao;
import com.maksim88.passwordedittext.PasswordEditText;

import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StoragePage extends AppCompatActivity {

    private TextView name;
    private TextView website;
    private TextView login;
    private PasswordEditText pass;
    private TextView note;
    private PasswordNoteDao noteDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_page);
        init();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        DaoSession daoSession = ((PasswordManager) getApplication()).getDaoSession();
        noteDao = daoSession.getPasswordNoteDao();
    }

    private void init(){
        name = (TextView)findViewById((R.id.edt_storage_name));
        website = (TextView)findViewById((R.id.edt_storage_website));
        login = (TextView)findViewById((R.id.edt_storage_login));
        pass = (PasswordEditText)findViewById(R.id.edt_storage_password);
        note = (TextView)findViewById((R.id.edt_storage_note));

    }

    public void clickDice(View view){
        Intent intent = new Intent();
        intent.setClass(StoragePage.this, PasswordGeneratorPage.class);
        intent.putExtra("MasterPassword", "mp");
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
                .setContentText("You will exit without saving it.")
                .setConfirmText("Yes,drop it!")
                .setCancelText("No,cancel plx!")
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickCancel(View view){
        onBackPressed();
    }

    public void clickSave(View view){
        PasswordNote passNote = new PasswordNote();
        passNote.setName(name.getText().toString());
        passNote.setWebSite(website.getText().toString());
        passNote.setUserName(login.getText().toString());
        passNote.setPassword(pass.getText().toString());
        passNote.setNote(note.getText().toString());
        passNote.setCreatedDate(new Date());
        passNote.setModifiedDate(new Date());
        passNote.setAccessedDate(new Date());
        noteDao.insert(passNote);
        Log.d("DaoExample", "Inserted new note, ID: " + passNote.getId() + " Pass: " + passNote.getPassword());
        setResult(1);
        finish();
    }
}