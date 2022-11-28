import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class App {

    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        try (FileOutputStream fos = new FileOutputStream("cle.key")) {
            fos.write(key.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    public static String encrypt(String algorithm, String input, SecretKey key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        try (FileOutputStream fos = new FileOutputStream("message_encrypted.txt")) {
            fos.write(cipherText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String algorithm, String file, SecretKey key)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        byte[] cipherText = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            cipherText = new byte[fis.available()];
            fis.read(cipherText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText);

    }

    public static void main(String[] args) throws Exception {
        SecretKey key = generateKey(128);
        String message = "Hello World";
        try (FileOutputStream fos = new FileOutputStream("message.txt")) {
            fos.write(message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String cypherText = encrypt("AES", message, key);
        System.out.println(cypherText);
        String plainText = decrypt("AES", "message_encrypted.txt", key);
        System.out.println(plainText);
    }

}
