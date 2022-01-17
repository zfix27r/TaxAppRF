package com.taxapprf.taxapp.ui.transactions;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.usersdata.Transaction;

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
                bundle.putString("id", (String) idTrans.getText());
                bundle.putString("type", (String) type.getText());
                bundle.putString("date", (String) date.getText());
                bundle.putString("valuta", String.valueOf(currency.getText())); //Поменять на currency!!!!
                bundle.putDouble("sum", Double.parseDouble(String.valueOf(sum.getText())));
                bundle.putDouble("sumRub", Double.parseDouble(String.valueOf(sumRub.getText())));

                Navigation.findNavController(v).navigate(R.id.action_transactionsFragment_to_transactionDetailsFragment, bundle);
            }
        });
    }

    public void bind (Transaction transaction, String key) {
        idTrans.setText(String.valueOf(transaction.getId()));
        type.setText(String.valueOf(transaction.getType()));
        date.setText(String.valueOf(transaction.getDate()));
        sum.setText(String.valueOf(transaction.getSum()));
        currency.setText(String.valueOf(transaction.getCurrency()));
        cb.setText(String.valueOf(transaction.getRateCentralBank()));
        String sumRubString = String.format("%.2f", transaction.getSumRub());
        sumRub.setText(sumRubString);
        this.key = key;
    }
}
