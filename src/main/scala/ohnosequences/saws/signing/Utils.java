package ohnosequences.saws.signing;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Locale;

public class Utils {

    public static byte[] hmac(byte[] data, byte[] key) throws Exception {
        String alg = "HmacSHA256";
        Mac mac = Mac.getInstance(alg);
        mac.init(new SecretKeySpec(key, alg));
        return mac.doFinal(data);
    }

    public static byte[] hmac(String data, byte[] key) throws Exception {
        return hmac(data.getBytes(), key);
    }

    public static byte[] hmac(byte[] data, String key) throws Exception {
        return hmac(data, key.getBytes());
    }

    public static byte[] hmac(String data, String key) throws Exception {
        return hmac(data.getBytes(), key.getBytes());
    }

    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i]);
            if (hex.length() == 1) {
                // Append leading zero.
                sb.append("0");
            } else if (hex.length() == 8) {
                // Remove ff prefix from negative numbers.
                hex = hex.substring(6);
            }
            sb.append(hex);
        }
        return sb.toString().toLowerCase(Locale.getDefault());
    }

    public static byte[] hash(InputStream input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        DigestInputStream digestInputStream = new DigestInputStream(input, md);
        byte[] buffer = new byte[1024];
        while (digestInputStream.read(buffer) > -1) {
        }
        return digestInputStream.getMessageDigest().digest();
    }

    final static String DEFAULT_ENCODING = "UTF-8";
    public static byte[] hash(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes(DEFAULT_ENCODING));
        return md.digest();
    }

    public static String urlEncode(String value, boolean path) {


        if (value == null) return "";

        try {
            String encoded = URLEncoder.encode(value, DEFAULT_ENCODING)
                    .replace("+", "%20").replace("*", "%2A")
                    .replace("%7E", "~");
            if (path) {
                encoded = encoded.replace("%2F", "/");
            }

            return encoded;
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}


