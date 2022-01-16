package com.taxapprf.taxapp.ui.register;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taxapprf.taxapp.firebase.UserLivaData;
import com.taxapprf.taxapp.usersdata.User;

public class RegisterViewModel extends AndroidViewModel {
    private MutableLiveData<String> message;
    private MutableLiveData<Boolean> loggedIn;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference users;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        message = new MutableLiveData<>();
        message.setValue(null);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        loggedIn = new MutableLiveData<>(false);
        if (new UserLivaData().getFirebaseUser() != null) {
            loggedIn.setValue(true);
        }
    }

    public void register (String name, String email, String password,  String phone) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            writeUser(name, email, password, phone);
                        } else {
                            message.postValue("Ошибка авторизациию. " + task.getException());
                            loggedIn.postValue(false);
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

    private void writeUser (String name, String email, String password,  String phone) {
        User user = new User(name, email, password, phone);
        users.child(new UserLivaData().getFirebaseUser().getUid())
                .setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loggedIn.postValue(true);
                        message.postValue("Пользователь добавлен!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loggedIn.postValue(false);
                    }
                });
    }
}
