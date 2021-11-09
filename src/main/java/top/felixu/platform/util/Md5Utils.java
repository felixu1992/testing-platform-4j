package top.felixu.platform.util;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;

/**
 * MD5 加密
 *
 * @author felixu
 * @since 2021.08.05
 */
@Slf4j
public class Md5Utils {

    private final static String SALT  = "testing-platform";

    private final static String ALGORITHM = "MD5";

    public static String encode(String rawKey) {
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance(ALGORITHM);
            mdTemp.update(salt(rawKey));
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            log.error("---> Sign Error:" + e.getLocalizedMessage(), e);
            return "";
        }
    }

    public static boolean matches(String rawKey, String encodeKey) {
        return encode(rawKey).equals(encodeKey);
    }

    private static byte[] salt(String rawKey) {
        return (rawKey + SALT).getBytes();
    }
}
