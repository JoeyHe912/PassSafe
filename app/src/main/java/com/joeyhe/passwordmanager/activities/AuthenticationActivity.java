package com.joeyhe.passwordmanager.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.joeyhe.passwordmanager.R;
import com.joeyhe.passwordmanager.db.DatabaseHelper;
import com.joeyhe.passwordmanager.utils.HashUtil;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AuthenticationActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("hasMasterPassword", false)) {
            setContentView(R.layout.activity_authentication);
        } else {
            Intent intent = new Intent();
            intent.setClass(AuthenticationActivity.this, MasterPasswordSettingActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void clickAuthenticate(View view) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Authenticating").setCancelable(false);
        pDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                EditText mpEditor = (EditText) findViewById(R.id.editor_mp);
                HashUtil hashUtil = new HashUtil(10000, 256, mpEditor.getText().toString());
                String iHash = hashUtil.getStringHash(sharedPreferences.getString("salt", null));
                if (sharedPreferences.getString("hash", null).equals(iHash)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setTitleText("Success!")
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }
                    });
                    DatabaseHelper.initDatabase(AuthenticationActivity.this, mpEditor.getText().toString());
                    Intent intent = new Intent();
                    intent.setClass(AuthenticationActivity.this, MainActivity.class);
                    intent.putExtra("MasterPassword", String.valueOf(mpEditor.getText()));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                    AuthenticationActivity.this.finish();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setTitleText("Oops...")
                                    .setContentText("Wrong Password")
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }
                    });
                }
            }
        }).start();
    }
}