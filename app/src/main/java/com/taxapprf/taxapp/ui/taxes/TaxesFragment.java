package com.taxapprf.taxapp.ui.taxes;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.databinding.FragmentTaxesBinding;
import com.taxapprf.taxapp.usersdata.YearStatement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TaxesFragment extends Fragment {
    private FragmentTaxesBinding binding;
    private TaxesViewModel viewModel;
    private static final int PICKFILE_RESULT_CODE = 1001;
    private String filePath;

    public TaxesFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(TaxesViewModel.class);
        binding = FragmentTaxesBinding.inflate(inflater, container, false);
        View viewRoot = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerYearStatements;
        List<YearStatement> yearStatements = new ArrayList<>();
        RecyclerYearStatementConfig recyclerViewConfig = new RecyclerYearStatementConfig();

        recyclerViewConfig.setConfig(recyclerView, getContext(), yearStatements);

        viewModel.getYearStatements().observe(getViewLifecycleOwner(), new Observer<List<YearStatement>>() {
            @Override
            public void onChanged(List<YearStatement> yearStatements) {
                recyclerViewConfig.setConfig(recyclerView, getContext(), yearStatements);
            }
        });

        Button buttonAddTrans = binding.buttonTaxesAddTrans;
        buttonAddTrans.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_taxesFragment_to_newTransactionFragment));

        Button buttonLoading = binding.buttonTaxesLoading;
        buttonLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/vnd.ms-excel");
                    startActivityForResult(intent, PICKFILE_RESULT_CODE);
                }
            }
        });


        return viewRoot;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode == RESULT_OK){
                    filePath = data.getData().getPath();
                    Log.d("OLGA", "onActivityResult: filePath " + filePath);

                }
                try {
                    viewModel.addTransactions(filePath);
                } catch (IOException e) {
                    Snackbar.make(getView(), "Не удалось конвертипровать файл", Snackbar.LENGTH_SHORT).show();
                }
                //break;
        }

    }

    public  boolean isStoragePermissionGranted(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}

