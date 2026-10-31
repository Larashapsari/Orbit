package orbit.ac.id;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * PasswordUtils — hash password dengan SHA-256.
 * Tidak memerlukan library tambahan (tersedia di Android SDK standar).
 */
public class PasswordUtils {

    private PasswordUtils() {}

    /**
     * Hash string dengan SHA-256 dan kembalikan sebagai hex string.
     * @param plainText password plaintext
     * @return hex string 64 karakter, atau plaintext asli jika terjadi error
     */
    public static String hash(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(plainText.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            // Fallback: kembalikan plaintext agar app tidak crash
            return plainText;
        }
    }

    /**
     * Verifikasi apakah plaintext cocok dengan hash yang tersimpan.
     * Juga mendukung password lama yang belum di-hash (backward compat).
     */
    public static boolean verify(String plainText, String storedValue) {
        if (storedValue == null || plainText == null) return false;
        return hash(plainText).equals(storedValue) || plainText.equals(storedValue);
    }
}

