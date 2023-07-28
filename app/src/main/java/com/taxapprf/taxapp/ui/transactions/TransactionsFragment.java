package com.taxapprf.taxapp.ui.transactions;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.databinding.FragmentTransactionsBinding;
import com.taxapprf.taxapp.excel.CreateExcelInLocal;
import com.taxapprf.taxapp.ui.VerificationDialog;
import com.taxapprf.taxapp.usersdata.Settings;
import com.taxapprf.domain.Transaction;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionsFragment extends Fragment {
    private FragmentTransactionsBinding binding;
    private TransactionsViewModel viewModel;

    private String year;
    private SharedPreferences settings;
    private File fileName;
    private  CreateExcelInLocal createExcelInLocal;

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
        recyclerViewConfig.setConfig(getContext(), recyclerView, transactions);

        viewModel.getTransactions().observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                Collections.sort(transactions);
                recyclerViewConfig.setConfig(getContext(), recyclerView, transactions);
            }
        });

        TextView textYearTax = binding.textTransYearSum;
        viewModel.getSumTaxes().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double yearTax) {
                String s = String.format("Налог за %s год: %s", year, yearTax.toString());
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
                VerificationDialog dialog = new VerificationDialog();
                dialog.show(getChildFragmentManager(), "deleteDialog");
                dialog.getVerificationStatus().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean status) {
                        if (status) {
                            viewModel.deleteYear(year);
                        }
                    }
                });

            }
        });



        ImageButton buttonDownload= binding.buttonTransDownload;

        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    fileName = viewModel.downloadStatement();
                    if (fileName.exists()){
                        Snackbar.make(v, "Отчет скачан.", Snackbar.LENGTH_SHORT).show();
                        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", fileName);
                        Log.d("OLGA", "onClick download uri: " + uri.getPath());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    } else {
                        Snackbar.make(v, "Не удалось скачать отчет.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });


        ImageButton buttonSend= binding.buttonTransSendEmail;
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    fileName = viewModel.createLocalStatement();
                    if (fileName.exists()){
                        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", fileName);
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        Log.d("OLGA", "onClick emailIntent uri: " + uri.getPath());
                        emailIntent.setType("vnd.android.cursor.dir/email");
                        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Расчёт налога от TaxApp");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    } else {
                        Snackbar.make(v, "Не удалось отправить отчет.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return viewRoot;
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.removeListener();
    }

    @Override
    public void onResume() {
        if (fileName != null) {
            fileName.delete();
        }
        super.onResume();
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