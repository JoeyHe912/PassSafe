package com.joeyhe.passwordmanager.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.joeyhe.passwordmanager.db.PasswordNote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HGY on 2017/7/21.
 */

public class PromptDialog extends DialogFragment {

    private String[] names;
    private DialogInterface.OnClickListener onClickListener;

    public void show(List<PasswordNote> notes, FragmentManager fragmentManager,
                     DialogInterface.OnClickListener onClickListener) {
        ArrayList<String> nameList = new ArrayList<>();
        for (PasswordNote note : notes) {
            nameList.add(note.getName());
        }
        names = nameList.toArray(new String[nameList.size()]);
        this.onClickListener = onClickListener;
        show(fragmentManager, "PromptDialog");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Looking for this?");
        builder.setNegativeButton("other", null);
        builder.setItems(names, onClickListener);
        return builder.create();
    }
}
