package com.taxapprf.taxapp.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taxapprf.taxapp.usersdata.Transaction;

import java.util.ArrayList;
import java.util.List;

public class FirebaseTransactions {
    private FirebaseDatabase database;
    private DatabaseReference referenceUser;
    private DatabaseReference referenceYearStatements;
    private DatabaseReference referenceTransactions;
    private List<Transaction> transactions = new ArrayList<>();

    public interface DataStatus{
        void DataIsLoaded(List<Transaction> transactions, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public FirebaseTransactions (FirebaseUser user, String account){
        database = FirebaseDatabase.getInstance();
        referenceUser = database.getReference("Users").child(user.getUid());
        referenceYearStatements = referenceUser.child("accounts").child(account);
    }


    public void readTransactions(String year, final DataStatus dataStatus) {
        referenceTransactions = referenceYearStatements.child(year).child("transactions");
        referenceTransactions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> keys = new ArrayList<>();
                transactions.clear();
                for (DataSnapshot keyNode: snapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Transaction transaction = keyNode.getValue(Transaction.class);
                    transactions.add(transaction);
                }
                dataStatus.DataIsLoaded(transactions, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void addTransaction(String year, Transaction transaction, final DataStatus dataStatus){
        referenceTransactions = referenceYearStatements.child(year).child("transactions");
        String key = referenceTransactions.push().getKey();
        referenceTransactions.child(key).setValue(transaction)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dataStatus.DataIsInserted();
                    }
                });
        referenceYearStatements.child(year).child("year").setValue(year);
    }

    public void updateTransaction (String year, String key, Transaction transaction, final DataStatus dataStatus){
        referenceTransactions = referenceYearStatements.child(year).child("transactions");
        referenceTransactions.child(key).setValue(transaction)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dataStatus.DataIsUpdated();
                    }
                });
    }

    public void deleteTransaction (String year, String key, final DataStatus dataStatus){
        referenceTransactions = referenceYearStatements.child(year).child("transactions");
        referenceTransactions.child(key).setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dataStatus.DataIsDeleted();
                    }
                });
    }
}
