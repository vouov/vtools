package com.vouov.vtools.document;

/**
 * @author yuminglong@gmail.com
 * @date 2015/3/4
 */
public class ExcelUtils {
    /*public static String getJSONValue(JSONObject jsonObject, String key) {
        String value = null;
        String[] strings = key.split("\\.");
        JSONObject object = jsonObject;
        if (strings != null && strings.length > 0) {
            for (int i = 0; i < strings.length; i++) {
                if (i != (strings.length - 1)) {
                    object = object.getJSONObject(strings[i]);
                    if (object == null) return "";
                } else {
                    value = object.getString(strings[i]);
                }
            }
        }
        return value;
    }

    public static void writeExcel(List<JSONObject> files, File output) throws IOException, InvalidFormatException {

        // 通过类加载器获取模板
        Workbook workbook = WorkbookFactory.create(YNoteHelper.class
                .getResourceAsStream("/Ynote_index_tpl.xlsx"));
        FileOutputStream fos = new FileOutputStream(output);
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(1);
        CellStyle rowStyle = row.getRowStyle();
        List<CellStyle> cellStyles = new ArrayList<CellStyle>();
        List<String> cellValues = new ArrayList<String>();
        Map<Integer, String> cellFormatMap = new HashMap<Integer, String>();
        Pattern pattern = Pattern.compile("^\\$\\{([\\w\\.]+)\\}$");
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
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
        int rowNum = 1;

        for (JSONObject file : files) {
            Row newRow = sheet.createRow(rowNum);
            newRow.setRowStyle(rowStyle);
            for (int i = 0; i < cellValues.size(); i++) {
                Cell newCell = newRow.createCell(i);
                newCell.setCellStyle(cellStyles.get(i));
                String newCellValue = cellValues.get(i);
                if (newCellValue != null && cellFormatMap.containsKey(i)) {
                    newCellValue = getJSONValue(file, cellFormatMap.get(i));
                }
                newCell.setCellValue(newCellValue);
            }

            rowNum++;
        }
        workbook.write(fos);
    }*/
}
