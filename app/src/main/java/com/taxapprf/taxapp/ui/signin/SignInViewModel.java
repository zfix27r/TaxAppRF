package com.taxapprf.taxapp.ui.signin;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.taxapprf.taxapp.firebase.UserLivaData;
import com.taxapprf.taxapp.usersdata.Settings;

public class SignInViewModel extends AndroidViewModel {
    private MutableLiveData<String> message;
    private MutableLiveData<Boolean> loggedIn;

    public SignInViewModel(@NonNull Application application) {
        super(application);
        message = new MutableLiveData<>();
        message.setValue(null);

        loggedIn = new MutableLiveData<>(false);
        if (new UserLivaData().getFirebaseUser() != null) {
            loggedIn.setValue(true);
        }
    }

    public void signIn (String email, String password){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences settings = getApplication().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
                            SharedPreferences.Editor preferenceEditor = settings.edit();
                            preferenceEditor.putString(Settings.EMAIL.name(), email);
                            preferenceEditor.apply();
                            message.postValue("Пользователь авторизован");
                        } else {
                            message.postValue("Ошибка авторизации. " + task.getException());
                        }
                        if (new UserLivaData().getFirebaseUser() != null) {
                            loggedIn.postValue(true);
                        }
                    }
                });
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<Boolean> getLoggedIn() {
        return loggedIn;
    }
}
