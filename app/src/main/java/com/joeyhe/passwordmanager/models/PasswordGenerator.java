package com.joeyhe.passwordmanager.models;

import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.Security;


/**
 * Created by HGY on 2017/6/6.
 */

public class PasswordGenerator {

    private String masterPass;
    private final String uppers = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String lowers = "abcdefghijklmnopqrstuvwxyz";
    private final String digits = "0123456789";
    private final String symbols = "!@#$%&*-_+[]/.()~^";
    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public PasswordGenerator(String masterPass){
        this.masterPass = masterPass;
    }

    public String generate(int length, boolean hasUpper, boolean hasLower, boolean hasSymbols, boolean hasDigits, boolean excludeSimilar){
        StringBuilder pw = new StringBuilder();
        String dict = new String();
        if (hasUpper){
            dict = dict.concat(uppers);
        }
        if (hasLower){
            dict = dict.concat(lowers);
        }
        if (hasSymbols){
            dict = dict.concat(symbols);
        }
        if (hasDigits){
            dict = dict.concat(digits);
        }
        if (excludeSimilar){
            dict = dict.replaceAll("i|I|1|L|l|0|o|O","");
        }
        System.out.println(dict.length());
        System.out.println(dict);

        int iterationCount = 1000;
        int keyLength = 256;
        int saltLength = keyLength / 8; // same size as key output
        int i = 0;
        while(i < length){
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[saltLength];
            random.nextBytes(salt);

            PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
            generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(masterPass.toCharArray()), salt, iterationCount);
            KeyParameter key = (KeyParameter)generator.generateDerivedMacParameters(keyLength);
            byte[] keyByte = key.getKey();
            int j = 0;
            while(i < length & j < 32){
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
