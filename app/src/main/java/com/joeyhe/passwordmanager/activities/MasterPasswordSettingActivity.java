package com.joeyhe.passwordmanager.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.joeyhe.passwordmanager.R;
import com.joeyhe.passwordmanager.db.DatabaseHelper;
import com.joeyhe.passwordmanager.utils.HashUtil;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MasterPasswordSettingActivity extends AppCompatActivity {

    private boolean isInitialising;
    private TextInputLayout tilEnter;
    private TextInputLayout tilConfirm;
    private EditText edtEnter;
    private EditText edtConfirm;
    private MenuItem itemSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInitialising = getIntent().getBooleanExtra("isInitialising", false);
        setTitle(isInitialising ? "Set Master Password" : "Change Master Password");
        setContentView(R.layout.activity_master_password_setting);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && !isInitialising) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_check, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        itemSave = menu.getItem(0);
        itemSave.setEnabled(tilEnter.getError() == null && !edtConfirm.getText().toString().isEmpty() && tilConfirm.getError() == null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.setTitleText(isInitialising ? "Initialising..." : "Resetting...").setCancelable(false);
                pDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                        HashUtil hashUtil = new HashUtil(10000, 256, edtEnter.getText().toString());
                        if (isInitialising) {
                            DatabaseHelper.initDatabase(MasterPasswordSettingActivity.this, edtEnter.getText().toString());
                        } else {
                            DatabaseHelper.getInstance().changeMasterPass(edtEnter.getText().toString());
                        }
                        sharedPreferences.edit()
                                .putString("hash", hashUtil.getStringHash())
                                .putString("salt", hashUtil.getSalt())
                                .putBoolean("hasMasterPassword", true)
                                .apply();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pDialog.setTitleText("Success!")
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        });
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (isInitialising) {
                            Intent intent = new Intent();
                            intent.setClass(MasterPasswordSettingActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        MasterPasswordSettingActivity.this.finish();
                    }
                }).start();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {
        tilEnter = (TextInputLayout) findViewById(R.id.til_enter);
        tilConfirm = (TextInputLayout) findViewById(R.id.til_confirm);
        edtEnter = (EditText) findViewById(R.id.edt_enter);
        edtConfirm = (EditText) findViewById(R.id.edt_confirm);
        tilEnter.setHint(isInitialising ? "Enter Master Password" : "Enter New Master Password");
        tilEnter.setError(getError(""));
        edtEnter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilEnter.setError(getError(charSequence));
                if (!edtConfirm.getText().toString().isEmpty()) {
                    if (edtEnter.getText().toString().equals(edtConfirm.getText().toString())) {
                        tilConfirm.setError(null);
                    } else {
                        tilConfirm.setError("Master Passwords should match.");
                    }
                }
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtEnter.getText().toString().equals(edtConfirm.getText().toString())) {
                    tilConfirm.setError(null);
                } else {
                    tilConfirm.setError("Master Password should match.");
                }
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private StringBuilder getError(CharSequence charSequence) {
        StringBuilder errorMsg = new StringBuilder("Master Password must\n");
        if (charSequence.length() < 8) {
            errorMsg.append("be at least 8 characters,\n");
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        for (int i = 0; i < charSequence.length(); i++) {
            hasUpper = hasUpper || Character.isUpperCase(charSequence.charAt(i));
            hasLower = hasLower || Character.isLowerCase(charSequence.charAt(i));
            hasDigit = hasDigit || Character.isDigit(charSequence.charAt(i));
            hasSpecial = hasSpecial || !Character.isLetterOrDigit(charSequence.charAt(i));
        }
        if (!hasUpper) {
            errorMsg.append("contain at least an uppercase letter,\n");
        }
        if (!hasLower) {
            errorMsg.append("contain at least a lowercase letter,\n");
        }
        if (!hasDigit) {
            errorMsg.append("contain at least an digit,\n");
        }
        if (!hasSpecial) {
            errorMsg.append("contain at least a special character,\n");
        }
        if (charSequence.length() >= 8 && hasUpper && hasLower && hasDigit && hasSpecial) {
            return null;
        } else {
            errorMsg.setLength(errorMsg.length() - 2);
            return errorMsg;
        }
    }
}
