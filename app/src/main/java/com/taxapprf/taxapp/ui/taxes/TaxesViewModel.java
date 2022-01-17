package com.taxapprf.taxapp.ui.taxes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.taxapprf.taxapp.firebase.FirebaseYearStatements;
import com.taxapprf.taxapp.firebase.UserLivaData;
import com.taxapprf.taxapp.usersdata.Settings;
import com.taxapprf.taxapp.usersdata.YearStatement;

import java.util.List;

public class TaxesViewModel extends AndroidViewModel {
    private SharedPreferences settings;
    private MutableLiveData<List<YearStatement>> yearStatements;

    public TaxesViewModel(@NonNull Application application) {
        super(application);

        yearStatements = new MutableLiveData<List<YearStatement>>();
        settings = getApplication().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
        String account = settings.getString(Settings.ACCOUNT.name(),  "");

        new FirebaseYearStatements(new UserLivaData().getFirebaseUser(), account).readYearStatements(new FirebaseYearStatements.DataStatus() {
            @Override
            public void DataIsLoaded(List<YearStatement> statements) {
                yearStatements.setValue(statements);
            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }

    public MutableLiveData<List<YearStatement>> getYearStatements() {
        return yearStatements;
    }
}
