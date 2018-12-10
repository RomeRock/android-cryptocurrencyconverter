package com.romerock.apps.utilities.cryptocurrencyconverter.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Base64;

import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class CipherAES {
    private final static String secretKey = "fdg%$/%(n bn34HacP4%WS";
    private final static String secretKeyAndroid = "4BRacU75#Zc7P336P";
    private final static String ALGORITHM = "AES";
    public static String id = "";
    public static Context context = null;
    @SuppressLint("TrulyRandom")

    public static String cipher(String data) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec;
        spec = new PBEKeySpec(secretKey.toCharArray(), secretKey.getBytes(), 128, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey key = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return toHex(cipher.doFinal(data.getBytes()));
    }

    public static String decipher(String data) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec;
        spec = new PBEKeySpec(secretKey.toCharArray(), secretKey.getBytes(), 128, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey key = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(toByte(data)));
    }


    public static String cipher(String data, boolean cipherProccess) throws Exception {
        String keyDevide = secretKeyAndroid;

        SecretKeySpec key = new SecretKeySpec(keyDevide.getBytes("UTF-8"), "AES");
        byte[] iv = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        String base64 = "";
        if (cipherProccess) {
            id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); //id dispositivo
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy date
            Date now = new Date();
            String strDate = sdfDate.format(now);
            String encriptData = id + ";" + data + ";" + strDate;
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] result = cipher.doFinal(encriptData.getBytes("UTF-8"));
            base64 = Base64.encodeToString(result, 0);
            // System.out.println("Ciphertext: " + base64 + "\n");
        } else {
            //To Decrypt:
            byte[] decoded = Base64.decode(data, 0);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] decrypted = cipher.doFinal(decoded);
            base64 = new String(decrypted);
        }
        return base64;
    }


    // Helper methods
    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] stringBytes) {
        StringBuffer result = new StringBuffer(2 * stringBytes.length);
        for (int i = 0; i < stringBytes.length; i++) {
            result.append(HEX.charAt((stringBytes[i] >> 4) & 0x0f)).append(HEX.charAt(stringBytes[i] & 0x0f));
        }
        return result.toString();
    }


    private final static String HEX = "0123456789ABCDEF";

    public static String hexEncode(byte[] input) {
        if (input == null || input.length == 0) {
            return "";
        }
        int inputLength = input.length;
        StringBuilder output = new StringBuilder(inputLength * 2);

        for (int i = 0; i < inputLength; i++) {
            int next = input[i] & 0xff;
            if (next < 0x10) {
                output.append("0");
            }
            output.append(Integer.toHexString(next));
        }

        return output.toString();
    }

}
