package com.joeyhe.passwordmanager.services;

import android.content.Context;
import android.graphics.Canvas;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

/**
 * Created by HGY on 2017/7/18.
 */

public class MyKeyboardView extends KeyboardView {

    public MyKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getKeyboard().getKeys();
    }
}
