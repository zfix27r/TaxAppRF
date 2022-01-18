package com.taxapprf.taxapp.ui.newtransaction;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.taxapprf.taxapp.firebase.FirebaseTransactions;
import com.taxapprf.taxapp.firebase.FirebaseYearSum;
import com.taxapprf.taxapp.firebase.UserLivaData;
import com.taxapprf.taxapp.retrofit2.Controller;
import com.taxapprf.taxapp.retrofit2.Currencies;
import com.taxapprf.taxapp.usersdata.Settings;
import com.taxapprf.taxapp.usersdata.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewTransactionViewModel extends AndroidViewModel {
    private MutableLiveData<String> message;
    private Double rateCentralBankDouble;
    private Transaction transaction;

    public NewTransactionViewModel(@NonNull Application application) {
        super(application);
        message = new MutableLiveData<>();
    }

    public void addTransaction(String year, Transaction trans) {
        Log.d("OLGA", "addTransaction: вошли11111");
        this.transaction = trans;
        Controller ctrl = new Controller(transaction.getDate());
        Call<Currencies> currenciesCall = ctrl.prepareCurrenciesCall();
        currenciesCall.enqueue(new Callback<Currencies>() {
            @Override
            public void onResponse(Call<Currencies> call, Response<Currencies> response) {
                if (response.isSuccessful()) {
                    Log.d("OLGA", "onResponse: ok2222222222");
                    Currencies cur = response.body();
                    rateCentralBankDouble = cur.getCurrencyRate(transaction.getCurrency());
                    transaction.setRateCentralBank(rateCentralBankDouble);
                    Double sumRubDouble = transaction.getSum() * rateCentralBankDouble * 0.13;
                    transaction.setSumRub(sumRubDouble);
                    addToFirebase(year, transaction);
                } else {
                    message.setValue("Не удалось загрузить курс валюты. Сделка не добавлена!");
                }
            }

            @Override
            public void onFailure(Call<Currencies> call, Throwable t) {
                message.setValue("Не удалось загрузить курс валюты. Сделка не добавлена!");
            }
        });
    }

    private void addToFirebase(String year, Transaction transaction){
        Log.d("OLGA", "addToFirebase: ok333333");
        SharedPreferences settings = getApplication().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
        String account = settings.getString(Settings.ACCOUNT.name(), "");
        new FirebaseTransactions(new UserLivaData().getFirebaseUser(), account).addTransaction(year, transaction, new FirebaseTransactions.DataStatus() {
            @Override
            public void DataIsLoaded(List<Transaction> transactions, List<String> keys) {
            }

            @Override
            public void DataIsInserted() {
                message.setValue("Сделка добавлена!");
            }

            @Override
            public void DataIsUpdated() {
            }

            @Override
            public void DataIsDeleted() {
            }
        });
        new FirebaseYearSum(new UserLivaData().getFirebaseUser(), account).readYearSumOnce(year, new FirebaseYearSum.DataStatus() {
            @Override
            public void DataIsLoaded(Double sumTaxes) {
                Double oldSumYear = sumTaxes;
                Double currentSumYear = oldSumYear + transaction.getSumRub();
                new FirebaseYearSum(new UserLivaData().getFirebaseUser(), account).updateYearSum(year, currentSumYear);
            }
        });
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }
}
