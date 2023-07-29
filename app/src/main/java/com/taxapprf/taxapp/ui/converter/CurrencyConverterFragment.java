package com.taxapprf.taxapp.ui.converter;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.databinding.FragmentCurrencyConverterBinding;
import com.taxapprf.taxapp.retrofit2.Currencies;
import com.taxapprf.taxapp.ui.newtransaction.DoubleCheck;
import com.taxapprf.taxapp.ui.rates.today.RatesTodayViewModel;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyConverterFragment extends Fragment {
    private FragmentCurrencyConverterBinding binding;
    private ConverterViewModel viewModel;
    private RatesTodayViewModel viewModelRates;

    public CurrencyConverterFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //viewModel = new ViewModelProvider(this, new ConverterViewModelFactory(getViewLifecycleOwner())).get(ConverterViewModel.class);

        binding = FragmentCurrencyConverterBinding.inflate(inflater, container, false);
        View viewRoot = binding.getRoot();

        Spinner currencies = binding.spinnerConverter;
        final String[] arrayCurrencies = getResources().getStringArray(R.array.transaction_currencies);
        ArrayAdapter<String> currenciesArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, arrayCurrencies);
        currenciesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencies.setAdapter(currenciesArrayAdapter);
        currencies.setSelection(currenciesArrayAdapter.getPosition("USD"));

        EditText amount = binding.editConverterUp;
        EditText amountRub = binding.editConverterDown;

        viewModel = new ViewModelProvider(this).get(ConverterViewModel.class);
        viewModelRates = new ViewModelProvider(requireActivity()).get(RatesTodayViewModel.class);
        viewModelRates.getCurrencies().observe(getViewLifecycleOwner(), new Observer<Currencies>() {
            @Override
            public void onChanged(Currencies rates) {
                viewModel.setRates(rates);
                String currency = currencies.getSelectedItem().toString();
                viewModel.calculate(currency);
            }
        });

        viewModel.getRub().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                amountRub.setText(s);
            }
        });

        viewModel.getValue().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                amount.setText(s);
            }
        });

        amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String amountStr = amount.getText().toString();
                    viewModel.calculateRub(amountStr);
                    hideKeyboard();
                }
            }
        });

        amountRub.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String amountRubStr = amountRub.getText().toString();
                    viewModel.calculateValue(amountRubStr);
                    hideKeyboard();
                }
            }
        });

        currencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currency = currencies.getSelectedItem().toString();
                viewModel.calculate(currency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return viewRoot;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
    }
}

