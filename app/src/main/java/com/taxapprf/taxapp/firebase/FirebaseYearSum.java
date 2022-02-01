package com.taxapprf.taxapp.firebase;

import android.util.Log;

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

public class FirebaseYearSum {
    private FirebaseDatabase database;
    private DatabaseReference referenceUser;
    private DatabaseReference referenceYearStatements;
    private DatabaseReference referenceYearSum;
    private Double sumTaxes = new Double(0.0);
    private ValueEventListener valueEventListener;


    public interface DataStatus{
        void DataIsLoaded(Double sumTaxes);
    }

    public FirebaseYearSum (FirebaseUser user, String account){
        database = FirebaseDatabase.getInstance();
        referenceUser = database.getReference("Users").child(user.getUid());
        referenceYearStatements = referenceUser.child("accounts").child(account);
    }

    public void readYearSum (String year, final DataStatus dataStatus) {
        referenceYearSum = referenceYearStatements.child(year).child("sumTaxes");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    sumTaxes = 0.0;
                } else {
                    if (snapshot.getValue() instanceof Long) {
                        Long l = new Long(String.valueOf(snapshot.getValue()));
                        sumTaxes = l.doubleValue();
                    }
                    else {
                        sumTaxes = (Double) snapshot.getValue();
                    }
                }
                dataStatus.DataIsLoaded(sumTaxes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        referenceYearSum.addValueEventListener(valueEventListener);
    }

    public void removeListener(){
        referenceYearSum.removeEventListener(valueEventListener);
    }

    public void readYearSumOnce (String year, final DataStatus dataStatus) {
        DatabaseReference referenceYearSum = referenceYearStatements.child(year).child("sumTaxes");
        referenceYearSum.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    //обработать
                }
                else {
                    if (task.getResult().getValue() == null) {
                        sumTaxes = 0.0;
                    } else {
                        if (task.getResult().getValue() instanceof Long) {
                            sumTaxes = (Long) task.getResult().getValue() + 0.0;
                        } else {
                            sumTaxes = (Double) task.getResult().getValue();
                        }
                    }
                    dataStatus.DataIsLoaded(sumTaxes);
                }
            }

        });
    }

    public void updateYearSum (String year, Double sum){
        DatabaseReference referenceYearSum = referenceYearStatements.child(year).child("sumTaxes");
        referenceYearSum.setValue(sum)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
    }
}
