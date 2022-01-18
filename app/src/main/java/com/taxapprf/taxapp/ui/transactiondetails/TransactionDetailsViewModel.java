package com.taxapprf.taxapp.ui.transactiondetails;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.taxapprf.taxapp.firebase.FirebaseTransactions;
import com.taxapprf.taxapp.firebase.FirebaseYearStatements;
import com.taxapprf.taxapp.firebase.FirebaseYearSum;
import com.taxapprf.taxapp.firebase.UserLivaData;
import com.taxapprf.taxapp.retrofit2.Controller;
import com.taxapprf.taxapp.retrofit2.Currencies;
import com.taxapprf.taxapp.usersdata.Settings;
import com.taxapprf.taxapp.usersdata.Transaction;
import com.taxapprf.taxapp.usersdata.YearStatement;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionDetailsViewModel extends AndroidViewModel {
    private MutableLiveData<String> message;
    private MutableLiveData<Boolean> isSuccessUpdate;
    private MutableLiveData<Boolean> isSuccessDelete;
    //private Double oldYearSum;
    private Double currentYearSum;
    private boolean deleteFlag = false;

    public TransactionDetailsViewModel(@NonNull Application application) {
        super(application);
        message = new MutableLiveData<>();
        isSuccessUpdate = new MutableLiveData<>(false);
        isSuccessDelete = new MutableLiveData<>(false);
    }

    public void deleteTransaction (String oldYear, String key, Double oldSumRub) {
        deleteFlag = true;
        deleteFromFirebase(oldYear, key, oldSumRub);

    }

    public void updateTransaction (Transaction transaction, String oldYear, String currentYear, String key, Double oldSumRub){
        if (!oldYear.equals(currentYear)) {
            deleteFromFirebase(oldYear, key, oldSumRub);
            addToFirebase(currentYear, transaction);
        } else {
            updateToFirebase(transaction, oldYear, key, oldSumRub);
        }
    }

    private void deleteFromFirebase (String year, String key, Double oldSumRub){
        isSuccessDelete.setValue(false);
        isSuccessUpdate.setValue(false);
        SharedPreferences settings = getApplication().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
        String account = settings.getString(Settings.ACCOUNT.name(), "");
        FirebaseUser user = new UserLivaData().getFirebaseUser();
        new FirebaseYearSum(user, account).readYearSumOnce(year, new FirebaseYearSum.DataStatus() {
            @Override
            public void DataIsLoaded(Double sumTaxes) {
                Log.d("OLGA", "DataIsLoaded: sumTaxes" + sumTaxes.toString());
                Double oldYearSum = sumTaxes;
                currentYearSum = oldYearSum - oldSumRub;
                Log.d("OLGA", "DataIsLoaded: currentYearSum " + currentYearSum.toString());
                if (currentYearSum == 0) {
                    new FirebaseYearStatements(user, account).deleteYearStatement(year, new FirebaseYearStatements.DataStatus() {
                        @Override
                        public void DataIsLoaded(List<YearStatement> yearStatements) {
                        }

                        @Override
                        public void DataIsDeleted() {
                        }
                    });
                } else {
                    new FirebaseYearSum(user, account).updateYearSum(year, currentYearSum);
                }

            }
        });



        new FirebaseTransactions(user, account).deleteTransaction(year, key, new FirebaseTransactions.DataStatus() {
            @Override
            public void DataIsLoaded(List<Transaction> transactions, List<String> keys) {
            }

            @Override
            public void DataIsInserted() {
            }

            @Override
            public void DataIsUpdated() {
            }

            @Override
            public void DataIsDeleted() {
                message.setValue("Сделка удалена");
                if (deleteFlag) {
                    isSuccessDelete.setValue(true);
                    deleteFlag = false;
                }
            }
        });
    }

    private void updateToFirebase (Transaction trans, String year, String key, Double oldSumRub) {
        isSuccessDelete.setValue(false);
        isSuccessUpdate.setValue(false);
        SharedPreferences settings = getApplication().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
        String account = settings.getString(Settings.ACCOUNT.name(), "");
        Log.d("OLGA", "updateToFirebase: account: " + account);
        FirebaseUser user = new UserLivaData().getFirebaseUser();

        Transaction transaction = trans;
        String date = transaction.getDate();
        String currency = transaction.getCurrency();
        Double sumDouble = transaction.getSum();

        Controller ctrl = new Controller(date);
        Call<Currencies> currenciesCall = ctrl.prepareCurrenciesCall();
        currenciesCall.enqueue(new Callback<Currencies>() {
            @Override
            public void onResponse(Call<Currencies> call, Response<Currencies> response) {
                Double rateCentralBankDouble;
                if (response.isSuccessful()) {
                    Currencies cur = response.body();
                    rateCentralBankDouble = cur.getCurrencyRate(currency);
                    transaction.setRateCentralBank(rateCentralBankDouble);
                    Double sumRubDouble = sumDouble * rateCentralBankDouble * 0.13;
                    transaction.setSumRub(sumRubDouble);

                    new FirebaseTransactions(user, account).updateTransaction(year, key, transaction, new FirebaseTransactions.DataStatus() {
                        @Override
                        public void DataIsLoaded(List<Transaction> transactions, List<String> keys) {
                        }

                        @Override
                        public void DataIsInserted() {
                        }

                        @Override
                        public void DataIsUpdated() {
                            message.setValue("Сделка обновлена");
                            isSuccessUpdate.setValue(true);
                        }

                        @Override
                        public void DataIsDeleted() {
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Currencies> call, Throwable t) {
                message.setValue("Не удалось обновить сделку");
            }
        });
    }

    private void addToFirebase(String year, Transaction trans){
        SharedPreferences settings = getApplication().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
        String account = settings.getString(Settings.ACCOUNT.name(), "");
        FirebaseUser user = new UserLivaData().getFirebaseUser();
        Transaction transaction = trans;
        String date = transaction.getDate();
        String currency = transaction.getCurrency();
        Double sumDouble = transaction.getSum();
        Controller ctrl = new Controller(date);
        Call<Currencies> currenciesCall = ctrl.prepareCurrenciesCall();
        currenciesCall.enqueue(new Callback<Currencies>() {
            @Override
            public void onResponse(Call<Currencies> call, Response<Currencies> response) {
                Double rateCentralBankDouble;
                if (response.isSuccessful()) {
                    Currencies cur = response.body();
                    rateCentralBankDouble = cur.getCurrencyRate(currency);
                    transaction.setRateCentralBank(rateCentralBankDouble);
                    Double sumRubDouble = sumDouble * rateCentralBankDouble * 0.13;
                    transaction.setSumRub(sumRubDouble);

                    new FirebaseTransactions(user, account).addTransaction(year, transaction, new FirebaseTransactions.DataStatus() {
                        @Override
                        public void DataIsLoaded(List<Transaction> transactions, List<String> keys) {
                        }

                        @Override
                        public void DataIsInserted() {
                            message.setValue("Сделка обновлена");
                            isSuccessUpdate.setValue(true);
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
                }else {
                    message.setValue("Не удалось загрузить курс валюты. Сделка не добавлена!");
                }
            }

            @Override
            public void onFailure(Call<Currencies> call, Throwable t) {
                Log.d("TAG", "onFailure: ");
            }
        });
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<Boolean> getIsSuccessUpdate() {
        return isSuccessUpdate;
    }

    public MutableLiveData<Boolean> getIsSuccessDelete() {
        return isSuccessDelete;
    }
}
