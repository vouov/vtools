package com.vouov.vtools.document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This class contains many utility methods used by jXLS framework
 *
 * @author Leonid Vysochyn
 * @author Vincent Dutat
 */
public final class POIUtil {

    protected static final Log log = LogFactory.getLog(POIUtil.class);
    private static final String[][] ENTITY_ARRAY = {{"quot", "34"}, // " -
            // double
            // -
            // quote
            {"amp", "38"}, // & - ampersand
            {"lt", "60"}, // < - less-than
            {"gt", "62"}, // > - greater-than
            {"apos", "39"} // XML apostrophe
    };
    private static Map xmlEntities = new HashMap();

    static {
        for (int i = 0; i < ENTITY_ARRAY.length; i++) {
            xmlEntities.put(ENTITY_ARRAY[i][1], ENTITY_ARRAY[i][0]);
        }
    }


    private static void removeRowCollectionPropertyFromCell(Cell cell,
                                                            String collectionName) {
        String regex = "[-+*/().A-Za-z_0-9\\s]*";
        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING) {
            String cellValue = cell.getRichStringCellValue().getString();
            String strToReplace = "\\$\\{" + regex
                    + collectionName.replaceAll("\\.", "\\\\.") + "\\." + regex
                    + "\\}";
            cell.setCellValue(cell.getSheet().getWorkbook().getCreationHelper().createRichTextString(cellValue.replaceAll(
                    strToReplace, "")));
        }
    }

    /**
     * Removes merged region from sheet
     *
     * @param sheet
     * @param region
     */
    public static void removeMergedRegion(Sheet sheet,
                                          CellRangeAddress region) {
        int index = getMergedRegionIndex(sheet, region);
        if (index >= 0) {
            sheet.removeMergedRegion(index);
        }
    }

    /**
     * returns merged region index
     *
     * @param sheet
     * @param mergedRegion
     * @return index of mergedRegion or -1 if the region not found
     */
    private static int getMergedRegionIndex(Sheet sheet,
                                            CellRangeAddress mergedRegion) {
        for (int i = 0, c = sheet.getNumMergedRegions(); i < c; i++) {
            CellRangeAddress region = getMergedRegion(sheet, i);
            if (areRegionsEqual(region, mergedRegion)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean areRegionsEqual(CellRangeAddress region1,
                                          CellRangeAddress region2) {
        if ((region1 == null && region2 != null)
                || (region1 != null && region2 == null)) {
            return false;
        }
        if (region1 == null) {
            return true;
        }
        return (region1.getFirstColumn() == region2.getFirstColumn()
                && region1.getLastColumn() == region2.getLastColumn()
                && region1.getFirstRow() == region2.getFirstRow() && region2.getLastRow() == region2.getLastRow());
    }

    private static CellRangeAddress getMergedRegion(Sheet sheet, int i) {
        CellRangeAddress region = sheet.getMergedRegion(i);
        return region;
    }

    protected static boolean isNewMergedRegion(CellRangeAddress region,
                                               Collection mergedRegions) {
        for (Iterator iterator = mergedRegions.iterator(); iterator.hasNext(); ) {
            CellRangeAddress cellRangeAddress = (CellRangeAddress) iterator.next();
            if (areRegionsEqual(cellRangeAddress, region)) {
                return false;
            }
        }
        return true;
    }

    public static CellRangeAddress getMergedRegion(Sheet sheet, int rowNum, int cellNum) {
        for (int i = 0, c = sheet.getNumMergedRegions(); i < c; i++) {
            CellRangeAddress merged = getMergedRegion(sheet, i);
            if (isRangeContainsCell(merged, rowNum, cellNum)) {
                return merged;
            }
        }
        return null;
    }

    public static boolean isRangeContainsCell(CellRangeAddress range, int row, int col) {
        if ((range.getFirstRow() <= row) && (range.getLastRow() >= row)
                && (range.getFirstColumn() <= col)
                && (range.getLastColumn() >= col)) {
            return true;
        }
        return false;
    }

    public static boolean removeMergedRegion(Sheet sheet, int rowNum,
                                             int cellNum) {
        Set mergedRegionNumbersToRemove = new TreeSet();
        for (int i = 0, c = sheet.getNumMergedRegions(); i < c; i++) {
            CellRangeAddress merged = getMergedRegion(sheet, i);
            if (isRangeContainsCell(merged, rowNum, cellNum)) {
                mergedRegionNumbersToRemove.add(i);
            }
        }
        for (Iterator iterator = mergedRegionNumbersToRemove.iterator(); iterator.hasNext(); ) {
            Integer regionNumber = (Integer) iterator.next();
            sheet.removeMergedRegion(regionNumber.intValue());
        }
        return !mergedRegionNumbersToRemove.isEmpty();
    }


    private static void prepareCollectionPropertyInCellForDuplication(
            Cell cell, String collectionName, String collectionItemName) {
        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING) {
            String cellValue = cell.getRichStringCellValue().getString();
            String newValue = replaceCollectionProperty(cellValue,
                    collectionName, collectionItemName);
            // String newValue = cellValue.replaceFirst(collectionName,
            // collectionItemName);
            cell.setCellValue(cell.getSheet().getWorkbook().getCreationHelper().createRichTextString(newValue));
        }
    }

    private static String replaceCollectionProperty(String property,
                                                    String collectionName, String newValue) {
        return property.replaceAll(collectionName, newValue);
    }


    private static void moveCell(Cell srcCell, Cell destCell) {
        destCell.setCellStyle(srcCell.getCellStyle());
        switch (srcCell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                destCell.setCellValue(srcCell.getRichStringCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                destCell.setCellValue(srcCell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
                destCell.setCellType(Cell.CELL_TYPE_BLANK);
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                destCell.setCellValue(srcCell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                destCell.setCellErrorValue(srcCell.getErrorCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                break;
            default:
                break;
        }
        srcCell.setCellType(Cell.CELL_TYPE_BLANK);
    }

    public static void copyRow(Sheet sheet, Row oldRow, Row newRow) {
        Set mergedRegions = new HashSet();
        if (oldRow.getHeight() >= 0) {
            newRow.setHeight(oldRow.getHeight());
        }
        if (oldRow.getFirstCellNum() >= 0 && oldRow.getLastCellNum() >= 0) {
            for (int j = oldRow.getFirstCellNum(), c = oldRow.getLastCellNum(); j <= c; j++) {
                Cell oldCell = oldRow.getCell(j);
                Cell newCell = newRow.getCell(j);
                if (oldCell != null) {
                    if (newCell == null) {
                        newCell = newRow.createCell(j);
                    }
                    copyCell(oldCell, newCell, true);
                    CellRangeAddress mergedRegion = getMergedRegion(sheet, oldRow.getRowNum(), oldCell.getColumnIndex());
                    if (mergedRegion != null) {
                        CellRangeAddress newMergedRegion = new CellRangeAddress(
                                newRow.getRowNum(), newRow.getRowNum()
                                + mergedRegion.getLastRow()
                                - mergedRegion.getFirstRow(), mergedRegion.getFirstColumn(), mergedRegion.getLastColumn());
                        if (isNewMergedRegion(newMergedRegion, mergedRegions)) {
                            mergedRegions.add(newMergedRegion);
                            sheet.addMergedRegion(newMergedRegion);
                        }
                    }
                }
            }
        }
    }

    public static void copyRow(Sheet srcSheet, Sheet destSheet,
                               Row srcRow, Row destRow) {
        Set mergedRegions = new TreeSet();
        if (srcRow.getHeight() >= 0) {
            destRow.setHeight(srcRow.getHeight());
        }
        if (srcRow.getFirstCellNum() >= 0 && srcRow.getLastCellNum() >= 0) {
            for (int j = srcRow.getFirstCellNum(), c = srcRow.getLastCellNum(); j <= c; j++) {
                Cell oldCell = srcRow.getCell(j);
                Cell newCell = destRow.getCell(j);
                if (oldCell != null) {
                    if (newCell == null) {
                        newCell = destRow.createCell(j);
                    }
                    copyCell(oldCell, newCell, true);
                    CellRangeAddress mergedRegion = getMergedRegion(srcSheet,
                            srcRow.getRowNum(), oldCell.getColumnIndex());
                    if (mergedRegion != null) {
                        // Region newMergedRegion = new Region( destRow.getRowNum(),
                        // mergedRegion.getColumnFrom(),
                        // destRow.getRowNum() + mergedRegion.getRowTo() -
                        // mergedRegion.getRowFrom(), mergedRegion.getColumnTo() );
                        CellRangeAddress newMergedRegion = new CellRangeAddress(
                                mergedRegion.getFirstRow(), mergedRegion.getLastRow(), mergedRegion.getFirstColumn(), mergedRegion.getLastColumn());
                        if (isNewMergedRegion(newMergedRegion, mergedRegions)) {
                            mergedRegions.add(newMergedRegion);
                            destSheet.addMergedRegion(newMergedRegion);
                        }
                    }
                }
            }
        }
    }

    public static void copyRow(Sheet srcSheet, Sheet destSheet,
                               Row srcRow, Row destRow, String expressionToReplace,
                               String expressionReplacement) {
        Set mergedRegions = new HashSet();
        if (srcRow.getHeight() >= 0) {
            destRow.setHeight(srcRow.getHeight());
        }
        if (srcRow.getFirstCellNum() >= 0 && srcRow.getLastCellNum() >= 0) {
            for (int j = srcRow.getFirstCellNum(), c = srcRow.getLastCellNum(); j <= c; j++) {
                Cell oldCell = srcRow.getCell(j);
                Cell newCell = destRow.getCell(j);
                if (oldCell != null) {
                    if (newCell == null) {
                        newCell = destRow.createCell(j);
                    }
                    copyCell(oldCell, newCell, true, expressionToReplace,
                            expressionReplacement);
                    CellRangeAddress mergedRegion = getMergedRegion(srcSheet,
                            srcRow.getRowNum(), oldCell.getColumnIndex());
                    if (mergedRegion != null) {
                        // Region newMergedRegion = new Region( destRow.getRowNum(),
                        // mergedRegion.getColumnFrom(),
                        // destRow.getRowNum() + mergedRegion.getRowTo() -
                        // mergedRegion.getRowFrom(), mergedRegion.getColumnTo() );
                        CellRangeAddress newMergedRegion = new CellRangeAddress(
                                mergedRegion.getFirstRow(), mergedRegion.getLastRow(), mergedRegion.getFirstColumn(), mergedRegion.getLastColumn());
                        if (isNewMergedRegion(newMergedRegion, mergedRegions)) {
                            mergedRegions.add(newMergedRegion);
                            destSheet.addMergedRegion(newMergedRegion);
                        }
                    }
                }
            }
        }
    }

    public static void copySheets(Sheet newSheet, Sheet sheet) {
        int maxColumnNum = 0;
        for (int i = sheet.getFirstRowNum(), c = sheet.getLastRowNum(); i <= c; i++) {
            Row srcRow = sheet.getRow(i);
            Row destRow = newSheet.createRow(i);
            if (srcRow != null) {
                POIUtil.copyRow(sheet, newSheet, srcRow, destRow);
                if (srcRow.getLastCellNum() > maxColumnNum) {
                    maxColumnNum = srcRow.getLastCellNum();
                }
            }
        }
        for (int i = 0; i <= maxColumnNum; i++) {
            newSheet.setColumnWidth(i, sheet.getColumnWidth(i));
        }
    }

    public static void copySheets(Sheet newSheet, Sheet sheet,
                                  String expressionToReplace, String expressionReplacement) {
        int maxColumnNum = 0;
        for (int i = sheet.getFirstRowNum(), c = sheet.getLastRowNum(); i <= c; i++) {
            Row srcRow = sheet.getRow(i);
            Row destRow = newSheet.createRow(i);
            if (srcRow != null) {
                POIUtil.copyRow(sheet, newSheet, srcRow, destRow,
                        expressionToReplace, expressionReplacement);
                if (srcRow.getLastCellNum() > maxColumnNum) {
                    maxColumnNum = srcRow.getLastCellNum();
                }
            }
        }
        for (int i = 0; i <= maxColumnNum; i++) {
            newSheet.setColumnWidth(i, sheet.getColumnWidth(i));
        }
    }

    public static void copyCell(Cell oldCell, Cell newCell,
                                boolean copyStyle) {
        if (copyStyle) {
            newCell.setCellStyle(oldCell.getCellStyle());
            copyConditionalFormat(oldCell, newCell);
        }
        switch (oldCell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                newCell.setCellValue(oldCell.getRichStringCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                newCell.setCellValue(oldCell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
                newCell.setCellType(Cell.CELL_TYPE_BLANK);
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                newCell.setCellValue(oldCell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                newCell.setCellErrorValue(oldCell.getErrorCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                newCell.setCellFormula(oldCell.getCellFormula());
                break;
            default:
                break;
        }
    }

    public static void copyCell(Cell oldCell, Cell newCell,
                                boolean copyStyle, String expressionToReplace,
                                String expressionReplacement) {
        if (copyStyle) {
            newCell.setCellStyle(oldCell.getCellStyle());
            copyConditionalFormat(oldCell, newCell);
        }
        switch (oldCell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                String oldValue = oldCell.getRichStringCellValue().getString();
                String newValue = replaceExpressions(oldValue, expressionToReplace, expressionReplacement);
                newCell.setCellValue(newCell.getSheet().getWorkbook().getCreationHelper().createRichTextString(newValue));
                break;
            case Cell.CELL_TYPE_NUMERIC:
                newCell.setCellValue(oldCell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
                newCell.setCellType(Cell.CELL_TYPE_BLANK);
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                newCell.setCellValue(oldCell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                newCell.setCellErrorValue(oldCell.getErrorCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                newCell.setCellFormula(oldCell.getCellFormula());
                break;
            default:
                break;
        }
    }

    static String replaceExpressions(String originalExpression,
                                     String expressionToReplace, String expressionReplacement) {
        /* assuming that:
           - originalExpression may contain expressions of the form ${expr}
           - expressionToReplace is an identifier (alphanumeric + underscore)
           - expressionReplacement is an identifier too
           - only root value may be replaced
        */
        return originalExpression.replaceAll(
                "(\\$\\{)" + expressionToReplace + "((\\b.*)?\\})",
                "$1" + expressionReplacement + "$2");
    }

    public static void copyConditionalFormat(Cell oldCell, Cell newCell) {
        SheetConditionalFormatting cf = oldCell.getSheet().getSheetConditionalFormatting();
        SheetConditionalFormatting ncf = newCell.getSheet().getSheetConditionalFormatting();
        int numCF = cf.getNumConditionalFormattings();
        List<ConditionalFormattingRule> rules = new ArrayList<ConditionalFormattingRule>();
        for (int i = 0; i < numCF; i++) {
            ConditionalFormatting f = cf.getConditionalFormattingAt(i);
            for (CellRangeAddress a : f.getFormattingRanges()) {
                if (a.getNumberOfCells() == 1 && a.isInRange(oldCell.getRowIndex(), oldCell.getColumnIndex())) {
                    int numR = f.getNumberOfRules();
                    for (int j = 0; j < numR; ++j) {
                        try {
                            rules.add(f.getRule(j));
                        } catch (IndexOutOfBoundsException ex) {
                        }
                    }
                }
            }
        }
        if (!rules.isEmpty()) {
            ncf.addConditionalFormatting(new CellRangeAddress[]{new CellRangeAddress(newCell.getRowIndex(), newCell.getRowIndex(), newCell.getColumnIndex(), newCell.getColumnIndex())}, rules.toArray(new ConditionalFormattingRule[0]));
        }
    }


    /**
     * Saves workbook to file
     *
     * @param fileName - File name to save workbook
     * @param workbook - Workbook to save
     */
    public static void writeToFile(String fileName, Workbook workbook) {
        OutputStream os;
        try {
            os = new BufferedOutputStream(new FileOutputStream(fileName));
            workbook.write(os);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Duplicates given CellStyle object
     *
     * @param workbook - source Workbook object
     * @param style    - CellStyle object to duplicate
     * @return CellStyle
     */
    public static CellStyle duplicateStyle(Workbook workbook,
                                           CellStyle style) {
        CellStyle newStyle = workbook.createCellStyle();
        newStyle.setAlignment(style.getAlignment());
        newStyle.setBorderBottom(style.getBorderBottom());
        newStyle.setBorderLeft(style.getBorderLeft());
        newStyle.setBorderRight(style.getBorderRight());
        newStyle.setBorderTop(style.getBorderTop());
        newStyle.setBottomBorderColor(style.getBottomBorderColor());
        newStyle.setDataFormat(style.getDataFormat());
        newStyle.setFillBackgroundColor(style.getFillBackgroundColor());
        newStyle.setFillForegroundColor(style.getFillForegroundColor());
        newStyle.setFillPattern(style.getFillPattern());
        newStyle.setFont(workbook.getFontAt(style.getFontIndex()));
        newStyle.setHidden(style.getHidden());
        newStyle.setIndention(style.getIndention());
        newStyle.setLeftBorderColor(style.getLeftBorderColor());
        newStyle.setLocked(style.getLocked());
        newStyle.setRightBorderColor(style.getRightBorderColor());
        newStyle.setTopBorderColor(style.getTopBorderColor());
        newStyle.setVerticalAlignment(style.getVerticalAlignment());
        newStyle.setWrapText(style.getWrapText());
        return newStyle;
    }

    public static String escapeAttributes(String tag) {
        if (tag == null) {
            return tag;
        }
        int i = 0;
        StringBuffer sb = new StringBuffer("");
        StringBuffer attrValue = new StringBuffer("");
        final char expressionClosingSymbol = '}';
        final char expressionStartSymbol = '{';
        boolean isAttrValue = false;
        int exprCount = 0;
        while (i < tag.length()) {
            if (!isAttrValue) {
                sb.append(tag.charAt(i));
                if (tag.charAt(i) == '\"') {
                    isAttrValue = true;
                    attrValue = new StringBuffer("");
                }
            } else {
                if (tag.charAt(i) == '\"') {
                    if (exprCount != 0) {
                        attrValue.append(tag.charAt(i));
                    } else {
                        sb.append(escapeXml(attrValue.toString()));
                        sb.append(tag.charAt(i));
                        isAttrValue = false;
                    }
                } else {
                    attrValue.append(tag.charAt(i));
                    if (tag.charAt(i) == expressionClosingSymbol) {
                        exprCount--;
                    } else if (tag.charAt(i) == expressionStartSymbol) {
                        exprCount++;
                    }
                }
            }
            i++;
        }
        if (isAttrValue) {
            log.warn("Can't parse ambiguous quot in " + tag);
        }
        return sb.toString();
    }

    /**
     * <p> Escapes XML entities in a
     * <code>String</code>. </p>
     *
     * @param str The
     *            <code>String</code> to escape.
     * @return A new escaped
     * <code>String</code>.
     */
    private static String escapeXml(String str) {
        if (str == null) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str.length() * 2);
        int i;
        for (i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            String entityName = getEntityName(ch);
            if (entityName == null) {
                if (ch > 0x7F) {
                    buf.append("&#");
                    buf.append((int) ch);
                    buf.append(';');
                } else {
                    buf.append(ch);
                }
            } else {
                buf.append('&');
                buf.append(entityName);
                buf.append(';');
            }
        }
        return buf.toString();
    }

    private static String getEntityName(char ch) {
        return (String) xmlEntities.get(Integer.toString(ch));
    }

    protected static void updateMergedRegionInRow(Sheet sheet, Set mergedRegions, int rowNum, int cellNum, int destCellNum, boolean removeSourceMergedRegion) {
        CellRangeAddress mergedRegion = POIUtil.getMergedRegion(sheet, rowNum, cellNum);
        if (mergedRegion != null && POIUtil.isNewMergedRegion(mergedRegion, mergedRegions)) {
            CellRangeAddress newMergedRegion = new CellRangeAddress(
                    mergedRegion.getFirstRow(), mergedRegion.getLastRow(),
                    mergedRegion.getFirstColumn() + destCellNum - cellNum, mergedRegion.getLastColumn() + destCellNum - cellNum);
            if (POIUtil.isNewMergedRegion(newMergedRegion, mergedRegions)) {
                mergedRegions.add(newMergedRegion);
                sheet.addMergedRegion(newMergedRegion);
                if (removeSourceMergedRegion) {
                    removeMergedRegion(sheet, mergedRegion);
                }
            }
        }
    }

    public static void shiftCellsLeft(Sheet sheet, int startRow,
                                      int startCol, int endRow, int endCol, int shiftNumber, boolean removeSourceMergedRegion) {
        Set mergedRegions = new HashSet();
        for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
            boolean doSetWidth = true;
            Row row = sheet.getRow(rowNum);
            if (row != null) {
                for (int colNum = startCol; colNum <= endCol; colNum++) {
                    Cell cell = row.getCell(colNum);
                    if (cell == null) {
                        cell = row.createCell(colNum);
                        doSetWidth = false;
                    }
                    int destColNum = colNum - shiftNumber;
                    Cell destCell = row.getCell(destColNum);
                    if (destCell == null) {
                        destCell = row.createCell(destColNum);
                    }
                    copyCell(cell, destCell, true);
                    POIUtil.updateMergedRegionInRow(sheet, mergedRegions, rowNum, colNum, destColNum, removeSourceMergedRegion);
                    if (doSetWidth) {
                        sheet.setColumnWidth(destCell.getColumnIndex(),
                                getWidth(sheet, cell.getColumnIndex()));
                    }
                    // Folowing 2 lines Added by kiransringeri@gmail.com
                    //Clear cell contents
                    row.removeCell(cell);
                    row.createCell(colNum);
                }
            }
        }
    }

    static int getWidth(Sheet sheet, int col) {
        int width = sheet.getColumnWidth(col);
        if (width == sheet.getDefaultColumnWidth()) {
            width = (int) (width * 256);
        }
        return width;
    }

    public static void shiftCellsRight(Sheet sheet, int startRow,
                                       int endRow, int startCol, int shiftNumber, boolean removeSourceMergedRegion) {
        Set mergedRegions = new HashSet();
        for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row != null) {
                int lastCellNum = row.getLastCellNum();
                for (int colNum = lastCellNum; colNum >= startCol; colNum--) {
                    int destColNum = colNum + shiftNumber;
                    Cell destCell = row.getCell(destColNum);
                    if (destCell == null) {
                        destCell = row.createCell(destColNum);
                    }
                    Cell cell = row.getCell(colNum);
                    if (cell == null) {
                        cell = row.createCell(colNum);
                    }
                    copyCell(cell, destCell, true);
                    POIUtil.updateMergedRegionInRow(sheet, mergedRegions, rowNum, colNum, destColNum, removeSourceMergedRegion);
                    // Folowing 2 lines Added by kiransringeri@gmail.com
                    //Clear cell contents
                    row.removeCell(cell);
                    row.createCell(colNum);
                }
            }
        }
    }

    public static void updateCellValue(Sheet sheet, int rowNum, int colNum,
                                       String cellValue) {
        Row hssfRow = sheet.getRow(rowNum);
        Cell hssfCell = hssfRow.getCell(colNum);
        hssfCell.setCellValue(hssfCell.getSheet().getWorkbook().getCreationHelper().createRichTextString(cellValue));
    }

    public static void copyPageSetup(Sheet destSheet, Sheet srcSheet) {
        Header header = srcSheet.getHeader();
        Footer footer = srcSheet.getFooter();
        if (footer != null) {
            destSheet.getFooter().setLeft(footer.getLeft());
            destSheet.getFooter().setCenter(footer.getCenter());
            destSheet.getFooter().setRight(footer.getRight());
        }
        if (header != null) {
            destSheet.getHeader().setLeft(header.getLeft());
            destSheet.getHeader().setCenter(header.getCenter());
            destSheet.getHeader().setRight(header.getRight());
        }
    }

    public static void copyConditionalFormatting(Sheet destSheet, Sheet srcSheet) {
        SheetConditionalFormatting cf = srcSheet.getSheetConditionalFormatting();
        int numCF = cf.getNumConditionalFormattings();
        for (int i = 0; i < numCF; i++) {
            destSheet.getSheetConditionalFormatting().addConditionalFormatting(cf.getConditionalFormattingAt(i));
        }
    }

    public static void copyPrintSetup(Sheet destSheet, Sheet srcSheet) {
        PrintSetup setup = srcSheet.getPrintSetup();
        if (setup != null) {
            destSheet.getPrintSetup().setLandscape(setup.getLandscape());
            destSheet.getPrintSetup().setPaperSize(setup.getPaperSize());
            destSheet.getPrintSetup().setScale(setup.getScale());
            destSheet.getPrintSetup().setFitWidth(setup.getFitWidth());
            destSheet.getPrintSetup().setFitHeight(setup.getFitHeight());
            destSheet.getPrintSetup().setFooterMargin(setup.getFooterMargin());
            destSheet.getPrintSetup().setHeaderMargin(setup.getHeaderMargin());
            destSheet.getPrintSetup().setPaperSize(setup.getPaperSize());
            destSheet.getPrintSetup().setPageStart(setup.getPageStart());
        }
    }

    public static void setPrintArea(Workbook resultWorkbook, int sheetNum) {
        int maxColumnNum = 0;
        for (int j = resultWorkbook.getSheetAt(sheetNum).getFirstRowNum(), c = resultWorkbook.getSheetAt(sheetNum).getLastRowNum(); j <= c; j++) {
            Row row = resultWorkbook.getSheetAt(sheetNum).getRow(j);
            if (row != null) {
                maxColumnNum = Math.max(maxColumnNum, row.getLastCellNum());
            }
        }
        resultWorkbook.setPrintArea(sheetNum, 0, maxColumnNum, 0,
                resultWorkbook.getSheetAt(sheetNum).getLastRowNum());
    }

    protected static final String regexCellRef = "[a-zA-Z]+[0-9]+";
    protected static final Pattern regexCellRefPattern = Pattern.compile(regexCellRef);
    protected static final String regexCellCharPart = "[0-9]+";
    protected static final String regexCellDigitPart = "[a-zA-Z]+";
    protected static final String cellRangeSeparator = ":";

    public static boolean isColumnRange(List cells) {
        String firstCell = (String) cells.get(0);
        boolean isColumnRange = true;
        if (firstCell != null && firstCell.length() > 0) {
            String firstCellCharPart = firstCell.split(regexCellCharPart)[0];
            String firstCellDigitPart = firstCell.split(regexCellDigitPart)[1];
            int cellNumber = Integer.parseInt(firstCellDigitPart);
            String nextCell, cellCharPart, cellDigitPart;
            for (int i = 1, c = cells.size(); i < c && isColumnRange; i++) {
                nextCell = (String) cells.get(i);
                cellCharPart = nextCell.split(regexCellCharPart)[0];
                cellDigitPart = nextCell.split(regexCellDigitPart)[1];
                if (!firstCellCharPart.equalsIgnoreCase(cellCharPart)
                        || Integer.parseInt(cellDigitPart) != ++cellNumber) {
                    isColumnRange = false;
                }
            }
        }
        return isColumnRange;
    }

    public static boolean isRowRange(List cells) {
        String firstCell = (String) cells.get(0);
        boolean isRowRange = true;
        if (firstCell != null && firstCell.length() > 0) {
            String firstCellDigitPart = firstCell.split(regexCellDigitPart)[1];
            String nextCell, cellDigitPart;
            CellReference cellRef = new CellReference(firstCell);
            int cellNumber = cellRef.getCol();
            for (int i = 1, c = cells.size(); i < c && isRowRange; i++) {
                nextCell = (String) cells.get(i);
                cellDigitPart = nextCell.split(regexCellDigitPart)[1];
                cellRef = new CellReference(nextCell);
                if (!firstCellDigitPart.equalsIgnoreCase(cellDigitPart)
                        || cellRef.getCol() != ++cellNumber) {
                    isRowRange = false;
                }
            }
        }
        return isRowRange;
    }

    public static String buildCommaSeparatedListOfCells(String refSheetName,
                                                        List cells) {
        StringBuilder listOfCellsBuilder = new StringBuilder();
        for (int i = 0, c = cells.size() - 1; i < c; i++) {
            String cell = (String) cells.get(i);
            listOfCellsBuilder.append(getRefCellName(refSheetName, cell));
            listOfCellsBuilder.append(",");
        }
        listOfCellsBuilder.append(getRefCellName(refSheetName, (String) cells.get(cells.size() - 1)));
        return listOfCellsBuilder.toString();
    }

    public static String detectCellRange(String refSheetName, List cells) {
        if (cells == null || cells.isEmpty()) {
            return "";
        }
        String firstCell = (String) cells.get(0);
        String range = firstCell;
        if (firstCell != null && firstCell.length() > 0) {
            if (isRowRange(cells) || isColumnRange(cells)) {
                String lastCell = (String) cells.get(cells.size() - 1);
                range = getRefCellName(refSheetName, firstCell)
                        + cellRangeSeparator + lastCell.toUpperCase();
            } else {
                range = buildCommaSeparatedListOfCells(refSheetName, cells);
            }
        }
        return range;
    }

    public static String getRefCellName(String refSheetName, String cellName) {
        if (refSheetName == null) {
            return cellName.toUpperCase();
        }
        return refSheetName + "!" + cellName.toUpperCase();
    }

    public static void shiftRows(Sheet sheet, int startRow, int endRow,
                                 int shiftNum) {
        if (startRow <= endRow) {
            short[] rowHeights = getRowHeights(sheet, startRow, endRow);
            sheet.shiftRows(startRow, endRow, shiftNum, false, false);
            copyPositiveRowHeight(sheet, startRow, endRow, shiftNum, rowHeights);
        }
    }

    private static short[] getRowHeights(Sheet sheet, int startRow,
                                         int endRow) {
        if (endRow - startRow + 1 < 0) {
            return new short[0];
        }
        short[] rowHeights = new short[endRow - startRow + 1];
        for (int i = startRow; i <= endRow; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                rowHeights[i - startRow] = row.getHeight();
            } else {
                rowHeights[i - startRow] = -1;
            }
        }
        return rowHeights;
    }

    static void copyPositiveRowHeight(Sheet sheet, int startRow,
                                      int endRow, int shiftNum, short[] rowHeights) {
        for (int i = startRow; i <= endRow; i++) {
            Row destRow = sheet.getRow(i + shiftNum);
            if (destRow != null && rowHeights[i - startRow] >= 0) {
                destRow.setHeight(rowHeights[i - startRow]);
            }
            // Row srcRow = sheet.getRow(i);
            // if( srcRow != null && destRow != null ){
            // if( srcRow.getHeight() >= 0 ){
            // destRow.setHeight( srcRow.getHeight() );
            // }
            // }
        }
    }

    public static Cell getOrCreateCell(Sheet poiSheet, Integer rowNum, Integer cellNum) {
        Row row = poiSheet.getRow(rowNum.intValue());
        if (row == null) {
            row = poiSheet.createRow(rowNum.intValue());
        }
        Cell cell = row.getCell(cellNum.intValue(), Row.CREATE_NULL_AS_BLANK);
        return cell;
    }
}
