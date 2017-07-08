package com.joeyhe.passwordmanager.utils;

import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.encoders.Hex;

import java.security.SecureRandom;

/**
 * Created by HGY on 2017/7/5.
 */

public class HashUtil {
    private byte[] salt;
    private byte[] keyByte;
    private int iterationCount;
    private int keyLength;
    private String masterPass;

    public HashUtil(int ic, int kl, String mp) {
        iterationCount = ic;
        keyLength = kl;
        masterPass = mp;
    }

    public byte[] deriveHash() {
        int saltLength = keyLength / 8;
        SecureRandom random = new SecureRandom();
        salt = new byte[saltLength];
        random.nextBytes(salt);
        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
        generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(masterPass.toCharArray()), salt, iterationCount);
        KeyParameter key = (KeyParameter) generator.generateDerivedMacParameters(keyLength);
        keyByte = key.getKey();
        return keyByte;
    }

    public byte[] deriveHash(String s) {
        salt = Hex.decode(s);
        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
        generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(masterPass.toCharArray()), salt, iterationCount);
        KeyParameter key = (KeyParameter) generator.generateDerivedMacParameters(keyLength);
        keyByte = key.getKey();
        return keyByte;
    }

    public String getStringHash() {
        return Hex.toHexString(deriveHash());
    }

    public String getStringHash(String s) {
        return Hex.toHexString(deriveHash(s));
    }

    public String getSalt() {
        return Hex.toHexString(salt);
    }
}
