package com.taxapprf.taxapp.ui.transactions;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taxapprf.taxapp.usersdata.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionItemView> {
    private Context context;
    private List<Transaction> transactions;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionItemView(context, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionItemView holder, int position) {
        holder.bind(transactions.get(position));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}
