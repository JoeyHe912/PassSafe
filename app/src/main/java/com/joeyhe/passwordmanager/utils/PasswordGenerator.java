package com.joeyhe.passwordmanager.utils;

import java.nio.ByteBuffer;


/**
 * Created by HGY on 2017/6/6.
 */

public class PasswordGenerator {

    private final String UPPERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String LOWERS = "abcdefghijklmnopqrstuvwxyz";
    private final String DIGITS = "0123456789";
    private final String SYMBOLS = "!@#$%&*-_+[]/.()~^";
    private final String SIMILAR = "i|I|1|L|l|0|o|O";
    private final int ITERATION_COUNT = 1000;
    private final int KEY_LENGTH = 256;
    private String masterPass;

    public PasswordGenerator(String masterPass){
        this.masterPass = masterPass;
    }

    public String generate(int length, boolean hasUpper, boolean hasLower, boolean hasSymbol, boolean hasDigit, boolean excludeSimilar){
        StringBuilder pw = new StringBuilder();
        String dict = "";
        if (hasUpper){
            dict = dict.concat(UPPERS);
        }
        if (hasLower){
            dict = dict.concat(LOWERS);
        }
        if (hasSymbol){
            dict = dict.concat(SYMBOLS);
        }
        if (hasDigit){
            dict = dict.concat(DIGITS);
        }
        if (excludeSimilar){
            dict = dict.replaceAll(SIMILAR, "");
        }

        int i = 0;
        HashUtil hashUtil = new HashUtil(ITERATION_COUNT, KEY_LENGTH, masterPass);
        while(i < length){
            byte[] keyByte = hashUtil.deriveHash();
            int j = 0;
            while (i < length & j < keyByte.length) {
                byte[] temp = new byte[2];
                temp[0] = keyByte[j];
                temp[1] = keyByte[j+1];
                ByteBuffer wrapped = ByteBuffer.wrap(temp);
                short rand = wrapped.getShort();
                pw.append(dict.charAt((rand & 0xFFFF) % dict.length()));
                j += 2;
                i += 1;
            }
        }
        return pw.toString();
    }
}
