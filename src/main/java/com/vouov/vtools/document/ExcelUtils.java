package com.vouov.vtools.document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yuminglong@gmail.com
 * @date 2015/3/4
 */
public class ExcelUtils {

    public static <T> void generateExcel(InputStream templateInputStream, Iterator<T> data, OutputStream outputStream) throws IOException, InvalidFormatException {
        // 通过类加载器获取模板
        XSSFWorkbook workbook = new XSSFWorkbook(templateInputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.getRow(1);

        List<CellStyle> cellStyles = new ArrayList<CellStyle>();
        List<String> cellValues = new ArrayList<String>();
        Map<Integer, String> cellFormatMap = new HashMap<Integer, String>();
        Pattern pattern = Pattern.compile("^\\$\\{([\\w\\.]+)\\}$");
        for (int i = 0; i < row.getLastCellNum(); i++) {
            XSSFCell cell = row.getCell(i);
            cellStyles.add(cell.getCellStyle());
            String cellValue = cell.getStringCellValue();
            cellValues.add(cellValue);
            if (cellValue != null) {
                Matcher matcher = pattern.matcher(cellValue);
                if (matcher.find()) {
                    cellFormatMap.put(i, matcher.group(1));
                }
            }
        }

        sheet.removeRow(row);
        SXSSFWorkbook newWorkbook = new SXSSFWorkbook(workbook); // keep 100 rows in memory, exceeding rows will be flushed to disk
        Sheet newSheet = newWorkbook.getSheetAt(0);
        int rowNum = 1;
        while (data.hasNext()) {
            T t = data.next();
            JSONObject item = null;
            if (t instanceof JSONObject) {
                item = (JSONObject) t;
            } else {
                String json = JSON.toJSONString(t);
                item = JSON.parseObject(json);
            }

            Row newRow = newSheet.createRow(rowNum);
            for (int i = 0; i < cellValues.size(); i++) {
                Cell newCell = newRow.createCell(i);
                newCell.setCellStyle(cellStyles.get(i));
                String newCellValue = cellValues.get(i);
                if (newCellValue != null && cellFormatMap.containsKey(i)) {
                    newCellValue = JsonUtils.getJSONValue(item, cellFormatMap.get(i));
                }
                newCell.setCellValue(newCellValue);
            }

            rowNum++;
        }
        newWorkbook.write(outputStream);
    }
}
