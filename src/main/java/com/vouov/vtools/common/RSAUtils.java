package com.vouov.vtools.common;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Enumeration;

/**
 * RSA公钥/私钥/签名工具包
 * <p/>
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 *
 * @author yuminglong@gmail.com
 * @date 2015/3/4
 */
public class RSAUtils {


    static {
        //添加RSA第三方provider，主要解决android的兼容问题，提供统一的Provider
        Security.addProvider(new BouncyCastleProvider());
    }


    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM_NAME = "RSA";

    public static final String KEY_ALGORITHM_MODE = "RSA/ECB/PKCS1Padding";


    /**
     * BouncyCastleProvider的名字
     */
    public static final String PROVIDER_NAME = "BC";

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM_NAME = "MD5withRSA";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM_NAME, PROVIDER_NAME);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        keyPair.getPublic();
        keyPair.getPrivate();
        return keyPair;
    }


    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       已加密数据
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_NAME, PROVIDER_NAME);
        signature.initSign(privateKey);
        signature.update(data);
        return Base64.encode(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param data      已加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, PublicKey publicKey, String sign)
            throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_NAME, PROVIDER_NAME);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(Base64.decode(sign));
    }

    /**
     * 私钥解密
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, PrivateKey privateKey)
            throws Exception {

        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE, PROVIDER_NAME);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥解密
     *
     * @param encryptedData 已加密数据
     * @param publicKey     公钥
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, PublicKey publicKey)
            throws Exception {

        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE, PROVIDER_NAME);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥加密
     *
     * @param data      源数据
     * @param publicKey 公钥
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, PublicKey publicKey)
            throws Exception {
        // 对数据加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE, PROVIDER_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);


        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 私钥加密
     *
     * @param data       源数据
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, PrivateKey privateKey)
            throws Exception {

        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE, PROVIDER_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }


    /**
     * 从文件中输入流中加载公钥
     *
     * @param in 公钥输入流
     * @throws Exception 加载公钥时产生的异常
     */
    public PublicKey loadPublicKey(InputStream in) throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String readLine = null;
        StringBuilder sb = new StringBuilder();
        while ((readLine = br.readLine()) != null) {
            if (readLine.charAt(0) == '-') {
                continue;
            } else {
                sb.append(readLine);
                sb.append('\r');
            }
        }
        return loadPublicKey(sb.toString());

    }

    /**
     * 从文件中加载私钥
     *
     * @param in 私钥文件名
     * @return 是否成功
     * @throws Exception
     */
    public PrivateKey loadPrivateKey(InputStream in) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String readLine = null;
        StringBuilder sb = new StringBuilder();
        while ((readLine = br.readLine()) != null) {
            if (readLine.charAt(0) == '-') {
                continue;
            } else {
                sb.append(readLine);
                sb.append('\r');
            }
        }
        return loadPrivateKey(sb.toString());
    }

    /**
     * 从字符串中加载私钥
     *
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static PrivateKey loadPrivateKey(String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_NAME, PROVIDER_NAME);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static PublicKey loadPublicKey(String publicKey) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_NAME, PROVIDER_NAME);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 从keystore中读取私钥
     *
     * @param alias
     * @param path     包含私钥的证书路径
     * @param password
     * @return 私钥PrivateKey
     * @throws Exception
     */
    public static PrivateKey loadKeyStorePrivateKey(String alias, String path, String password) throws Exception {
        FileInputStream fis = new FileInputStream(path);
        char[] passwordChars = null;
        if ((password != null) || "".equals(password.trim())) {
            passwordChars = password.toCharArray();
        }

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(fis, passwordChars);
        fis.close();
        String keyAlias = alias;
        if (alias == null) {
            Enumeration<String> en = keyStore.aliases();
            if (en.hasMoreElements()) {
                keyAlias = en.nextElement();
            }
        }
        return (PrivateKey) keyStore.getKey(keyAlias, passwordChars);
    }

    /**
     * 从keystore中读取私钥
     *
     * @param path     包含私钥的证书路径
     * @param password
     * @return 私钥PrivateKey
     * @throws Exception
     */
    public static PrivateKey loadKeyStorePrivateKey(String path, String password) throws Exception {
        return loadKeyStorePrivateKey(null, path, password);
    }

    /**
     * 从keystore中读取公钥
     *
     * @param alias
     * @param path     包含公钥的证书路径
     * @param password
     * @return
     * @throws Exception
     */
    public static PublicKey loadKeyStorePublicKey(String alias, String path, String password) throws Exception {
        FileInputStream fis = new FileInputStream(path);
        char[] passwordChars = null;
        if ((password != null) || "".equals(password.trim())) {
            passwordChars = password.toCharArray();
        }

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(fis, passwordChars);
        fis.close();
        String keyAlias = alias;
        if (alias == null) {
            Enumeration<String> en = keyStore.aliases();
            if (en.hasMoreElements()) {
                keyAlias = en.nextElement();
            }
        }
        return keyStore.getCertificate(keyAlias).getPublicKey();
    }

    /**
     * 从keystore中读取公钥
     *
     * @param path     包含公钥的证书路径
     * @param password
     * @return
     * @throws Exception
     */
    public static PublicKey loadKeyStorePublicKey(String path, String password) throws Exception {
        return loadKeyStorePublicKey(null, path, password);
    }

    /**
     * 根据公钥n、e生成公钥
     *
     * @param modulus        公钥n串
     * @param publicExponent 公钥e串
     * @return 返回公钥PublicKey
     * @throws Exception
     */
    public static PublicKey loadPublicKey(String modulus, String publicExponent) throws Exception {
        KeySpec publicKeySpec = new RSAPublicKeySpec(
                new BigInteger(modulus, 16), new BigInteger(publicExponent, 16));
        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM_NAME, PROVIDER_NAME);
        PublicKey publicKey = factory.generatePublic(publicKeySpec);
        return publicKey;
    }

}
