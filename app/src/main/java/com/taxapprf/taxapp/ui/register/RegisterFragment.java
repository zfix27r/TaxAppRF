package com.taxapprf.taxapp.ui.register;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.databinding.FragmentRegisterBinding;


public class RegisterFragment extends Fragment {
    private RegisterViewModel viewModel;
    private FragmentRegisterBinding binding;


    public RegisterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View viewRoot = binding.getRoot();

        viewModel.getMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                if (message != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });


        viewModel.getLoggedIn().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loggedIn) {
                if (loggedIn){
                    Navigation.findNavController(viewRoot).navigate(R.id.action_registerFragment_to_firstAccountFragment);
                }
            }
        });

        EditText name = binding.editRegisterName;
        EditText email = binding.editRegisterEmail;
        EditText phone = binding.editRegisterPhone;
        EditText password = binding.editRegisterPassword;

        Button buttonCancel = binding.buttonRegisterCancel;
        buttonCancel.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment));

        Button buttonRegister = binding.buttonRegisterCreate;
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sEmail = email.getText().toString();
                String sName = name.getText().toString();
                String sPhone = phone.getText().toString();
                String sPassword = password.getText().toString();
                if (TextUtils.isEmpty(sEmail)) {
                    Toast.makeText(getContext(), "Введите ваш email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(sName)) {
                    Toast.makeText(getContext(), "Введите ваше имя", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(sPhone)) {
                    Toast.makeText(getContext(), "Введите ваш телефон", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (sPassword.length() < 8 ) {
                    Toast.makeText(getContext(), "Введите пароль, который больше 8 символов", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!new EmailCheck(sEmail).check()) {
                    Toast.makeText(getContext(), "Некорректный email", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewModel.register(sName, sEmail, sPassword, sPhone);
            }
        });

        return viewRoot;
    }
}