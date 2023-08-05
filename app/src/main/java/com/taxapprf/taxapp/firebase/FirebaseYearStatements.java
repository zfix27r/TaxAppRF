/*
package com.taxapprf.taxapp.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taxapprf.taxapp.usersdata.YearStatement;

import java.util.ArrayList;
import java.util.List;

public class FirebaseYearStatements {
    private FirebaseDatabase database;
    private DatabaseReference referenceUser;
    private DatabaseReference referenceYearStatements;
    private List<YearStatement> yearStatements = new ArrayList<>();
    private ValueEventListener valueEventListener;


    public interface DataStatus{
        void DataIsLoaded(List<YearStatement> yearStatements);
        void DataIsDeleted();
    }

    public FirebaseYearStatements (FirebaseUser user, String account){
        database = FirebaseDatabase.getInstance();
        referenceUser = database.getReference("Users").child(user.getUid());
        referenceYearStatements = referenceUser.child("accounts").child(account);
    }

    public void readYearStatements(final DataStatus dataStatus){
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                yearStatements.clear();
                for (DataSnapshot keyNode: snapshot.getChildren()) {
                    YearStatement yearStatement = keyNode.getValue(YearStatement.class);
                    yearStatements.add(yearStatement);
                }
                dataStatus.DataIsLoaded(yearStatements);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        referenceYearStatements.addValueEventListener(valueEventListener);
    }

    public void removeListener(){
        referenceYearStatements.removeEventListener(valueEventListener);
    }

    public void deleteYearStatement (String year, final DataStatus dataStatus){
        referenceYearStatements.child(year).setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dataStatus.DataIsDeleted();
                    }
                });
    }
}
*/
