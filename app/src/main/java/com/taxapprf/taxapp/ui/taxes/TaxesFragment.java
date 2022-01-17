package com.taxapprf.taxapp.ui.taxes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.databinding.FragmentTaxesBinding;
import com.taxapprf.taxapp.usersdata.YearStatement;

import java.util.ArrayList;
import java.util.List;

public class TaxesFragment extends Fragment {
    private FragmentTaxesBinding binding;
    private TaxesViewModel viewModel;

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
                Snackbar.make(v, "click loading", Snackbar.LENGTH_SHORT).show();
                //написать обработчик
                //прописать логику обработки отчета xls or ...
            }
        });


        return viewRoot;
    }
}