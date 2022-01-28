package com.taxapprf.taxapp.ui.changeaccount;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.activities.MainActivity;
import com.taxapprf.taxapp.databinding.FragmentChangeAccountBinding;
import com.taxapprf.taxapp.usersdata.Settings;

public class ChangeAccountFragment extends Fragment {
    private FragmentChangeAccountBinding binding;
    private ChangeAccountViewModel viewModel;


    public ChangeAccountFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ChangeAccountViewModel.class);
        binding = FragmentChangeAccountBinding.inflate(inflater, container, false);
        View viewRoot = binding.getRoot();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item);
        viewModel.getAllAccounts().observe(getViewLifecycleOwner(), new Observer<String[]>() {
            @Override
            public void onChanged(String[] accounts) {
                adapter.clear();
                adapter.addAll(accounts);
            }
        });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = binding.spinnerChangeAccount;
        spinner.setAdapter(adapter);

        Button buttonOpen = binding.buttonChangeAccountOpen;
        SharedPreferences.Editor settingsEditor = getContext().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE).edit();
        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner.getSelectedItem() == null) {
                    Snackbar.make(v, "Идет загрузка доступных счетов. Пожалуйста, подождите.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                String item = spinner.getSelectedItem().toString();
                settingsEditor.putString(Settings.ACCOUNT.name(), item);
                settingsEditor.apply();
                Navigation.findNavController(v).navigate(R.id.action_changeAccountFragment_to_taxesFragment);
            }
        });


        EditText account = binding.editChangeAccountName;

        Button buttonCreate = binding.buttonChangeAccountCreate;
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

        return viewRoot;
    }
}