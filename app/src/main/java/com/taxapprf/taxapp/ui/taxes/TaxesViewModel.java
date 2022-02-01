package com.taxapprf.taxapp.ui.taxes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.taxapprf.taxapp.excel.ParseExcel;
import com.taxapprf.taxapp.firebase.FirebaseTransactions;
import com.taxapprf.taxapp.firebase.FirebaseYearStatements;
import com.taxapprf.taxapp.firebase.UserLivaData;
import com.taxapprf.taxapp.retrofit2.Controller;
import com.taxapprf.taxapp.retrofit2.Currencies;
import com.taxapprf.taxapp.ui.newtransaction.DateCheck;
import com.taxapprf.taxapp.usersdata.Settings;
import com.taxapprf.taxapp.usersdata.Transaction;
import com.taxapprf.taxapp.usersdata.YearStatement;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaxesViewModel extends AndroidViewModel {
    private final SharedPreferences settings;
    private MutableLiveData<List<YearStatement>> yearStatements;
    private final FirebaseYearStatements firebaseYearStatements;

    public TaxesViewModel(@NonNull Application application) {
        super(application);

        yearStatements = new MutableLiveData<List<YearStatement>>();
        settings = getApplication().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
        String account = settings.getString(Settings.ACCOUNT.name(), "");

        firebaseYearStatements = new FirebaseYearStatements(new UserLivaData().getFirebaseUser(), account);
        createListener();
    }

    public MutableLiveData<List<YearStatement>> getYearStatements() {
        return yearStatements;
    }

    public void removeListener(){
        firebaseYearStatements.removeListener();
    }

    public void createListener(){
        firebaseYearStatements.readYearStatements(new FirebaseYearStatements.DataStatus() {
            @Override
            public void DataIsLoaded(List<YearStatement> statements) {
                Collections.reverse(statements);
                yearStatements.setValue(statements);
            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }

    public void addTransactions(String filePath) throws IOException {
        SharedPreferences settings = getApplication()
                .getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
        String account = settings.getString(Settings.ACCOUNT.name(), "");
        Runnable task = new Runnable() {
            @Override
            public void run() {
                List<Transaction> transactions = null;
                try {
                    transactions = new ParseExcel(filePath).parse();
                } catch (IOException e) {
                    //...обработать
                    return;
                }
                for (Transaction transaction: transactions)
                {
                    String year = new DateCheck(transaction.getDate()).getYear();
                    Controller ctrl = new Controller(transaction.getDate());
                    Call<Currencies> currenciesCall = ctrl.prepareCurrenciesCall();
                    currenciesCall.enqueue(new Callback<Currencies>() {
                        @Override
                        public void onResponse(Call<Currencies> call, Response<Currencies> response) {
                            if (response.isSuccessful()) {
                                Currencies cur = response.body();
                                Double rateCentralBankDouble = cur.getCurrencyRate(transaction.getCurrency());
                                transaction.setRateCentralBank(rateCentralBankDouble);
                                Double sum = transaction.getSum();
                                int k;
                                switch (transaction.getType()) {
                                    case "TRADE":
                                        k = 1;
                                        break;
                                    case "FUNDING/WITHDRAWAL":
                                        k = 0;
                                        break;
                                    case "COMMISSION":
                                        sum = Math.abs(sum);
                                        k = -1;
                                        break;
                                    default:
                                        k = 1;
                                }

                                BigDecimal sumRubBigDecimal = new BigDecimal(sum * rateCentralBankDouble * 0.13 * k);
                                sumRubBigDecimal = sumRubBigDecimal.setScale(2, RoundingMode.HALF_UP);
                                Double sumRubDouble = sumRubBigDecimal.doubleValue();
                                transaction.setSumRub(sumRubDouble);
                                addToFirebase(account, year, transaction);
                            } else {
                                //...
                            }
                        }

                        @Override
                        public void onFailure(Call<Currencies> call, Throwable t) {
                            //message.setValue("Не удалось загрузить курс валюты. Сделка не добавлена!");
                        }
                    });
                }
                //calculateSum(years);
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private void addToFirebase(String account, String year, Transaction transaction) {
        new FirebaseTransactions(new UserLivaData().getFirebaseUser(), account)
                .addTransaction(year, transaction, new FirebaseTransactions.DataStatus() {
            @Override
            public void DataIsLoaded(List<Transaction> transactions) {
            }

            @Override
            public void DataIsInserted() {
                //message.setValue("Сделка добавлена!");
                calculateSum(account, year);
            }

            @Override
            public void DataIsUpdated() {
            }

            @Override
            public void DataIsDeleted() {
            }
        });
    }

    public void calculateSum (String account, String year) {
        new FirebaseTransactions(new UserLivaData().getFirebaseUser(), account).sumTransaction(year);
    }
}

