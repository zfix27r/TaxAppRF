package com.taxapprf.taxapp.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class FirebaseAccounts {
    private FirebaseDatabase database;
    private DatabaseReference referenceUser;
    private String[] accounts;

    public interface DataStatus{
        void DataIsLoaded(String[] accounts);
        void DataIsDeleted();
    }

    public FirebaseAccounts(FirebaseUser firebaseUser) {
        this.database = FirebaseDatabase.getInstance();
        this.referenceUser = database.getReference().child("Users").child(firebaseUser.getUid());
    }

    public void readAccounts (final DataStatus dataStatus) {
        DatabaseReference referenceAccounts = referenceUser.child("accounts");
        referenceAccounts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    accounts = new String[]{"DefaultAccount"};
                } else {
                    List<String> keys = new ArrayList<>();
                    for (DataSnapshot keyNode: snapshot.getChildren()) {
                        keys.add(keyNode.getKey());
                    }
                    accounts = keys.toArray(new String[0]);
                }
                dataStatus.DataIsLoaded(accounts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deleteAccount (String account, final DataStatus dataStatus) {
        DatabaseReference referenceAccounts = referenceUser.child("accounts");
        referenceAccounts.child(account).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dataStatus.DataIsDeleted();
            }
        });
    }
}
