package com.taxapprf.taxapp.ui.transactions;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.taxapprf.taxapp.firebase.FirebaseTransactions;
import com.taxapprf.taxapp.firebase.FirebaseYearStatements;
import com.taxapprf.taxapp.firebase.FirebaseYearSum;
import com.taxapprf.taxapp.firebase.UserLivaData;
import com.taxapprf.taxapp.usersdata.Settings;
import com.taxapprf.taxapp.usersdata.Transaction;
import com.taxapprf.taxapp.usersdata.YearStatement;

import java.util.List;

public class  TransactionsViewModel extends AndroidViewModel {
    private SharedPreferences settings;

    private MutableLiveData<List<String>> keys;
    private MutableLiveData<List<Transaction>> transactions;
    private MutableLiveData<Double> sumTaxes;

    public TransactionsViewModel(@NonNull Application application) {
        super(application);

        transactions = new MutableLiveData<List<Transaction>>();
        sumTaxes = new MutableLiveData<Double>();
        keys = new MutableLiveData<List<String>>();

        settings = getApplication().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
        String account = settings.getString(Settings.ACCOUNT.name(), "");
        String year = settings.getString(Settings.YEAR.name(), "");

        new FirebaseTransactions(new UserLivaData().getFirebaseUser(), account).readTransactions(year, new FirebaseTransactions.DataStatus() {
            @Override
            public void DataIsLoaded(List<Transaction> transactionsDB, List<String> keysDB) {
                transactions.setValue(transactionsDB);
                keys.setValue(keysDB);
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
