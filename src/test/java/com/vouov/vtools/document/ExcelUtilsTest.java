package com.vouov.vtools.document;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;

/**
 * @author yuminglong@gmail.com
 * @date 2015/3/8
 */
public class ExcelUtilsTest {
    @Test
    public void testGenerateExcel() throws Exception {
        InputStream is = ExcelUtilsTest.class.getResourceAsStream("/com/vouov/vtools/document/Ynote_index_tpl.xlsx");
        Iterator<JSONObject> it = new Iterator<JSONObject>() {
            private int i=0;
            @Override
            public boolean hasNext() {
                return i<50000;
            }

            @Override
            public JSONObject next() {
                i++;
                JSONObject object = new JSONObject();
                object.put("groupId", i);
                object.put("fileId", i);
                object.put("directoryPath", i);
                object.put("name", i);
                object.put("title", i);
                JSONObject modifier = new JSONObject();
                modifier.put("name", ""+i);
                modifier.put("email", ""+i);
                object.put("modifier", modifier);
                JSONObject creator = new JSONObject();
                creator.put("name", ""+i);
                creator.put("email", ""+i);
                object.put("creator", creator);
                object.put("version", i);
                return object;
            }
        };
        FileOutputStream fos = new FileOutputStream("D:/ynote_tools.xlsx");
        ExcelUtils.generateExcel(is, it, fos);
    }
}
