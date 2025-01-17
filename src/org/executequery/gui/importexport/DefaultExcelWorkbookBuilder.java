/*
 * DefaultExcelWorkbookBuilder.java
 *
 * Copyright (C) 2002-2017 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.executequery.gui.importexport;

import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Takis Diakoumis, Pawel Bialkowski
 */
public class DefaultExcelWorkbookBuilder implements ExcelWorkbookBuilder {

    private int currentRow;

    private final XSSFWorkbook workbook;

    private XSSFSheet currentSheet;

    private final XSSFCellStyle defaultCellStyle;

    public DefaultExcelWorkbookBuilder() {

        workbook = new XSSFWorkbook();
        defaultCellStyle = createStyle();
    }

    public void reset() {

        currentRow = 0;
        currentSheet = null;
    }

    public void writeTo(OutputStream outputStream) throws IOException {

        workbook.write(outputStream);
    }

    public void createSheet(String sheetName) {

        currentSheet = workbook.createSheet(sheetName);
    }

    public void addRow(List<String> values) {

        fillRow(values, createRow(++currentRow), defaultCellStyle);
    }

    public void addRowHeader(List<String> values) {

        if (currentRow > 0) {

            currentRow++;
        }

        XSSFFont font = createFont();
        font.setBold(true);

        XSSFCellStyle style = createStyle();
        style.setFont(font);

        fillRow(values, createRow(currentRow), style);
    }

    private XSSFRow createRow(int rowNumber) {

        return currentSheet.createRow(rowNumber);
    }

    private void fillRow(List<String> values, XSSFRow row, XSSFCellStyle style) {

        for (int i = 0, n = values.size(); i < n; i++) {

            XSSFCell cell = row.createCell(i);

            // set encoding no longer supported in POI 3.2
//            cell.setEncoding(XSSFCell.ENCODING_UTF_16);

            cell.setCellStyle(style);
            cell.setCellValue(new XSSFRichTextString(values.get(i)));
        }

    }

    private XSSFCellStyle createStyle() {

        return workbook.createCellStyle();
    }

    private XSSFFont createFont() {

        return workbook.createFont();
    }

}







