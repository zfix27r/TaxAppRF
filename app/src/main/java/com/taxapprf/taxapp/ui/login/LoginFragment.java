package com.taxapprf.taxapp.ui.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.firebase.UserLivaData;


public class LoginFragment extends Fragment {

    public LoginFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

//        if (new UserLivaData().getFirebaseUser() != null) {
//            Navigation.findNavController(rootView).navigate(R.id.action_loginFragment_to_selectAccountFragment);
//        }

        Button buttonSignIn = rootView.findViewById(R.id.buttonLoginSignIn);
        buttonSignIn.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signInFragment));

        Button buttonRegister = rootView.findViewById(R.id.buttonLoginRegister);
        buttonRegister.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment));

        return rootView;
    }
}