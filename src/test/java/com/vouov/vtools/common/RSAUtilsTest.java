package com.vouov.vtools.common;

import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;

/**
 * @author yuml
 * @date 2015/3/5
 */
public class RSAUtilsTest {

    @Test
    public void testRSA() throws Exception {
        KeyPair keyPair = RSAUtils.generateKeyPair();

        String source = "RSA算法即RSA加密算法，这是一种非对称加密算法。在公开密钥加密和电子商业中RSA被广泛使用。RSA是1977年由罗纳德·李维斯特（Ron Rivest）、阿迪·萨莫尔（Adi Shamir）和伦纳德·阿德曼（Leonard Adleman）一起提出的。RSA就是他们三人姓氏开头字母拼在一起组成的。";
        byte[] data = StringUtils.getBytesUtf8(source);
        byte[] encodedData = RSAUtils.encryptByPublicKey(data, keyPair.getPublic());
        String sign = RSAUtils.sign(encodedData, keyPair.getPrivate());

        boolean status = RSAUtils.verify(encodedData, keyPair.getPublic(), sign);
        Assert.assertTrue(status);
        byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData, keyPair.getPrivate());
        String target = StringUtils.newStringUtf8(decodedData);
        Assert.assertEquals(source, target);



    }

}
