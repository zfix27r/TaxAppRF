package com.taxapprf.taxapp.ui.select.account;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.taxapprf.taxapp.firebase.FirebaseAccounts;
import com.taxapprf.taxapp.firebase.UserLivaData;

public class SelectAccountViewModel extends AndroidViewModel {
    MutableLiveData<String[]> allAccounts;

    public SelectAccountViewModel(@NonNull Application application) {
        super(application);
        allAccounts = new MutableLiveData<>();

        new FirebaseAccounts(new UserLivaData().getFirebaseUser()).readAccounts(new FirebaseAccounts.DataStatus() {
            @Override
            public void DataIsLoaded(String[] accounts) {
                allAccounts.setValue(accounts);
            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }

    public void exitDataBase(){
        new UserLivaData().getFirebaseAuth().signOut();
    }

    public MutableLiveData<String[]> getAllAccounts() {
        return allAccounts;
    }
}
