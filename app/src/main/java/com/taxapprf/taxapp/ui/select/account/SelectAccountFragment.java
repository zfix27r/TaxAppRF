package com.taxapprf.taxapp.ui.select.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.activities.MainActivity;
import com.taxapprf.taxapp.databinding.FragmentSelectAccountBinding;
import com.taxapprf.taxapp.usersdata.Settings;


public class SelectAccountFragment extends Fragment {
    private SelectAccountViewModel viewModel;
    private FragmentSelectAccountBinding binding;

    public SelectAccountFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(SelectAccountViewModel.class);
        binding = FragmentSelectAccountBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item);
        viewModel.getAllAccounts().observe(getViewLifecycleOwner(), new Observer<String[]>() {
            @Override
            public void onChanged(String[] accounts) {
                adapter.clear();
                adapter.addAll(accounts);
            }
        });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = binding.spinnerSelectAccount;
        spinner.setAdapter(adapter);

        Button buttonOpen = binding.buttonSelectOpen;
        SharedPreferences.Editor preferenceEditor = getContext()
                .getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE).edit();
        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner.getSelectedItem() == null) {
                    Snackbar.make(v, "Идет загрузка доступных счетов. Пожалуйста, подождите.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                String item = spinner.getSelectedItem().toString();
                preferenceEditor.putString(Settings.ACCOUNT.name(), item);
                preferenceEditor.apply();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });

        Button buttonCreate = binding.buttonSelectNewAccountCreate;
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_selectAccountFragment_to_newAccountFragment);
            }
        });

        Button buttonExit = binding.buttonSelectExit;
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.exitDataBase();
                Navigation.findNavController(v).navigate(R.id.action_selectAccountFragment_to_loginFragment);
            }
        });


        return rootView;
    }
}