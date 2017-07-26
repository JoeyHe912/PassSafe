package com.joeyhe.passwordmanager.services;

import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import com.joeyhe.passwordmanager.PasswordManager;
import com.joeyhe.passwordmanager.R;
import com.joeyhe.passwordmanager.activities.AuthenticationActivity;
import com.joeyhe.passwordmanager.utils.ProcessDetectUtil;

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
    public void onPress(final int i) {

    }

    @Override
    public void onRelease(int i) {

    }

    @Override
    public void onKey(int i, int[] ints) {
        InputConnection ic = getCurrentInputConnection();
        PasswordManager pm = (PasswordManager) getApplication();
        Intent intent = new Intent();
        intent.setClass(this, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
            case -13:
                String packageName = ProcessDetectUtil.getCurrentProcess(this);
                intent.putExtra("action", AuthenticationActivity.GET_NOTE);
                intent.putExtra("packageName", packageName);
                getApplicationContext().startActivity(intent);
                break;
            case -14:
                if (!pm.getLogin().isEmpty()) {
                    clearEdit(ic);
                    ic.commitText(pm.getLogin(), 1);
                    pm.setLogin("");
                } else {
                    Toast.makeText(this, getResources().getString(R.string.keyboard_username),
                            Toast.LENGTH_LONG).show();
                    pm.setiLogin(ic.getExtractedText(new ExtractedTextRequest(), 0).text.toString());
                }
                break;
            case -15:
                if (!pm.getPassword().isEmpty()) {
                    clearEdit(ic);
                    ic.commitText(pm.getPassword(), 1);
                    pm.setPassword("");
                } else {
                    Toast.makeText(this, getResources().getString(R.string.keyboard_password),
                            Toast.LENGTH_LONG).show();
                    pm.setiPassword(ic.getExtractedText(new ExtractedTextRequest(), 0).text.toString());
                }
                break;
            case -16:
                intent.putExtra("action", AuthenticationActivity.OPEN_GENERATOR);
                getApplicationContext().startActivity(intent);
                break;
            case -17:
                intent.putExtra("action", AuthenticationActivity.SAVE_NOTE);
                intent.putExtra("packageName", ProcessDetectUtil.getCurrentProcess(this));
                getApplicationContext().startActivity(intent);
                break;
            default:
                char code = (char) i;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
        }
    }

    private void clearEdit(InputConnection ic) {
        CharSequence currentText = ic.getExtractedText(new ExtractedTextRequest(), 0).text;
        CharSequence beforeCursorText = ic.getTextBeforeCursor(currentText.length(), 0);
        CharSequence afterCursorText = ic.getTextAfterCursor(currentText.length(), 0);
        ic.deleteSurroundingText(beforeCursorText.length(), afterCursorText.length());
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