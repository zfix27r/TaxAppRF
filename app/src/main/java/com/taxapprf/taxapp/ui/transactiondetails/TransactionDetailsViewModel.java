package com.taxapprf.taxapp.ui.transactiondetails;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

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
import com.taxapprf.domain.Transaction;
import com.taxapprf.taxapp.usersdata.YearStatement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionDetailsViewModel extends AndroidViewModel {
    private final MutableLiveData<String> message;
    private final MutableLiveData<Boolean> isSuccessUpdate;
    private final MutableLiveData<Boolean> isSuccessDelete;
    //private MutableLiveData<String> keyLiveData;
    private Double currentYearSum;
    private boolean deleteFlag = false;
    private String key;

    public TransactionDetailsViewModel(@NonNull Application application) {
        super(application);
        //keyLiveData = new MutableLiveData<>();
        message = new MutableLiveData<>();
        isSuccessUpdate = new MutableLiveData<>(false);
        isSuccessDelete = new MutableLiveData<>(false);
    }

    public void deleteTransaction (String oldYear, String keyD, Double oldSumRub) {
        this.key = keyD;
        deleteFlag = true;
        deleteFromFirebase(oldYear, key, oldSumRub);

    }

    public void updateTransaction (Transaction transaction, String oldYear, String currentYear, String keyD, Double oldSumRub){
        this.key = keyD;
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
                Double oldYearSum = sumTaxes;
                BigDecimal currentYearSumBigDecimal = new BigDecimal(oldYearSum - oldSumRub);
                currentYearSumBigDecimal = currentYearSumBigDecimal.setScale(2, RoundingMode.HALF_UP);
                currentYearSum = currentYearSumBigDecimal.doubleValue();
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
            public void DataIsLoaded(List<Transaction> transactions) {
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

    private void updateToFirebase (Transaction transaction, String year, String key, Double oldSumRub) {
        isSuccessDelete.setValue(false);
        isSuccessUpdate.setValue(false);
        SharedPreferences settings = getApplication().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
        String account = settings.getString(Settings.ACCOUNT.name(), "");
        FirebaseUser user = new UserLivaData().getFirebaseUser();

        String date = transaction.getDate();
        String currency = transaction.getCurrency();


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
                    Double sumDouble = transaction.getSum();
                    int k;
                    switch (transaction.getType()){
                        case "TRADE":
                            k = 1;
                            break;
                        case "FUNDING/WITHDRAWAL":
                            k = 0;
                            break;
                        case "COMMISSION":
                            sumDouble = Math.abs(sumDouble);
                            k = -1;
                            break;
                        default: k = 1;
                    }
                    BigDecimal sumRubBigDecimal = new BigDecimal(sumDouble * rateCentralBankDouble * 0.13 * k);
                    sumRubBigDecimal = sumRubBigDecimal.setScale(2, RoundingMode.HALF_UP);
                    Double sumRubDouble = sumRubBigDecimal.doubleValue();
                    transaction.setSumRub(sumRubDouble);
                    transaction.setKey(key);

                    new FirebaseTransactions(user, account).updateTransaction(year, key, transaction, new FirebaseTransactions.DataStatus() {
                        @Override
                        public void DataIsLoaded(List<Transaction> transactions) {
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
                    Double sumDouble = transaction.getSum();
                    int k;
                    switch (transaction.getType()){
                        case "TRADE":
                            k = 1;
                            break;
                        case "FUNDING/WITHDRAWAL":
                            k = 0;
                            break;
                        case "COMMISSION":
                            sumDouble = Math.abs(sumDouble);
                            k = -1;
                            break;
                        default: k = 1;
                    }

                    BigDecimal sumRubBigDecimal = new BigDecimal(sumDouble * rateCentralBankDouble * 0.13 * k);
                    sumRubBigDecimal = sumRubBigDecimal.setScale(2, RoundingMode.HALF_UP);
                    Double sumRubDouble = sumRubBigDecimal.doubleValue();
                    transaction.setSumRub(sumRubDouble);

                    new FirebaseTransactions(user, account)
                            .addTransaction(year, transaction, new FirebaseTransactions.DataStatus() {
                        @Override
                        public void DataIsLoaded(List<Transaction> transactions) {
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
                            BigDecimal oldSumYear = new BigDecimal(sumTaxes);
                            Double currentSumYear = oldSumYear.add(new BigDecimal(transaction.getSumRub())).doubleValue();
                            new FirebaseYearSum(new UserLivaData().getFirebaseUser(), account).updateYearSum(year, currentSumYear);
                        }
                    });
                }else {
                    message.setValue("Не удалось загрузить курс валюты. Сделка не добавлена!");
                }
            }

            @Override
            public void onFailure(Call<Currencies> call, Throwable t) {
                //
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
