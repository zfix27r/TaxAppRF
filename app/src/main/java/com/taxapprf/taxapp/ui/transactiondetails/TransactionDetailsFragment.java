package com.taxapprf.taxapp.ui.transactiondetails;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.databinding.FragmentTransactionDetailsBinding;
import com.taxapprf.taxapp.ui.VerificationDialog;
import com.taxapprf.taxapp.ui.newtransaction.DateCheck;
import com.taxapprf.taxapp.ui.newtransaction.DoubleCheck;
import com.taxapprf.taxapp.usersdata.Settings;
import com.taxapprf.taxapp.usersdata.Transaction;

public class TransactionDetailsFragment extends Fragment {
    private FragmentTransactionDetailsBinding binding;
    private TransactionDetailsViewModel viewModel;


    public TransactionDetailsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(TransactionDetailsViewModel.class);
        binding = FragmentTransactionDetailsBinding.inflate(inflater, container, false);
        View viewRoot = binding.getRoot();
        SharedPreferences settings = getContext().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);

        Spinner typeTransaction = binding.spinnerDetailType;
        EditText idTrans = binding.editDetailId;
        EditText date = binding.editDetailDate;
        EditText sum = binding.editDetailSum;
        Spinner currencies = binding.spinnerDetailCurrencies;

        final String key = getArguments().getString("key");
        Log.d("OLGA - TransDetail", "key = getArguments(): " + key);
        final Double oldSum = getArguments().getDouble("sum", 0.00);
        final Double oldSumRub = getArguments().getDouble("sumRub", 0.00);
        final String oldCurrency = getArguments().getString("currency");
        final String oldIdTrans = getArguments().getString("id", "");
        final String oldDate = getArguments().getString("date", "");
        final String oldType = getArguments().getString("type");

        idTrans.setText(oldIdTrans);
        date.setText(oldDate);
        sum.setText(oldSum.toString());

        final String[] arrayTypeTrans = getResources().getStringArray(R.array.type_transaction);
        final String[] arrayCurrencies = getResources().getStringArray(R.array.currencies);

        final ArrayAdapter<String> currenciesArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, arrayCurrencies);
        currenciesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencies.setAdapter(currenciesArrayAdapter);
        currencies.setSelection(currenciesArrayAdapter.getPosition(oldCurrency));

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

        typeTransArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeTransaction.setAdapter(typeTransArrayAdapter);
        typeTransaction.setSelection(typeTransArrayAdapter.getPosition(oldType));

        Button buttonUpdate = binding.buttonDetailUpdate;
        Button buttonDelete = binding.buttonDetailDelete;

        //String account = settings.getString(Settings.ACCOUNT.name(), "");
        String oldYear = settings.getString(Settings.YEAR.name(), "");

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerificationDialog dialog = new VerificationDialog();
                dialog.show(getChildFragmentManager(), "deleteTransDialog");
                dialog.getVerificationStatus().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean status) {
                        if (status) {
                            Log.d("OLGA - TransDetail", "onChanged: key " + key);
                            viewModel.deleteTransaction(oldYear, key, oldSumRub);
                        }
                    }
                });

            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(typeTransaction.getSelectedItemPosition() == 0) {
                    Snackbar.make(v, "Выберите тип сделки.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(currencies.getSelectedItemPosition() == 0) {
                    Snackbar.make(v, "Выберите валюту сделки.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                DateCheck dateCheck = new DateCheck(date.getText().toString());
                String currentYear;
                if (!dateCheck.check()) {
                    Snackbar.make(v, "Неправильный формат даты!", Snackbar.LENGTH_SHORT).show();
                    return;
                } else {
                    currentYear = dateCheck.getYear();
                }
                DoubleCheck doubleCheck = new DoubleCheck(sum.getText().toString());
                Double sumDouble;
                if (!doubleCheck.isCheck()) {
                    Snackbar.make(v, "Неправильно введена сумма!", Snackbar.LENGTH_SHORT).show();
                    return;
                } else {
                    sumDouble = doubleCheck.getNumDouble();
                }

                if (TextUtils.isEmpty(idTrans.getText())) {
                    Snackbar.make(v, "Введите наименование сделки", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                String type = typeTransaction.getSelectedItem().toString();
                String dateString = date.getText().toString();
                String id = idTrans.getText().toString();
                String currency = currencies.getSelectedItem().toString();

                Transaction transaction = new Transaction(id, type, dateString, currency, sumDouble);

                viewModel.updateTransaction(transaction, oldYear, currentYear, key, oldSumRub);

            }
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                if (message != null) {
                    Snackbar.make(viewRoot, message, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getIsSuccessDelete().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccessDelete) {
                if (isSuccessDelete) {
                    Navigation.findNavController(viewRoot).navigate(R.id.action_transactionDetailsFragment_to_transactionsFragment);
                }
            }
        });

        viewModel.getIsSuccessUpdate().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccessUpdate) {
                if (isSuccessUpdate) {
                    Navigation.findNavController(viewRoot).navigate(R.id.action_transactionDetailsFragment_to_taxesFragment);
                }
            }
        });

        return viewRoot;
    }

    @Override
    public void onStop() {
        InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
        super.onStop();
    }
}