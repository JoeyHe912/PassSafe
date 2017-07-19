package com.joeyhe.passwordmanager.services;

import android.content.Context;
import android.inputmethodservice.Keyboard;

/**
 * Created by HGY on 2017/7/18.
 */

public class MyKeyboard extends Keyboard {
    public MyKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }
}
