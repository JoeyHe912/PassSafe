package com.joeyhe.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.joeyhe.passwordmanager.interfaces.InformationStorageInterface;
import com.joeyhe.passwordmanager.interfaces.PasswordGeneratorInterface;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickPG(View view){
        EditText mpEditor = (EditText)findViewById(R.id.editor_mp);
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, PasswordGeneratorInterface.class);
        intent.putExtra("MasterPassword", String.valueOf(mpEditor.getText()));
        startActivity(intent);
    }

    public void clickAdd(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, InformationStorageInterface.class);
        startActivity(intent);
    }
}
