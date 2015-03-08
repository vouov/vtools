package com.vouov.vtools.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author yuminglong@gmail.com
 * @date 2015/3/5
 */
public class StringUtilsTest {
    @Test
    public void testUtf8Length() throws Exception {
        String data = "test";
        Assert.assertEquals(4, StringUtils.getBytesUtf8(data).length);

        data = "test中国";
        Assert.assertEquals(10, StringUtils.getBytesUtf8(data).length);

        data = "中国";
        Assert.assertEquals(6, StringUtils.getBytesUtf8(data).length);

        data = "Ω";
        Assert.assertEquals(2, StringUtils.getBytesUtf8(data).length);

        data = "test";
        Assert.assertEquals(4, StringUtils.getBytesIso8859_1(data).length);

        data = "test中国";
        Assert.assertEquals(6, StringUtils.getBytesIso8859_1(data).length);

        data = "中国";
        Assert.assertEquals(2, StringUtils.getBytesIso8859_1(data).length);

        data = "Ω";
        Assert.assertEquals(1, StringUtils.getBytesIso8859_1(data).length);
    }
}
