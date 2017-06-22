package com.joeyhe.passwordmanager.Interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.joeyhe.passwordmanager.R;
import com.joeyhe.passwordmanager.models.PasswordGenerator;

import java.math.BigDecimal;
import java.math.BigInteger;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PasswordGeneratorInterface extends AppCompatActivity
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_generator_interface);
        Intent intent = getIntent();
        pg = new PasswordGenerator(intent.getStringExtra("MasterPassword"));
        init();
        passwordView.setText(intent.getStringExtra("MasterPassword"));
        listenSeekBar();
    }

    private void init(){
        upper = (CheckBox)findViewById(R.id.check_upper);
        lower = (CheckBox)findViewById(R.id.check_lower);
        number = (CheckBox)findViewById(R.id.check_number);
        symbol = (CheckBox)findViewById(R.id.check_symbol);
        similar = (CheckBox)findViewById(R.id.check_excludeSimilar);
        length = (EditText)findViewById(R.id.editor_length);
        passwordView = (TextView) findViewById(R.id.view_password);
        sbLength = (SeekBar)findViewById(R.id.seekBar_length);
    }

    public void clickGenerate(View view){
        if (upper.isChecked() | lower.isChecked() | symbol.isChecked() | number.isChecked()) {
            String password = pg.generate(Integer.parseInt(length.getText().toString()),
                    upper.isChecked(), lower.isChecked(), symbol.isChecked(), number.isChecked(), similar.isChecked());
            passwordView.setText(password);
        }
        else{
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Please check at lease one of the options above.")
                    .show();
        }
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
}
