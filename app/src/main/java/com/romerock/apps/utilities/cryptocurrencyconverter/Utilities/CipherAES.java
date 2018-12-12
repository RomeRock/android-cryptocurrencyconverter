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
    private final static String DEVICE = "TYPE_ANDROID2";
    public static String id = "";
    public static Context context = null;
    protected static String base64EncodedPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxj8ffQrclj1IiE6wq9GouONthDapAg0DlwhGIvpV71uljvJDQ1EXdZRi84v/p/lWSOhPWnsW1VSxaVTfnoVgniVmtMwHJLN2RLBCNRezTwSoBCVLfJde80PegXdPw3uLVCmnSEqkAf/lOR85vMr9WJ/T9rPWZW37xvPJyNQd2ZmItBINEgzabfGHosDLtwgN79IFxsOl45P6X8++95EvbL2hin0lN88HIIi8i8S7qtUuOlnvEGEdqskTQJTYTWTVBw8Q7iDFccSzbvybSCTV1hWd7icOcO+lbJKLf/sZPekVOlZHiGAPp3TmpypcR/cj/VlWYfFejAOj7uRy/loO5QIDAQAB";
    public static final String base64EncodedPublicKeyTEST = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCg" +
            "KCAQEAhNe2XQ70DceAwE6uyYJGK1dIBbZcPdlER/9EEzylr6RDU6tnGj0Tk7kceN03GKvRf/ucT+ERLL3O" +
            "aHR22PXRXLZ17NZ81x6oS2vGmLyXBnjrU/I+asl8cNuLGySaoCdXxPAV+A9g6OG13dk+KY9i0O1roGpFH" +
            "fsAFyKCgSqR0PMJZ1bS+wFFBYf3M4IxgBcxuuZKDmR+MztCgm5N4zc6w2CwFZn3mXeDoTg15mWDU3sZO" +
            "WeRwFeynhV+FCYdDp8DpAkLk1b5IiXYFQ53wxCh/GxiKqBB6uQMmAixFjAcZV1QWfcBABae9vxiV5" +
            "VAEJvOOnhPxnaT9HYadW0pQ/UbJwIDAQAB";
    protected static String apiKeyTwitter="IyySdIcR9GAtE3HVQ8i3kwT5O";
    protected static String apiSecretKeyTwitter="W51n5bjwmmG9c211eiaSZ6WngMjIeGeE7eO7gjjGwbzAEunTTn";

    public static String getApiKeyTwitter() {
        return apiKeyTwitter;
    }

    public static String getApiSecretKeyTwitter() {
        return apiSecretKeyTwitter;
    }

    public static String getItemPurchasedTest() {
        return ITEM_PURCHASED_TEST;
    }

    public static final String ITEM_PURCHASED_TEST = "android.test.purchased";

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        CipherAES.context = context;
    }

    public static String getDivece() {
        return DEVICE;
    }

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

    public static String getBaseKey(){
        return base64EncodedPublicKey;
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
