package com.taxapprf.taxapp.activities;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.taxapprf.taxapp.firebase.FirebaseUserEmail;
import com.taxapprf.taxapp.firebase.FirebaseUserName;
import com.taxapprf.taxapp.firebase.UserLivaData;
import com.taxapprf.taxapp.usersdata.Settings;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<String> userName;
    private MutableLiveData<String> userEmail;

    public MainViewModel(@NonNull Application application) {
        super(application);
        userName = new MutableLiveData<>();
        userEmail = new MutableLiveData<>();
        SharedPreferences settings = getApplication().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        new FirebaseUserName(new UserLivaData().getFirebaseUser()).readName(new FirebaseUserName.DataStatus() {
            @Override
            public void DataIsLoaded(String name) {
                userName.setValue(name);
            }
        });

        new FirebaseUserEmail(new UserLivaData().getFirebaseUser()).readEmail(new FirebaseUserName.DataStatus() {
            @Override
            public void DataIsLoaded(String email) {
                userEmail.setValue(email);
                editor.putString(Settings.EMAIL.name(), email);
                editor.apply();
            }
        });

    }

    public MutableLiveData<String> getUserName() {
        return userName;
    }

    public MutableLiveData<String> getUserEmail() {
        return userEmail;
    }
}
