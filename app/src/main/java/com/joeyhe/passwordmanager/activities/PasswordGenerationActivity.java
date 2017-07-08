package com.joeyhe.passwordmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.joeyhe.passwordmanager.R;
import com.joeyhe.passwordmanager.db.DatabaseHelper;
import com.joeyhe.passwordmanager.utils.PasswordGenerator;

import java.math.BigDecimal;
import java.math.BigInteger;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PasswordGenerationActivity extends AppCompatActivity
        implements NumberPickerDialogFragment.NumberPickerDialogHandlerV2{

    private PasswordGenerator pg;
    private TextView passwordView;
    private CheckBox upper;
    private CheckBox lower;
    private CheckBox number;
    private CheckBox symbol;
    private CheckBox similar;
    private EditText length;
    private SeekBar sbLength;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_generation);
        Intent intent = getIntent();
        pg = new PasswordGenerator(DatabaseHelper.getInstance().getMasterPass());
        pass = intent.getStringExtra("Pass");
        initView();
        renew();
        listenSeekBar();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initView() {
        upper = (CheckBox)findViewById(R.id.chk_upper);
        lower = (CheckBox)findViewById(R.id.chk_lower);
        number = (CheckBox)findViewById(R.id.chk_number);
        symbol = (CheckBox)findViewById(R.id.chk_symbol);
        similar = (CheckBox)findViewById(R.id.chk_excludeSimilar);
        length = (EditText)findViewById(R.id.txt_length);
        passwordView = (TextView) findViewById(R.id.txt_pg_password);
        sbLength = (SeekBar)findViewById(R.id.seek_length);
    }

    public void clickGenerate(View view){
        if (upper.isChecked() | lower.isChecked() | symbol.isChecked() | number.isChecked()) {
            renew();
        }else{
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Please include at least one type of characters.")
                    .show();
        }
    }

    private void renew(){
        String password = pg.generate(Integer.parseInt(length.getText().toString()),
                upper.isChecked(), lower.isChecked(), symbol.isChecked(), number.isChecked(), similar.isChecked());
        passwordView.setText(password);
    }

    private void listenSeekBar(){
        sbLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    length.setText(i+4+"");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        length.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment)
                        .setMaxNumber(new BigDecimal(64))
                        .setMinNumber(new BigDecimal(4));
                npb.show();
            }
        });
    }

    @Override
    public void onDialogNumberSet(int reference, BigInteger number, double decimal, boolean isNegative, BigDecimal fullNumber) {
        length.setText(String.valueOf(number));
        sbLength.setProgress(number.intValue()-4);
    }

    public void clickAccept(View view){
        Intent intent = new Intent();
        intent.putExtra("Pass", passwordView.getText());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra("Pass", pass);
        setResult(RESULT_OK, intent);
        finish();
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
}
