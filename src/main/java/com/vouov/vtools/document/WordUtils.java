package com.vouov.vtools.document;

import com.vouov.vtools.common.IOUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.*;

/**
 * @author yuminglong@gmail.com
 * @date 2015/3/4
 */
public class WordUtils {

    /**
     * html文本转换为word
     *
     * @param html
     * @param baseURL
     * @param os
     * @throws Docx4JException
     */
    public static void html2Docx(String html, String baseURL, OutputStream os) throws Docx4JException {
        String xhtml = HtmlUtils.tidyHtml(html);
        // To docx, with content controls
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

        XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
        //XHTMLImporter.setDivHandler(new DivToSdt());
        wordMLPackage.getMainDocumentPart().getStyleDefinitionsPart().getLinkedStyle("");
        wordMLPackage.getMainDocumentPart().getContent().addAll(
                XHTMLImporter.convert(xhtml, baseURL));

        /*System.out.println(XmlUtils.marshaltoString(wordMLPackage
                .getMainDocumentPart().getJaxbElement(), true, true));*/

        wordMLPackage.save(os);
    }

    /**
     * html文本转换为word
     *
     * @param is
     * @param baseURL
     * @param os
     * @throws IOException
     * @throws Docx4JException
     */
    public static void html2Docx(InputStream is, String baseURL, OutputStream os) throws IOException, Docx4JException {
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
            html2Docx(html.toString(), baseURL, os);
        } finally {
            IOUtils.close(bufferedReader);
            IOUtils.close(isReader);
        }
    }

    /**
     * html文本转换为word
     *
     * @param html
     * @param baseURL
     * @param outFile
     * @throws Docx4JException
     */
    public static void html2Docx(String html, String baseURL, File outFile) throws Docx4JException {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(outFile);
            html2Docx(html, baseURL, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(os);
        }
    }
}
