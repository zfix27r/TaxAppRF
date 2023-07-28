package com.taxapprf.taxapp.ui.transactions;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.taxapprf.taxapp.R;
import com.taxapprf.domain.Transaction;

public class TransactionItemView extends RecyclerView.ViewHolder{
    private Context context;

    private TextView idTrans;
    private TextView type;
    private TextView date;
    private TextView sum;
    private TextView currency;
    private TextView cb;
    private TextView sumRub;

    private String key;
    private Transaction transaction;


    public TransactionItemView(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).
                inflate(R.layout.transaction_list_item, parent, false));
        idTrans = itemView.findViewById(R.id.textTransItemId);
        type = itemView.findViewById(R.id.textTransItemType);
        date = itemView.findViewById(R.id.textTransItemDate);
        sum = itemView.findViewById(R.id.textTransItemSum);
        currency = itemView.findViewById(R.id.textTransItemCurrency);
        cb = itemView.findViewById(R.id.textTransItemCB);
        sumRub = itemView.findViewById(R.id.textTransItemSumRub);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = itemView.getTransitionName();
                Bundle bundle = new Bundle();
                bundle.putString("key", key);
                bundle.putString("id", transaction.getId());
                bundle.putString("type", transaction.getType());
                bundle.putString("date", transaction.getDate());
                bundle.putString("currency", transaction.getCurrency());
                bundle.putDouble("sum", transaction.getSum());
                bundle.putDouble("sumRub", transaction.getSumRub());

                Navigation.findNavController(v).navigate(R.id.action_transactionsFragment_to_transactionDetailsFragment, bundle);
            }
        });
    }

    public void bind (Transaction transaction) {
        idTrans.setText(transaction.getId());
        type.setText(transaction.getType());
        date.setText(transaction.getDate());

        String sumString = transaction.getSum().toString();
        sum.setText(sumString );

        currency.setText(String.valueOf(transaction.getCurrency()));
        String cbString = transaction.getRateCentralBank().toString();
        cb.setText(cbString);

        String sumRubString = transaction.getSumRub().toString();
        sumRub.setText(sumRubString);

        this.key = transaction.getKey();
        this.transaction = transaction;

    }
}
