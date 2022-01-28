package com.taxapprf.taxapp.ui.newaccount;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.databinding.FragmentNewAccountBinding;
import com.taxapprf.taxapp.activities.MainActivity;
import com.taxapprf.taxapp.usersdata.Settings;

public class NewAccountFragment extends Fragment {
    private FragmentNewAccountBinding binding;
    private SharedPreferences.Editor settingsEditor;

    public NewAccountFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewAccountBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        EditText account = binding.editNewAccountName;
        settingsEditor = getActivity().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE).edit();

        Button buttonCreate = binding.buttonNewAccountCreate;
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = account.getText().toString();
                if(TextUtils.isEmpty(name)) {
                    Snackbar.make(v, "Введите имя счета (без пробелов)", Snackbar.LENGTH_SHORT).show();
                } else {
                    settingsEditor.putString(Settings.ACCOUNT.name(), account.getText().toString());
                    settingsEditor.apply();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                }
            }
        });

        Button buttonCancel = binding.buttonNewAccountCancel;
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_newAccountFragment_to_selectAccountFragment);
            }
        });

        return rootView;
    }
}