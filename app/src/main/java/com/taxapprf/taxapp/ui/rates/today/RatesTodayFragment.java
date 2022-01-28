package com.taxapprf.taxapp.ui.rates.today;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taxapprf.taxapp.databinding.FragmentRatesTodayBinding;
import com.taxapprf.taxapp.retrofit2.Currencies;
import com.taxapprf.taxapp.retrofit2.CurrencyRate;

import java.util.ArrayList;

public class RatesTodayFragment extends Fragment {
    private FragmentRatesTodayBinding binding;
    private RatesTodayViewModel viewModel;


    public RatesTodayFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRatesTodayBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(RatesTodayViewModel.class);
        View rootView = binding.getRoot();

        Currencies currencies = new Currencies();
        currencies.setCurrencyList(new ArrayList<CurrencyRate>());
        RecyclerViewCurrencyConfig recyclerViewConfig= new RecyclerViewCurrencyConfig();
        RecyclerView recyclerView = binding.recyclerviewCurrencies;
        recyclerViewConfig.setConfig(recyclerView, getContext(), currencies);

        viewModel.getCurrencies().observe(getActivity(), new Observer<Currencies>() {
            @Override
            public void onChanged(Currencies currencies) {
                recyclerViewConfig.setConfig(recyclerView, getContext(), currencies);
            }
        });


        return rootView;
    }
}