package com.taxapprf.taxapp.excel;


import android.text.TextUtils;
import android.util.Log;

import com.taxapprf.taxapp.usersdata.Transaction;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ParseExcel {
    private String filePath;
    private final String TAG = "OLGA";

    public ParseExcel(String filePath){
        this.filePath = filePath;
    }


    public List<Transaction> parse() throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        FileInputStream fileInputStream = null;
        if (TextUtils.isEmpty(filePath)) {
            Log.d(TAG, "parse: !TextUtils.isEmpty(filePath) " + !TextUtils.isEmpty(filePath));
            throw new IOException();
        }
        if (filePath.contains("raw:")) {
            filePath = filePath.replaceFirst(".+raw:", "");
            Log.d(TAG, "parse: filePath " + filePath);
        }
        //fileInputStream = new FileInputStream(new File("storage/emulated/0/Download/Custom_2019-01-01_2019-12-31.xls"));
        fileInputStream = new FileInputStream(new File(filePath));

        HSSFWorkbook workBook =  new HSSFWorkbook(fileInputStream);
        Sheet sheet = workBook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
                String id;
                String type;
                String date;
                Double sum;
                String currency;
                int count = 5;

                Iterator<Cell> cells = row.iterator();
                Transaction transaction = new Transaction();
                try {
                    for (int i = 0; i < count; i++) {

                        Cell cell = cells.next();
                        switch (i) {
                            case 0:
                                id = cell.getStringCellValue();
                                transaction.setId(id);
                                break;
                            case 1:
                                type = cell.getStringCellValue();
                                transaction.setType(type);
                                break;
                            case 2:
                                if (cell.getCellType() == CellType.NUMERIC) {
                                    Date currentDate = cell.getDateCellValue();
                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    date = dateFormat.format(currentDate);
                                    transaction.setDate(date);
                                    break;
                                } else {
                                    date = cell.getStringCellValue();
                                    date.replaceAll("\\.", "/");
                                    transaction.setDate(date);
                                    break;
                                }
                            case 3:
                                sum = cell.getNumericCellValue();
                                transaction.setSum(sum);
                                break;
                            case 4:
                                currency = cell.getStringCellValue();
                                transaction.setCurrency(currency);
                                break;
                        }
                    }
                    transactions.add(transaction);
                } catch (Exception e) {
                    //....
                }
            //}
        }
        return transactions;
    }
}
