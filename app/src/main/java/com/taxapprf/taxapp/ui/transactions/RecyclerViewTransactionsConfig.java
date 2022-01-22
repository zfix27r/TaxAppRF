package com.taxapprf.taxapp.ui.transactions;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.taxapprf.taxapp.usersdata.Transaction;

import java.util.List;

public class RecyclerViewTransactionsConfig {
    private Context context;
    private TransactionAdapter transactionAdapter;

    public void setConfig (Context context, RecyclerView recyclerView, List<Transaction> transactions){
        this.context = context;
        transactionAdapter = new TransactionAdapter (context, transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(transactionAdapter);
    }


}
