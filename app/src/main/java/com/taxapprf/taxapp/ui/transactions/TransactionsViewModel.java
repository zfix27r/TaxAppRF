package com.taxapprf.taxapp.ui.transactions;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.taxapprf.taxapp.excel.CreateExcelInDownload;
import com.taxapprf.taxapp.excel.CreateExcelInLocal;
import com.taxapprf.taxapp.firebase.FirebaseTransactions;
import com.taxapprf.taxapp.firebase.FirebaseYearStatements;
import com.taxapprf.taxapp.firebase.FirebaseYearSum;
import com.taxapprf.taxapp.firebase.UserLivaData;
import com.taxapprf.taxapp.usersdata.Settings;
import com.taxapprf.taxapp.usersdata.Transaction;
import com.taxapprf.taxapp.usersdata.YearStatement;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class  TransactionsViewModel extends AndroidViewModel {
    private SharedPreferences settings;
    private String year;

    private MutableLiveData<List<String>> keys;
    private MutableLiveData<List<Transaction>> transactions;
    private MutableLiveData<Double> sumTaxes;
    private List<String> mkeys;
    private List<Transaction> mtransactions;
    private Double msumTaxes;

    public TransactionsViewModel(@NonNull Application application) {
        super(application);

        transactions = new MutableLiveData<List<Transaction>>();
        sumTaxes = new MutableLiveData<Double>();
        keys = new MutableLiveData<List<String>>();

        settings = getApplication().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
        String account = settings.getString(Settings.ACCOUNT.name(), "");
        year = settings.getString(Settings.YEAR.name(), "");

        new FirebaseTransactions(new UserLivaData().getFirebaseUser(), account).readTransactions(year, new FirebaseTransactions.DataStatus() {
            @Override
            public void DataIsLoaded(List<Transaction> transactionsDB) {
                transactions.setValue(transactionsDB);
                mtransactions = transactionsDB;
                //keys.setValue(keysDB);
                //mkeys = keysDB;
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });

        new FirebaseYearSum(new UserLivaData().getFirebaseUser(), account).readYearSum(year, new FirebaseYearSum.DataStatus() {
            @Override
            public void DataIsLoaded(Double sumTaxesDB) {
                sumTaxes.setValue(sumTaxesDB);
                msumTaxes = sumTaxesDB;
            }
        });
    }

    public void deleteYear (String year) {
        String account = settings.getString(Settings.ACCOUNT.name(), "");
        new FirebaseYearStatements(new UserLivaData().getFirebaseUser(), account).deleteYearStatement(year, new FirebaseYearStatements.DataStatus() {
            @Override
            public void DataIsLoaded(List<YearStatement> yearStatements) {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }

    public File downloadStatement (){
        if (!new CheckPermission(getApplication()).isStoragePermissionGranted()){
            return null;
        }
        year = settings.getString(Settings.YEAR.name(), "");
        try {
            CreateExcelInDownload excelStatement = new CreateExcelInDownload(year, msumTaxes, mtransactions);
            File file = excelStatement.create();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public File createLocalStatement(){
        if (!new CheckPermission(getApplication()).isStoragePermissionGranted()){
            return null;
        }
        try {
            CreateExcelInLocal excelStatement = new CreateExcelInLocal(getApplication(), year, msumTaxes, mtransactions);
            File file = null;
            file = excelStatement.create();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


    public List<String> getMkeys() {
        return mkeys;
    }

    public List<Transaction> getMtransactions() {
        return mtransactions;
    }

    public Double getMsumTaxes() {
        return msumTaxes;
    }

    public MutableLiveData<List<String>> getKeys() {
        return keys;
    }

    public MutableLiveData<List<Transaction>> getTransactions() {
        return transactions;
    }

    public MutableLiveData<Double> getSumTaxes() {
        return sumTaxes;
    }
}
