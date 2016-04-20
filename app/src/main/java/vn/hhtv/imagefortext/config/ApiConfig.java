package vn.hhtv.imagefortext.config;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by iservice on 2/3/16.
 */
public class ApiConfig {

    public static final String ROOT_URL = "http://igo10.hhtv.vn/"; // DEV
    public static final String SEARCH_URL = ROOT_URL + "a1/search";

    public static final String KEY_TEXT = "text";
    public static final String KEY_SIZE = "size";
    public static final String KEY_IMAGE = "image";
    public static final String UPLOAD_URL = ROOT_URL + "a1/post";

    public static final String getKKyo(){
        long ss = System.currentTimeMillis() - 450431850278l;
        String kk = "IGO1010" + String.valueOf(ss).charAt(0) + "g" + "IGO1010";
        return kk;
    }

    public static byte[] getkkko(){
        byte[] bytes = new byte[]{-67,66,-106,67,-84,92,55,-120,108,-92,123,97,-56,-50,-15,-104,-121,119,7,47,-128,-57,-68,-49,28,-32,69,91,-109,62,26,10};

        return bytes;
    }

    public static final String geteyk(){
        String key = getKKyo(); // 128 bit key
        // Create key and cipher
        java.security.Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        // encrypt the text
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            String kkk = new String(cipher.doFinal(getkkko())) + "3a3a".substring(2);
            System.currentTimeMillis();
            return kkk;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return "igo10";
    }
}
