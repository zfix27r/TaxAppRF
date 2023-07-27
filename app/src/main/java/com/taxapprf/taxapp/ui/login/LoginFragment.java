package com.taxapprf.taxapp.ui.login;

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
import android.widget.Button;

import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.databinding.FragmentLoginBinding;
import com.taxapprf.taxapp.ui.MainActivity;
import com.taxapprf.taxapp.usersdata.Settings;


public class LoginFragment extends Fragment {
    FragmentLoginBinding binding;
    LoginViewModel viewModel;

    public LoginFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View viewRoot = binding.getRoot();

        viewModel.getLoggedIn().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loggedIn) {
                if (loggedIn) {
                    SharedPreferences settings = getContext().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
                    if (settings.getString(Settings.ACCOUNT.name(), "") != null) {
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    } else {
                        Navigation.findNavController(viewRoot).navigate(R.id.action_loginFragment_to_selectAccountFragment);
                    }
                }
            }
        });

        Button buttonSignIn = binding.buttonLoginSignIn;
        buttonSignIn.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signInFragment));

        Button buttonRegister = binding.buttonLoginRegister;
        buttonRegister.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment));

        return viewRoot;
    }
}