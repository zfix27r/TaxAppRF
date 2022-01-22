package com.taxapprf.taxapp.ui.taxes;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.taxapprf.taxapp.R;
import com.taxapprf.taxapp.usersdata.Settings;
import com.taxapprf.taxapp.usersdata.YearStatement;

import java.text.DecimalFormat;
import java.util.List;

public class RecyclerYearStatementConfig {
    private Context context;
    private YearStatementAdapter yearStatementAdapter;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;


    public void setConfig(RecyclerView recyclerView, Context context, List<YearStatement> yearStatements){
        this.context = context;
        settings = context.getSharedPreferences(Settings.SETTINGSFILE.name(), Context.MODE_PRIVATE);
        editor= settings.edit();
        yearStatementAdapter = new YearStatementAdapter(yearStatements);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(yearStatementAdapter);
    }

    class YearStatementItemView extends RecyclerView.ViewHolder {
        private TextView year;
        private TextView yearSum;

        private String key;

        public YearStatementItemView(ViewGroup parent){
            super(LayoutInflater.from(context).
                    inflate(R.layout.year_statement_list_item, parent, false));
            year = itemView.findViewById(R.id.textViewYear);
            yearSum = itemView.findViewById(R.id.textViewYearSum);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.putString(Settings.YEAR.name(), String.valueOf(year.getText()));
                    editor.apply();
                    editor.putString(Settings.TAXSUM.name(), String.valueOf(yearSum.getText()));
                    editor.apply();
                    Navigation.findNavController(v).navigate(R.id.action_taxesFragment_to_transactionsFragment);
                }
            });
        }

        public void bind (YearStatement yearStatement) {
            String yearSumStr = yearStatement.getSumTaxes().toString();
            //String yearSumStr = new DecimalFormat("#0.00").format(yearStatement.getSumTaxes());
            year.setText(String.valueOf(yearStatement.getYear()));
            yearSum.setText(yearSumStr);
        }
    }

    class YearStatementAdapter extends RecyclerView.Adapter<YearStatementItemView>{
        private List<YearStatement> yearStatements;

        public YearStatementAdapter(List<YearStatement> yearStatements) {
            this.yearStatements = yearStatements;
        }

        @NonNull
        @Override
        public YearStatementItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new YearStatementItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull YearStatementItemView holder, int position) {
            holder.bind(yearStatements.get(position));
        }

        @Override
        public int getItemCount() {
            return yearStatements.size();
        }
    }
}
