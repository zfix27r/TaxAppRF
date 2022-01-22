package com.taxapprf.taxapp.ui.changeaccount;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.taxapprf.taxapp.firebase.FirebaseAccounts;
import com.taxapprf.taxapp.firebase.UserLivaData;


public class ChangeAccountViewModel extends AndroidViewModel {
    private MutableLiveData<String[]> allAccounts;

    public ChangeAccountViewModel(@NonNull Application application) {
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

    public MutableLiveData<String[]> getAllAccounts() {
        return allAccounts;
    }
}
