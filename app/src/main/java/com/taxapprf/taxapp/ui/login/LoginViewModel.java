package com.taxapprf.taxapp.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.taxapprf.taxapp.firebase.UserLivaData;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<Boolean> loggedIn;

    public LoginViewModel() {
        loggedIn = new MutableLiveData<>(false);
        if (new UserLivaData().getFirebaseUser() != null) {
            loggedIn.setValue(true);
        }
    }

    public MutableLiveData<Boolean> getLoggedIn() {
        return loggedIn;
    }
}
