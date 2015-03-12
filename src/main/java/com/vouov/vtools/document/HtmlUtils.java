package com.vouov.vtools.document;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML文本处理工具类
 *
 * @author yuminglong@gmail.com
 * @date 2015/3/8
 */
public class HtmlUtils {
    /**
     * 清理HTML内容,转换成标准xhtml
     * @param html
     * @return
     */
    public static String tidyHtml(String html){
        Document doc = Jsoup.parse(html);
        // Clean the document.
        //doc = new Cleaner(Whitelist.basicWithImages()).clean(doc);
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        doc.outputSettings().prettyPrint(true);

        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        // Get back the string of the body.
        return doc.html();
    }

    public static String fillImageURL(String html, String prefix, String suffix){
        Pattern pattern = Pattern.compile("<img.*?src=\"(.*?)\".*?/>");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            String imageUrl = matcher.group(1);
            System.out.println("-----" + imageUrl);
            StringBuilder newImageUrl = new StringBuilder();
            if(prefix!=null){
                newImageUrl.append(prefix);
            }
            newImageUrl.append(imageUrl);
            if(suffix!=null){
                newImageUrl.append(suffix);
            }
            html = html.replace(imageUrl, newImageUrl.toString());
        }
        return html;
    }
}
