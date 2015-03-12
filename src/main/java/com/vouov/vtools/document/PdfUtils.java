package com.vouov.vtools.document;


import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.vouov.vtools.common.IOUtils;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;

/**
 * PDF文档工具类
 *
 * @author yuminglong@gmail.com
 * @date 2015/3/4
 */
public class PdfUtils {

    /**
     * 使用IText默认实现
     *
     * @param html
     * @param baseURL
     * @param os
     * @throws DocumentException
     * @throws IOException
     *//*
    public static void html2Pdf(String html, String baseURL, OutputStream os) throws DocumentException, IOException {
        String xhtml = HtmlUtils.tidyHtml(html);
        xhtml = HtmlUtils.fillImageURL(xhtml, baseURL, null);
        // step 1
        Document document = new Document();
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, os);
        // step 3
        document.open();
        // step 4
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(xhtml.getBytes("UTF-8")), Charset.forName("UTF-8"));
        //step 5
        document.close();
    }*/

    /**
     * 使用flying-saucer-pdf-itext5实现
     *
     * @param html
     * @param baseURL
     * @param os
     * @throws IOException
     * @throws DocumentException
     */
    public static void html2Pdf(String html, String baseURL, OutputStream os) throws IOException, DocumentException {
        ITextRenderer renderer = new ITextRenderer();
        String xhtml = HtmlUtils.tidyHtml(html);

        renderer.setDocumentFromString(xhtml, baseURL);

        // 解决中文支持问题
        ITextFontResolver fontResolver = renderer.getFontResolver();
        fontResolver.addFont("C:/Windows/Fonts/SIMSUN.TTC", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        //解决图片的相对路径问题
        renderer.getSharedContext().setBaseURL(baseURL);
        renderer.layout();
        renderer.createPDF(os);
    }

    public static void html2Pdf(InputStream is, String baseURL, OutputStream os) throws IOException, DocumentException {
        InputStreamReader isReader = null;
        BufferedReader bufferedReader = null;
        try {
            isReader = new InputStreamReader(is);
            bufferedReader = new BufferedReader(isReader);
            StringBuilder html = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                html.append(line).append("\r\n");
            }
            html2Pdf(html.toString(), baseURL, os);
        } finally {
            IOUtils.close(bufferedReader);
            IOUtils.close(isReader);
        }
    }

}
