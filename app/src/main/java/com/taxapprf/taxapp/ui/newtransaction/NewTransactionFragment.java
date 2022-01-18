package com.taxapprf.taxapp.ui.newtransaction;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.databinding.FragmentNewTransactionBinding;
import com.taxapprf.taxapp.usersdata.Transaction;

public class NewTransactionFragment extends Fragment {
    private FragmentNewTransactionBinding binding;
    private NewTransactionViewModel viewModel;


    public NewTransactionFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(NewTransactionViewModel.class);
        binding = FragmentNewTransactionBinding.inflate(inflater, container, false);
        View viewRoot = binding.getRoot();

        Spinner typeTransaction = binding.spinnerNewTransType;
        EditText idTrans = binding.editNewTransId;
        EditText date = binding.editNewTransDate;
        EditText sum = binding.editNewTransSum;
        Spinner currencies = binding.spinnerNewTransCurrencies;

        final String[] arrayTypeTrans = getResources().getStringArray(R.array.type_transaction);
        final String[] arrayCurrencies = getResources().getStringArray(R.array.currencies);
        ArrayAdapter<String> currenciesArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, arrayCurrencies);
        currencies.setAdapter(currenciesArrayAdapter);
        currencies.setSelection(currenciesArrayAdapter.getPosition("USD"));

        final ArrayAdapter<String> typeTransArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, arrayTypeTrans){
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        typeTransArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item); //Удалить??
        typeTransaction.setAdapter(typeTransArrayAdapter);

        Button buttonCancel = binding.buttonNewTransCancel;
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_newTransactionFragment_to_taxesFragment);
            }
        });

        Button buttonAdd = binding.buttonNewTransAdd;
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(typeTransaction.getSelectedItemPosition() == 0) {
                    Toast.makeText(getContext(), "Выберите тип сделки.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(currencies.getSelectedItemPosition() == 0) {
                    Toast.makeText(getContext(), "Выберите валюту сделки.", Toast.LENGTH_SHORT).show();
                    return;
                }

                DateCheck dateCheck = new DateCheck(date.getText().toString());
                String year;
                if (!dateCheck.check()) {
                    Toast.makeText(getContext(), "Неправильный формат даты!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    year = dateCheck.getYear();
                }

                DoubleCheck doubleCheck = new DoubleCheck(sum.getText().toString());
                Double sumDouble;
                if (!doubleCheck.isCheck()) {
                    Toast.makeText(getContext(), "Неправильно введена сумма!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    sumDouble = doubleCheck.getNumDouble();
                }

                if (TextUtils.isEmpty(idTrans.getText())) {
                    Toast.makeText(getContext(), "Введите наименование сделки", Toast.LENGTH_SHORT).show();
                    return;
                }

                String type = typeTransaction.getSelectedItem().toString();
                String dateString = date.getText().toString();
                String id = idTrans.getText().toString();
                String currency = currencies.getSelectedItem().toString();

                Transaction transaction = new Transaction(id, type, dateString, currency, sumDouble);
                viewModel.addTransaction(year, transaction);

            }
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                if (message != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });


        return viewRoot;
    }


}