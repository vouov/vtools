package com.vouov.vtools.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 加密工具类，支持的算法如下：
 * <ul>
 * <li>md5</li>
 * <li>sha512</li>
 * <li>rsa</li>
 * <li>aes</li>
 * </ul>
 *
 * @author yuml
 * @date 2015/3/4
 */
public class CyptoUtils {
    private static final Logger logger = LoggerFactory.getLogger(CyptoUtils.class);

    private static final char[] DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static byte[] decodeHex(char[] data) throws Exception {
        int len = data.length;
        if ((len & 1) != 0) {
            throw new Exception("Odd number of characters.");
        } else {
            byte[] out = new byte[len >> 1];
            int i = 0;

            for (int j = 0; j < len; ++i) {
                int f = toDigit(data[j], j) << 4;
                ++j;
                f |= toDigit(data[j], j);
                ++j;
                out[i] = (byte) (f & 255);
            }

            return out;
        }
    }

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        int i = 0;

        for (int j = 0; i < l; ++i) {
            out[j++] = toDigits[(240 & data[i]) >>> 4];
            out[j++] = toDigits[15 & data[i]];
        }

        return out;
    }

    public static String encodeHexString(byte[] data) {
        return new String(encodeHex(data));
    }

    protected static int toDigit(char ch, int index) throws Exception {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new Exception("Illegal hexadecimal character " + ch + " at index " + index);
        } else {
            return digit;
        }
    }

    protected static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException var2) {
            throw new IllegalArgumentException(var2);
        }
    }

    protected static MessageDigest updateDigest(MessageDigest messageDigest, byte[] valueToDigest) {
        messageDigest.update(valueToDigest);
        return messageDigest;
    }

    protected static MessageDigest updateDigest(MessageDigest digest, InputStream data) throws IOException {
        byte[] buffer = new byte[1024];

        for (int read = data.read(buffer, 0, 1024); read > -1; read = data.read(buffer, 0, 1024)) {
            digest.update(buffer, 0, read);
        }

        return digest;
    }

    protected static MessageDigest updateDigest(MessageDigest messageDigest, String valueToDigest) {
        messageDigest.update(StringUtils.getBytesUtf8(valueToDigest));
        return messageDigest;
    }

    /**
     * 字符串MD5摘要生成
     *
     * @param data
     * @return
     */
    public static String md5(String data) {
        return encodeHexString(updateDigest(getDigest("MD5"), data).digest());
    }

    /**
     * 输入流MD5摘要生成
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String md5(InputStream is) throws IOException {
        return encodeHexString(updateDigest(getDigest("MD5"), is).digest());
    }

    /**
     * 文件内容MD5摘要生成
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String md5(File file) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return md5(fis);
        } finally {
            IOUtils.close(fis);
        }
    }

    /**
     * 带有salt干扰的字符串MD5摘要生成
     *
     * @param data
     * @param salt
     * @return
     */
    public static String md5(String data, String salt) {
        MessageDigest digest = updateDigest(getDigest("MD5"), data);
        return encodeHexString(updateDigest(digest, salt).digest());
    }

    /**
     * 字符串SHA-512摘要生成
     *
     * @param data
     * @return
     */
    public static String sha512(String data) {
        return encodeHexString(updateDigest(getDigest("SHA-512"), data).digest());
    }

    /**
     * 输入流SHA-512摘要生成
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String sha512(InputStream is) throws IOException {
        return encodeHexString(updateDigest(getDigest("MD5"), is).digest());
    }

    /**
     * 文件内容SHA-512摘要生成
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String sha512(File file) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return sha512(fis);
        } finally {
            IOUtils.close(fis);
        }
    }

    /**
     * 带有salt干扰的字符串SHA-512摘要生成
     *
     * @param data
     * @param salt
     * @return
     */
    public static String sha512(String data, String salt) {
        MessageDigest digest = updateDigest(getDigest("SHA-512"), data);
        return encodeHexString(updateDigest(digest, salt).digest());
    }

    public static String encodeAES(String data, String password, String iv) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, new SecureRandom(password.getBytes()));
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器
        byte[] byteContent = data.getBytes("utf-8");
        cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
        byte[] result = cipher.doFinal(byteContent);
        return new String(result); // 加密
    }

    public static String decodeAES(String data, String password, String iv) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, new SecureRandom(password.getBytes()));
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器
        cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
        byte[] result = cipher.doFinal(data.getBytes());
        return StringUtils.newStringUtf8(result); // 加密
    }

    /**
     * 兼容PHP等其他语言AES加密方法
     *
     * @param data
     * @param password
     * @param iv
     * @return
     * @throws Exception
     */
    public static String encodeAESZeroPadding(String data, String password, String iv) throws Exception {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(StringUtils.getBytesIso8859_1(md5(password)), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(StringUtils.getBytesIso8859_1(md5(iv)));
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            //对内容不是16整数倍数时补位
            int blockSize = cipher.getBlockSize();
            byte[] bytes = StringUtils.getBytesUtf8(data);
            int dataBytesLength = bytes.length;
            int mod = dataBytesLength % blockSize;
            if (mod != 0) {
                dataBytesLength = dataBytesLength + (blockSize - mod);
            }
            byte[] newBytes = new byte[dataBytesLength];
            System.arraycopy(bytes, 0, newBytes, 0, bytes.length);


            byte[] result = cipher.doFinal(bytes);
            return Base64.encode(result);
        } catch (Exception e) {
            logger.error("AES ZeroPadding模式加密失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 兼容PHP等其他语言AES解密方法
     *
     * @param data
     * @param password
     * @param iv
     * @return
     * @throws Exception
     */
    public static String decodeAESZeroPadding(String data, String password, String iv) throws Exception {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(StringUtils.getBytesIso8859_1(md5(password)), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(StringUtils.getBytesIso8859_1(md5(iv)));
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);// 初始化

            byte[] bytes = Base64.decode(data);
            byte[] result = cipher.doFinal(bytes);
            return StringUtils.newStringUtf8(result);
        } catch (Exception e) {
            logger.error("AES ZeroPadding模式解密失败", e);
            throw new RuntimeException(e);
        }
    }

    public static String encodeRSA(String data) {
        return null;
    }

    public static String decodeRSA(String data) {
        return null;
    }
}
