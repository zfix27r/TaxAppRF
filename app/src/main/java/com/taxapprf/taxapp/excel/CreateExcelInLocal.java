package com.taxapprf.taxapp.excel;

import android.content.Context;
import com.taxapprf.taxapp.usersdata.Transaction;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateExcelInLocal {
    private final Context context;
    private final String year;
    private final Double yearTax;
    private final List<Transaction> transactions;


    public CreateExcelInLocal(Context context, String year, Double yearTax, List<Transaction> transactions) {
        this.context = context;
        this.year = year;
        this.yearTax = yearTax;
        this.transactions = transactions;
    }

    public File create () throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Отчет");

        int rownum = 0;
        Cell cell;
        Row row;
        row = sheet.createRow(rownum);

        //1
        cell = row.createCell(0, CellType.STRING);
        String s = String.format("Расчет налога за %s год", year);
        cell.setCellValue(s);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        rownum++;
        row = sheet.createRow(rownum);

        //2
        cell = row.createCell(0, CellType.STRING);
        String ss = String.format("Сумма налога составляет: %.2f руб.", yearTax);
        cell.setCellValue(ss);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

        rownum++;
        row = sheet.createRow(rownum);

        //3.1
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Обозначение сделки");

        //3.2
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Тип операции");

        //3.3
        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Дата");

        //3.4
        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Сумма");

        //3.5
        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Актив");

        //3.6
        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Курс ЦБ РФ");

        //3.7
        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Налог, руб.");

        for (Transaction transaction: transactions) {
            rownum++;
            row = sheet.createRow(rownum);

            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(transaction.getId());

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(transaction.getType());

            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(transaction.getDate());

            cell = row.createCell(3, CellType.NUMERIC);
            cell.setCellValue(transaction.getSum());

            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(transaction.getCurrency());

            cell = row.createCell(5, CellType.NUMERIC);
            cell.setCellValue(transaction.getRateCentralBank());

            cell = row.createCell(6, CellType.NUMERIC);
            cell.setCellValue(transaction.getSumRub());
        }
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);

        String statementName = "Statement-" + dateText + ".xls";


        String filePath = context.getFilesDir() + "/" + statementName;
        FileOutputStream outFile = context.openFileOutput(statementName, Context.MODE_PRIVATE);
        workbook.write(outFile);
        outFile.close();
        File file = new File(filePath);
        return file;
    }
}
