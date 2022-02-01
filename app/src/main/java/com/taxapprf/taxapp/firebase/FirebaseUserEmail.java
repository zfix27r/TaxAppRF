package com.taxapprf.taxapp.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUserEmail {
    private FirebaseDatabase database;
    private DatabaseReference referenceUser;
    private DatabaseReference referenceUserName;
    private String userEmail;


    public interface DataStatus{
        void DataIsLoaded(String userName);
    }

    public FirebaseUserEmail(FirebaseUser user) {
        database = FirebaseDatabase.getInstance();
        referenceUser = database.getReference("Users").child(user.getUid());
        referenceUserName = referenceUser.child("email");
    }

    public void readEmail (final FirebaseUserName.DataStatus dataStatus) {
        referenceUserName.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    //...
                }
                else {
                    userEmail = String.valueOf(task.getResult().getValue());
                    dataStatus.DataIsLoaded(userEmail);
                }
            }
        });
    }
}
