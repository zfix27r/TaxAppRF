package com.taxapprf.taxapp.ui.exit;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.firebase.UserLivaData;
import com.taxapprf.taxapp.activities.LoginActivity;

public class ExitFragment extends Fragment {


    public ExitFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new UserLivaData().getFirebaseAuth().signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
        return inflater.inflate(R.layout.fragment_exit, container, false);
    }
}