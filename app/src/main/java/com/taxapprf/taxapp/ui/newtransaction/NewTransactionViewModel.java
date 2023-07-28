package com.taxapprf.taxapp.ui.newtransaction;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.taxapprf.taxapp.firebase.FirebaseTransactions;
import com.taxapprf.taxapp.firebase.FirebaseYearSum;
import com.taxapprf.taxapp.firebase.UserLivaData;
import com.taxapprf.taxapp.retrofit2.Controller;
import com.taxapprf.taxapp.retrofit2.Currencies;
import com.taxapprf.taxapp.usersdata.Settings;
import com.taxapprf.domain.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        this.transaction = trans;
        Controller ctrl = new Controller(transaction.getDate());
        Call<Currencies> currenciesCall = ctrl.prepareCurrenciesCall();
        currenciesCall.enqueue(new Callback<Currencies>() {
            @Override
            public void onResponse(Call<Currencies> call, Response<Currencies> response) {
                if (response.isSuccessful()) {
                    Currencies cur = response.body();
                    rateCentralBankDouble = cur.getCurrencyRate(transaction.getCurrency());
                    transaction.setRateCentralBank(rateCentralBankDouble);
                    Double sum = transaction.getSum();
                    int k;
                    switch (transaction.getType()){
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
                        default: k = 1;
                    }

                    BigDecimal sumRubBigDecimal = new BigDecimal(sum * rateCentralBankDouble * 0.13 * k);
                    sumRubBigDecimal = sumRubBigDecimal.setScale(2, RoundingMode.HALF_UP);
                    Double sumRubDouble = sumRubBigDecimal.doubleValue();
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
        SharedPreferences settings = getApplication().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
        String account = settings.getString(Settings.ACCOUNT.name(), "");
        new FirebaseTransactions(new UserLivaData().getFirebaseUser(), account).addTransaction(year, transaction, new FirebaseTransactions.DataStatus() {
            @Override
            public void DataIsLoaded(List<Transaction> transactions) {
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
                BigDecimal currentSumYearBigDecimal = new BigDecimal(oldSumYear + transaction.getSumRub());
                currentSumYearBigDecimal = currentSumYearBigDecimal.setScale(2, RoundingMode.HALF_UP);
                Double currentSumYear = currentSumYearBigDecimal.doubleValue();
                new FirebaseYearSum(new UserLivaData().getFirebaseUser(), account).updateYearSum(year, currentSumYear);
            }
        });
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }
}
