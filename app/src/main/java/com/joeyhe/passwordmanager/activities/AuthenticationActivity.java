package com.joeyhe.passwordmanager.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.joeyhe.passwordmanager.R;
import com.joeyhe.passwordmanager.db.DatabaseHelper;
import com.joeyhe.passwordmanager.utils.DataCleanUtil;
import com.joeyhe.passwordmanager.utils.HashUtil;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AuthenticationActivity extends AppCompatActivity {

    public static final int GET_NOTE = 0;
    public static final int OPEN_GENERATOR = 1;
    public static final int SAVE_NOTE = 2;

    private SharedPreferences sharedPreferences;
    private EditText edtMp;
    private int action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("hasMasterPassword", false)) {
            setContentView(R.layout.activity_authentication);
            edtMp = (EditText) findViewById(R.id.edt_authentication_mp);
            edtMp.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        authenticate();
                        return true;
                    }
                    return false;
                }
            });
            action = getIntent().getIntExtra("action", -1);
            if (action != -1) {
                reformView();
            }
        } else {
            Intent intent = new Intent();
            intent.setClass(AuthenticationActivity.this, MasterPasswordSettingActivity.class);
            intent.putExtra("isInitialising", true);
            startActivity(intent);
            finish();
        }
    }

    public void clickAuthenticate(View view) {
        authenticate();
    }

    private void authenticate() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Authenticating").setCancelable(false);
        pDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashUtil hashUtil = new HashUtil(10000, 256, edtMp.getText().toString());
                String iHash = hashUtil.getStringHash(sharedPreferences.getString("salt", null));
                if (iHash.equals(sharedPreferences.getString("hash", null))) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.setTitleText("Success!")
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }
                    });
                    DatabaseHelper.initDatabase(AuthenticationActivity.this, edtMp.getText().toString());
                    sharedPreferences.edit().putInt("wrongTimes", 0).apply();
                    Intent intent = new Intent();
                    switch (action) {
                        case OPEN_GENERATOR:
                            intent.setClass(AuthenticationActivity.this, PasswordGenerationActivity.class);
                            intent.putExtra("isFromIME", true);
                            break;
                        case SAVE_NOTE:
                            intent.setClass(AuthenticationActivity.this, StorageActivity.class);
                            intent.putExtra("packageName", getIntent().getStringExtra("packageName"));
                            intent.putExtra("isFromIME", true);
                            break;
                        case GET_NOTE:
                            intent.putExtra("packageName", getIntent().getStringExtra("packageName"));
                        default:
                            intent.setClass(AuthenticationActivity.this, MainActivity.class);
                            intent.putExtra("isFromIME", action == GET_NOTE);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pDialog.dismiss();
                    startActivity(intent);
                    AuthenticationActivity.this.finish();
                } else {
                    final int wrongTimes = sharedPreferences.getInt("wrongTimes", 0) + 1;
                    final String wrongMsg;
                    if (wrongTimes == 5) {
                        sharedPreferences.edit().clear().apply();
                        DataCleanUtil.cleanApplicationData(getApplicationContext());
                        wrongMsg = "5 wrong tries! All data has been eliminated";
                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                            }
                        });
                    } else {
                        sharedPreferences.edit().putInt("wrongTimes", wrongTimes).apply();
                        wrongMsg = "5 wrong tries will cause elimination of all data!";
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextInputLayout) findViewById(R.id.til_authentication))
                                    .setError(wrongTimes != 4 ?
                                            "You have " + (5 - wrongTimes) + " tries left."
                                            : "Warning! You only have 1 try left!");
                            pDialog.setTitleText("Wrong Password")
                                    .setContentText(wrongMsg)
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }
                    });
                }
            }
        }).start();
    }

    private void reformView() {
        setTitle("Enter your master password.");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
