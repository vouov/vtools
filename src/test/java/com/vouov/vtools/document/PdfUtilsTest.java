package com.vouov.vtools.document;

import org.junit.Test;

import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author yuminglong@gmail.com
 * @date 2015/3/8
 */
public class PdfUtilsTest {
    @Test
    public void testHtml2Pdf() throws Exception {
        InputStream is = WordUtilsTest.class.getClassLoader().getResourceAsStream("com/vouov/vtools/document/w3c_school.xhtml.html");
        String baseURL = "http://demo.cssmoban.com/cssthemes3/mstp_58_space/";
        PdfUtils.html2Pdf(is, baseURL, new FileOutputStream("D:/w3c_school.pdf"));

    }
}
