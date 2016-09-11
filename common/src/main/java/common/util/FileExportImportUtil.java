//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package common.util;

import org.apache.poi.hssf.usermodel.*;

import javax.swing.*;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class FileExportImportUtil {
    InputStream os;
    List<List<String>> list = new ArrayList();
    public HSSFWorkbook demoWorkBook = new HSSFWorkbook();
    public HSSFSheet demoSheet;
    String name;

    public FileExportImportUtil(String name) {
        this.name = name;
        this.demoSheet = this.demoWorkBook.createSheet(name);
    }

    public void createTableRow(List<String> cells, int rowIndex) {
        HSSFRow row = this.demoSheet.createRow(rowIndex);
        for (int i = 0; i < cells.size(); ++i) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(cells.get(i));
        }

    }

    public void createExcelSheeet() throws SQLException {
        for (int i = 0; i < this.list.size(); ++i) {
            this.createTableRow(this.list.get(i), i);
        }

    }

    public InputStream exportExcel(HSSFSheet sheet) throws IOException {
        sheet.setGridsPrinted(true);
        HSSFFooter footer = sheet.getFooter();
        footer.setRight("Page " + HSSFFooter.page() + " of " + HSSFFooter.numPages());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            this.demoWorkBook.write(baos);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        byte[] ba = baos.toByteArray();
        this.os = new ByteArrayInputStream(ba);
        return this.os;
    }

    public InputStream export(List<List<String>> zlist) {
        InputStream myos = null;

        Object e1;
        try {
            this.list = zlist;
            this.createExcelSheeet();
            myos = this.exportExcel(this.demoSheet);
            InputStream e = myos;
            return e;
        } catch (Exception var14) {
            JOptionPane.showMessageDialog(null, "表格导出出错，错误信息 ：" + var14 + "\n错误原因可能是表格已经打开。");
            var14.printStackTrace();
            e1 = null;
        } finally {
            try {
                this.os.close();
                if (myos != null) {
                    myos.close();
                }
            } catch (Exception var13) {
                var13.printStackTrace();
            }

        }

        return (InputStream) e1;
    }

    public HSSFWorkbook getHSSFWorkbook(List<List<String>> zlist) {
        try {
            this.list = zlist;
            this.createExcelSheeet();
            this.demoSheet.setGridsPrinted(true);
            HSSFFooter e = this.demoSheet.getFooter();
            e.setRight("Page " + HSSFFooter.page() + " of " + HSSFFooter.numPages());
            return this.demoWorkBook;
        } catch (Exception var3) {
            JOptionPane.showMessageDialog( null, "表格导出出错，错误信息 ：" + var3 + "\n错误原因可能是表格已经打开。");
            var3.printStackTrace();
            return null;
        }
    }


    public static File createExcel(String sheetMame, String[] headName, List<List<String>> list, String expName, String dir) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetMame);
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell;

        int file;
        for (file = 0; file < headName.length; ++file) {
            cell = row.createCell(file);
            cell.setCellValue(headName[file]);
        }

        for (file = 0; file < list.size(); ++file) {
            row = sheet.createRow(file + 1);
            List fos = (List) list.get(file);

            for (int i = 0; i < fos.size(); ++i) {
                cell = row.createCell(i);
                cell.setCellValue((String) fos.get(i));
            }
        }

        File var11 = new File(dir + "/" + expName + ".xls");
        if (!(new File(dir)).exists()) {
            (new File(dir)).mkdirs();
        }
        try (FileOutputStream var12 = new FileOutputStream(var11);) {
            workbook.write(var12);
        }

        return var11;
    }

}
