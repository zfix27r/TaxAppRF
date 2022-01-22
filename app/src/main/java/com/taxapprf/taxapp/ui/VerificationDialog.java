package com.taxapprf.taxapp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;

public class VerificationDialog extends DialogFragment {
    private MutableLiveData<Boolean> verificationStatus;

    public VerificationDialog() {
        this.verificationStatus = new MutableLiveData<>();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String title = "Подтвердить операцию";
        String message = "Вы уверены, что хотите безвозвратно удалить?";
        String buttonOk = "Да";
        String buttonCancel = "Отмена";

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(buttonOk, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                verificationStatus.setValue(true);
            }
        });
        builder.setNegativeButton(buttonCancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                verificationStatus.setValue(false);
                dialog.cancel();
            }
        });
        builder.setCancelable(true);

        return builder.create();
    }

    public MutableLiveData<Boolean> getVerificationStatus() {
        return verificationStatus;
    }
}


