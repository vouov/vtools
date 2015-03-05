package com.vouov.vtools.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author yuml
 * @date 2015/3/4
 */
public class CyptoUtilsTest {
    @Test
    public void testHex() throws Exception {
        String data = "test";
        char[] encodeHexBytes = CyptoUtils.encodeHex(StringUtils.getBytesUtf8(data));
        Assert.assertEquals(data, StringUtils.newStringUtf8(CyptoUtils.decodeHex(encodeHexBytes)));

        String encodeHexBytes2 = CyptoUtils.encodeHexString(StringUtils.getBytesUtf8(data));
        Assert.assertEquals(data, StringUtils.newStringUtf8(CyptoUtils.decodeHex(encodeHexBytes2.toCharArray())));
    }

    @Test
    public void testMd5() throws Exception {
        String data = "test";
        Assert.assertEquals("098f6bcd4621d373cade4e832627b4f6", CyptoUtils.md5(data));
    }

    @Test
    public void testSha512() throws Exception {
        String data = "test";
        Assert.assertEquals("ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff", CyptoUtils.sha512(data));
    }

    @Test
    public void testAes() throws Exception {
        String data = "test";
        String password = "password";

        String encoded = CyptoUtils.encodeAES(data, password);
        Assert.assertEquals("2hgJMEluxpv+upI7cxEDeg==", encoded);
        Assert.assertEquals(data, CyptoUtils.decodeAES(encoded, password));
    }

    @Test
    public void testAesZeroPadding() throws Exception {
        String data = "test";
        String password = "password";
        String iv = "1234567";

        String encoded = CyptoUtils.encodeAESZeroPadding(data, password, iv);
        Assert.assertEquals("lrtERk11oL8kEh33NYpcFA==", encoded);
        Assert.assertEquals(data, CyptoUtils.decodeAESZeroPadding(encoded, password, iv));

    }

}
