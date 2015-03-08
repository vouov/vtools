package com.vouov.vtools.document;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author yuminglong@gmail.com
 * @date 2015/3/8
 */
public class HtmlUtilsTest {
    @Test
    public void testTidyHtml() throws Exception {
        String html = "<p>test1</p><br><div>xxxxxx";
        String target = "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  <p>test1</p>\n" +
                "  <br />\n" +
                "  <div>\n" +
                "   xxxxxx\n" +
                "  </div>\n" +
                " </body>\n" +
                "</html>";
        String xhtml = HtmlUtils.tidyHtml(html);
        Assert.assertEquals(target, xhtml);
    }
}
