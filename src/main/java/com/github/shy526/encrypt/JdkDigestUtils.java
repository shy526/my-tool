package com.github.shy526.encrypt;


import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * 常用指纹摘要算法
 *
 * @author shy526
 */
public final class JdkDigestUtils {
    private final static String H_MAC_MD5 = "HmacMD5";

    /**
     * 摘要算法
     *
     * @param digestMode 摘要
     * @param str        需要摘要的文本
     * @return String
     */
    public static byte[] encoder(DigestMode digestMode, String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(digestMode.getMode());
            return messageDigest.digest(str.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public enum DigestMode {
        /**
         * sha
         */
        SHA("SHA"),
        /**
         * md5
         */
        MD5("MD5"),
        /**
         * sha_256
         */
        SHA_256("SHA-256");
        private final String mode;

        DigestMode(String mode) {
            this.mode = mode;
        }

        public String getMode() {
            return mode;
        }

    }


    /**
     * hmacMd5
     *
     * @param str 需要摘要的文本
     * @return String
     */
    public static byte[] encoderHmacMd5(String str) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(H_MAC_MD5);
            keyGenerator.init(new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] bytes = secretKey.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, H_MAC_MD5);
            Mac mac = Mac.getInstance(secretKeySpec.getAlgorithm());
            mac.init(secretKeySpec);
            return mac.doFinal(str.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
