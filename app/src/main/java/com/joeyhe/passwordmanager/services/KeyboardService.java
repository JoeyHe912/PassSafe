package com.joeyhe.passwordmanager.services;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import com.joeyhe.passwordmanager.R;

public class KeyboardService extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView keyboardView;
    private Keyboard keyboard_qwerty;
    private Keyboard keyboard_symbols;
    private Keyboard keyboard_symbols_alt;

    private boolean caps = false;
    private boolean sym = false;
    private boolean symalt = false;

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard_qwerty = new Keyboard(this, R.xml.qwerty);
        keyboard_symbols = new Keyboard(this, R.xml.symbols);
        keyboard_symbols_alt = new Keyboard(this, R.xml.symbols_alt);
        keyboardView.setKeyboard(keyboard_qwerty);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    @Override
    public void onPress(int i) {

    }

    @Override
    public void onRelease(int i) {

    }

    @Override
    public void onKey(int i, int[] ints) {
        InputConnection ic = getCurrentInputConnection();
        switch (i) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboard_qwerty.setShifted(caps);
                keyboardView.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                sym = !sym;
                keyboardView.setKeyboard(sym ? keyboard_symbols : keyboard_qwerty);
                symalt = false;
                break;
            case Keyboard.KEYCODE_ALT:
                symalt = !symalt;
                keyboardView.setKeyboard(symalt ? keyboard_symbols_alt : keyboard_symbols);
                break;
            default:
                char code = (char) i;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
        }
    }

    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
