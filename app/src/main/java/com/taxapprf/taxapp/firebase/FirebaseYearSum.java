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
    private Double sumTaxes = new Double(0);


    public interface DataStatus{
        void DataIsLoaded(Double sumTaxes);
    }

    public FirebaseYearSum (FirebaseUser user, String account){
        database = FirebaseDatabase.getInstance();
        referenceUser = database.getReference("Users").child(user.getUid());
        referenceYearStatements = referenceUser.child("accounts").child(account);
    }

    public void readYearSum (String year, final DataStatus dataStatus) {
        DatabaseReference referenceYearSum = referenceYearStatements.child(year).child("sumTaxes");
        referenceYearSum.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    sumTaxes = 0.0;
                } else {
                    //String s = snapshot.getValue().toString();
                    sumTaxes = (Double) snapshot.getValue();
                }
                dataStatus.DataIsLoaded(sumTaxes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void readYearSumOnce (String year, final DataStatus dataStatus) {
        DatabaseReference referenceYearSum = referenceYearStatements.child(year).child("sumTaxes");
        referenceYearSum.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                }
                else {
                    if (task.getResult().getValue() == null) {
                        sumTaxes = 0.0;
                    } else {
                        //String s = String.valueOf(task.getResult().getValue());
                        sumTaxes = (Double) task.getResult().getValue();
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
