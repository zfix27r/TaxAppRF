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
    private RatesTodayViewModel viewModel;
    private Currencies rates;
    private Double rate;
    private String currency;
    private String value;
    private String rub;



    public CurrencyConverterFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(RatesTodayViewModel.class);
        binding = FragmentCurrencyConverterBinding.inflate(inflater, container, false);
        View viewRoot = binding.getRoot();

        Spinner currencies = binding.spinnerConverter;
        final String[] arrayCurrencies = getResources().getStringArray(R.array.currencies);
        ArrayAdapter<String> currenciesArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, arrayCurrencies);
        currenciesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencies.setAdapter(currenciesArrayAdapter);
        currencies.setSelection(currenciesArrayAdapter.getPosition("USD"));

        EditText amount = binding.editConverterUp;
        EditText amountRub = binding.editConverterDown;



        currency = currencies.getSelectedItem().toString();
        viewModel.getCurrencies().observe(getViewLifecycleOwner(), new Observer<Currencies>() {
            @Override
            public void onChanged(Currencies cur) {
                rates = cur;
                value = "1";
                rate = rates.getCurrencyRate(currency);
                Double sum = Double.parseDouble(amount.getText().toString()) * rate;
                rub = sum.toString().replaceAll(",", "\\.");
                amountRub.setText(rub);

                amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            String amountStr = amount.getText().toString();
                            DoubleCheck doubleCheck = new DoubleCheck(amountStr);
                            if (!doubleCheck.isCheck()){
                                amount.setText(value);
                                hideKeyboard();
                                return;
                            }
                            value = amountStr.replaceAll(",", "\\.");
                            amount.setText(value);
                            //currency = currencies.getSelectedItem().toString();
                            rate = rates.getCurrencyRate(currency);
                            Double sum = new BigDecimal(doubleCheck.getNumDouble()).multiply(BigDecimal.valueOf(rate)).doubleValue();
                            rub = sum.toString().replaceAll(",", "\\.");
                            amountRub.setText(rub);
                            hideKeyboard();
                        }
                    }
                });


                amountRub.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            String amountRubStr = amountRub.getText().toString();
                            DoubleCheck doubleCheck = new DoubleCheck(amountRubStr);
                            if (!doubleCheck.isCheck()){
                                amountRub.setText(rub);
                                hideKeyboard();
                                return;
                            }
                            rub = amountRubStr.replaceAll(",", "\\.");
                            amountRub.setText(rub);
                            //currency = currencies.getSelectedItem().toString();
                            rate = rates.getCurrencyRate(currency);
                            BigDecimal sumBigDecimal = new BigDecimal(doubleCheck.getNumDouble());
                            sumBigDecimal = sumBigDecimal.divide(BigDecimal.valueOf(rate), 4, RoundingMode.HALF_UP);
                            //sumBigDecimal.setScale(4, RoundingMode.HALF_UP);
                            Double sum = sumBigDecimal.doubleValue();
                            value = sum.toString().replaceAll(",", "\\.");
                            amount.setText(value);
                            hideKeyboard();
                        }
                    }
                });


                currencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currency = currencies.getSelectedItem().toString();
                        rate = rates.getCurrencyRate(currency);
                        amount.setText("1");
                        Double sum = 1 * rate;
                        value = "1";
                        rub = String.format("%.2f", sum).replaceAll(",", "\\.");
                        amountRub.setText(rub);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }
        });


        return viewRoot;
    }
    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
    }
}

