package com.taxapprf.taxapp.ui.rates.today;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.retrofit2.Currencies;
import com.taxapprf.taxapp.retrofit2.CurrencyRate;

import java.util.List;

public class RecyclerViewCurrencyConfig {
    private Context context;
    private CurrencyAdapter currencyAdapter;

    public void setConfig (RecyclerView recyclerView, Context context, Currencies currencies){
        this.context = context;
        this.currencyAdapter = new CurrencyAdapter(currencies);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(currencyAdapter);
    }

    class CurrencyItemView extends RecyclerView.ViewHolder {
        private TextView currencyCode;
        private TextView currencyName;
        private TextView currencyRate;


        public CurrencyItemView(ViewGroup parent) {
            super(LayoutInflater.from(context).inflate(R.layout.currency_rate_list_item, parent, false));
            currencyCode = itemView.findViewById(R.id.textViewCurrencyCode);
            currencyName = itemView.findViewById(R.id.textViewCurrencyName);
            currencyRate = itemView.findViewById(R.id.textViewCurrencyRate);
        }

        public void bind (CurrencyRate rate) {
            currencyCode.setText(rate.getCharCode());
            currencyName.setText(rate.getName());
            currencyRate.setText(rate.getValueString());
        }
    }



    class CurrencyAdapter extends RecyclerView.Adapter<CurrencyItemView>{
        List<CurrencyRate> rateList;

        public CurrencyAdapter(Currencies currencies) {
            rateList = currencies.getCurrencyList();
        }

        @NonNull
        @Override
        public CurrencyItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CurrencyItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CurrencyItemView holder, int position) {
            holder.bind(rateList.get(position));
        }

        @Override
        public int getItemCount() {
            return rateList.size();
        }
    }
}
