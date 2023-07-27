package com.taxapprf.taxapp.ui.first.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.taxapprf.taxapp.databinding.FragmentFirstAccountBinding;
import com.taxapprf.taxapp.ui.MainActivity;
import com.taxapprf.taxapp.usersdata.Settings;

public class FirstAccountFragment extends Fragment {
    private SharedPreferences.Editor preferenceEditor;
    private FragmentFirstAccountBinding binding;


    public FirstAccountFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFirstAccountBinding.inflate(inflater, container, false);
        View viewRoot = binding.getRoot();

        preferenceEditor = getContext().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE).edit();

        EditText account = binding.editFirstAccountName;

        Button buttonCancel = binding.buttonFirstAccountCancel;
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceEditor.putString(Settings.ACCOUNT.name(), "DefaultAccount");
                preferenceEditor.apply();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();

            }
        });

        Button buttonCreate = binding.buttonFirstAccountCreate;
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameNewAccount = account.getText().toString();
                if(TextUtils.isEmpty(nameNewAccount)) {
                    Snackbar.make(v, "Введите имя счета (без пробелов)", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                preferenceEditor.putString(Settings.ACCOUNT.name(), nameNewAccount);
                preferenceEditor.apply();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });

        return viewRoot;
    }
}