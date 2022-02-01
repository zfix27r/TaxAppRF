package com.taxapprf.taxapp.excel;

import android.content.Context;
import android.util.Log;


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
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendExcelStatement {
    private final Context context;
    private final String email;
    private final String year;
    private final Double yearTax;
    private final List<Transaction> transactions;


    private String filePath;

    public SendExcelStatement(Context context, String email, String year, Double yearTax, List<Transaction> transactions) {
        this.context = context;
        this.email = email;
        this.year = year;
        this.yearTax = yearTax;
        this.transactions = transactions;
    }

    public void send() throws InterruptedException {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    createFile(); //io exp
                } catch (IOException e) {
                }
            }
        });
        thread1.start();
        thread1.join();

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendEmail(); //io mess
                    if (!new File(filePath).exists()) {
                        context.deleteFile(filePath); //переместить за пределы функции
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        });
        thread2.start();


        Log.d("OLGA", "send: отправили письмо");
        context.deleteFile(filePath); //переместить за пределы функции

    }

    private void createFile () throws IOException {
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);

        String statementName = "Statement-" + dateText + ".xls";
        filePath = context.getFilesDir() + "/" + statementName;

        FileOutputStream outFile = context.openFileOutput(statementName, Context.MODE_PRIVATE);
        workbook.write(outFile);
        outFile.close();

    }


    private void sendEmail () throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtps");
        properties.put("mail.smtps.host", "smtp.gmail.com");
        properties.put("mail.smtps.auth", "true");
        properties.put("mail.smtp.sendpartial", "true");

        //логин и пароль gmail пользователя
        String userLogin = "taxapprf@gmail.com";
        String userPassword = "mailtaxap";

        Session session = Session.getDefaultInstance(properties);

        //создаем сообщение
        MimeMessage message = new MimeMessage(session);
        //устанавливаем тему письма
        message.setSubject("Расчет налога от TaxApp");
        //указываем получателя
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        //указываем дату отправления
        message.setSentDate(new Date());
        setFileAsAttachment(message, filePath);
        message.saveChanges();
        //авторизуемся на сервере:
        Transport transport = session.getTransport();
        transport.connect("smtp.gmail.com", 465, userLogin, userPassword);
        //отправляем сообщение:
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();



    }

    private static void setFileAsAttachment(Message msg, String filename)
            throws MessagingException {

        // Создание и заполнение первой части
        MimeBodyPart emailText = new MimeBodyPart();
        emailText.setText("Это письмо автоматически сформировано приложением TaxApp. Отчет находится в приложенном файле.");

        // Создание второй части
        MimeBodyPart emailFile = new MimeBodyPart();

        // Добавление файла во вторую часть
        FileDataSource fds = new FileDataSource(filename);
        emailFile.setDataHandler(new DataHandler(fds));
        emailFile.setFileName(fds.getName());

        // Создание экземпляра класса Multipart. Добавление частей сообщения в него.
        Multipart mp = new MimeMultipart();
        mp.addBodyPart(emailText);
        mp.addBodyPart(emailFile);

        // Установка экземпляра класса Multipart в качестве контента документа
        msg.setContent(mp);
    }

}
