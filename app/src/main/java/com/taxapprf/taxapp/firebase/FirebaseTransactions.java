package com.taxapprf.taxapp.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taxapprf.domain.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FirebaseTransactions {
    private final FirebaseUser user;
    private final String account;
    private final DatabaseReference referenceYearStatements;
    private DatabaseReference referenceTransactions;
    private List<Transaction> transactions = new ArrayList<>();
    private ValueEventListener valueEventListener;

    public interface DataStatus {
        void DataIsLoaded(List<Transaction> transactions);

        void DataIsInserted();

        void DataIsUpdated();

        void DataIsDeleted();
    }

    public FirebaseTransactions(FirebaseUser user, String account) {
        this.user = user;
        this.account = account;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference referenceUser = database.getReference("Users").child(user.getUid());
        referenceYearStatements = referenceUser.child("accounts").child(account);
    }


    public void readTransactions(String year, final DataStatus dataStatus) {
        referenceTransactions = referenceYearStatements.child(year).child("transactions");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BigDecimal currentYearSumBigDecimal = new BigDecimal(0);
                transactions.clear();
                for (DataSnapshot keyNode : snapshot.getChildren()) {
                    Transaction transaction = keyNode.getValue(Transaction.class);
                    transactions.add(transaction);
                    currentYearSumBigDecimal = currentYearSumBigDecimal.add(BigDecimal.valueOf(transaction.getSumRub()));
                }
                dataStatus.DataIsLoaded(transactions);
                if (!transactions.isEmpty()) {
                    new FirebaseYearSum(user, account).updateYearSum(year, currentYearSumBigDecimal.doubleValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        referenceTransactions.addValueEventListener(valueEventListener);
    }

    public void removeListener(){
        referenceTransactions.removeEventListener(valueEventListener);
    }

    public void sumTransaction(String year){
        referenceTransactions = referenceYearStatements.child(year).child("transactions");
        referenceTransactions.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                BigDecimal currentYearSumBigDecimal = new BigDecimal(0);
                for (DataSnapshot keyNode: task.getResult().getChildren()){
                    Transaction transaction = keyNode.getValue(Transaction.class);
                    currentYearSumBigDecimal = currentYearSumBigDecimal.add(BigDecimal.valueOf(transaction.getSumRub()));
                }
                new FirebaseYearSum(user, account).updateYearSum(year, currentYearSumBigDecimal.doubleValue());
            }
        });
    }

    public void addTransaction(String year, Transaction transaction, final DataStatus dataStatus){
        referenceTransactions = referenceYearStatements.child(year).child("transactions");
        String key = referenceTransactions.push().getKey();
        transaction.setKey(key);
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

