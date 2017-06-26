package com.joeyhe.passwordmanager.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.joeyhe.passwordmanager.R;
import com.maksim88.passwordedittext.PasswordEditText;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class InformationStorageInterface extends AppCompatActivity {

    private PasswordEditText pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_storage_interface);
        init();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void init(){
        pass = (PasswordEditText)findViewById(R.id.editText_password);
    }

    public void clickDice(View view){
        Intent intent = new Intent();
        intent.setClass(InformationStorageInterface.this, PasswordGeneratorInterface.class);
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
                        InformationStorageInterface.this.finish();
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
}