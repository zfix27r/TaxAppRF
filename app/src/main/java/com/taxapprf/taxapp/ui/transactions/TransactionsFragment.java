package com.taxapprf.taxapp.ui.transactions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.databinding.FragmentTransactionsBinding;
import com.taxapprf.taxapp.excel.CreateExcelStatement;
import com.taxapprf.taxapp.excel.SendExcelStatement;
import com.taxapprf.taxapp.usersdata.Settings;
import com.taxapprf.taxapp.usersdata.Transaction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransactionsFragment extends Fragment {
    private FragmentTransactionsBinding binding;
    private TransactionsViewModel viewModel;

    private String year;
    private SharedPreferences settings;
    private File fileName; // или удалить или прописать потом автоматическое открытие файла

    public TransactionsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(TransactionsViewModel.class);
        binding = FragmentTransactionsBinding.inflate(inflater, container, false);
        View viewRoot = binding.getRoot();

        settings = getContext().getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
        year = settings.getString(Settings.YEAR.name(), "");

        RecyclerView recyclerView = binding.recyclerTransactions;
        List<Transaction> transactions = new ArrayList<>();
        RecyclerViewTransactionsConfig recyclerViewConfig = new RecyclerViewTransactionsConfig();
        recyclerViewConfig.setConfig(getContext(), recyclerView, transactions, null);

        viewModel.getTransactions().observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                viewModel.getKeys().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> keys) {
                        recyclerViewConfig.setConfig(getContext(), recyclerView, transactions, keys);
                    }
                });
            }
        });

        TextView textYearTax = binding.textTransYearSum;
        viewModel.getSumTaxes().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double yearTax) {
                String s = String.format(" Налог за %s год: %,3f", year, yearTax);
                textYearTax.setText(s);
            }
        });

        ImageButton buttonAdd = binding.buttonTransAdd;
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_transactionsFragment_to_newTransactionFragment);
            }
        });

        ImageButton buttonDelete = binding.buttonTransDeleteYear;
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteYear(year);

            }
        });

        ImageButton buttonDownload= binding.buttonTransDownload;

        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new CheckPermission(getContext()).isStoragePermissionGranted()){
                    return;
                }
                viewModel.getTransactions().observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
                    @Override
                    public void onChanged(List<Transaction> transactions) {
                        viewModel.getSumTaxes().observe(getViewLifecycleOwner(), new Observer<Double>() {
                            @Override
                            public void onChanged(Double sumTaxes) {
                                try {
                                    CreateExcelStatement excelStatement = new CreateExcelStatement(year, sumTaxes, transactions);
                                    excelStatement.create();
                                    fileName = excelStatement.getFileName();
                                    Toast.makeText(getContext(), "Отчет скачан.", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    Toast.makeText(getContext(), "Не удалось создать файл", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                });
            }
        });


        //Добавить подтверждение email!!!!!!!!
        ImageButton buttonSend= binding.buttonTransSendEmail;
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = settings.getString(Settings.EMAIL.name(), "");
                viewModel.getTransactions().observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
                    @Override
                    public void onChanged(List<Transaction> transactions) {
                        viewModel.getSumTaxes().observe(getViewLifecycleOwner(), new Observer<Double>() {
                            @Override
                            public void onChanged(Double sumTaxes) {
                                SendExcelStatement sendExcelStatement = new SendExcelStatement(getContext(), email, year, sumTaxes, transactions);
                                try {
                                    sendExcelStatement.send();
                                    Toast.makeText(getContext(), "Отчет отправлен на email.", Toast.LENGTH_SHORT).show();
                                } catch (InterruptedException exception) {
                                    Toast.makeText(getContext(), "Не удалось отправить отчет!", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
                    }
                });
            }
        });




        return viewRoot;
    }
}