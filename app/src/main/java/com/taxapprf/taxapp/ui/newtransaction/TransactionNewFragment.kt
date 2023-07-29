package com.taxapprf.taxapp.ui.newtransaction

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.taxapprf.domain.Transaction
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentNewTransactionBinding
import com.taxapprf.taxapp.ui.BaseFragment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

class TransactionNewFragment : BaseFragment(R.layout.fragment_new_transaction) {
    private val binding by viewBinding(FragmentNewTransactionBinding::bind)
    private val viewModel by viewModels<TransactionNewViewModel>()

    var dateAndTime = Calendar.getInstance()
    var date: EditText? = null


    fun onCreateView(
        inflater: LayoutInflater?, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val typeTransaction: Spinner = binding.spinnerNewTransType
        val idTrans: EditText = binding.editNewTransId
        date = binding.editNewTransDate
        val sum: EditText = binding.editNewTransSum
        val currencies: Spinner = binding.spinnerNewTransCurrencies

        val arrayTypeTrans = resources.getStringArray(R.array.type_transaction)
        val arrayCurrencies = resources.getStringArray(R.array.currencies)

        val currenciesArrayAdapter =
            ArrayAdapter(this.context!!, android.R.layout.simple_spinner_item, arrayCurrencies)
        currenciesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencies.adapter = currenciesArrayAdapter
        currencies.setSelection(currenciesArrayAdapter.getPosition("USD"))
        val typeTransArrayAdapter: ArrayAdapter<String> = object : ArrayAdapter<String?>(
            this.context!!, android.R.layout.simple_spinner_item, arrayTypeTrans
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.BLACK)
                }
                return view
            }
        }
        typeTransArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeTransaction.adapter = typeTransArrayAdapter
        val image: ImageView = binding.imageNewTransDate
        image.setOnClickListener { v -> setDate(v) }


//        Button buttonCancel = binding.buttonNewTransCancel;
//        buttonCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(v).navigate(R.id.action_newTransactionFragment_to_taxesFragment);
//            }
//        });
        val buttonAdd: Button = binding.buttonNewTransAdd
        buttonAdd.setOnClickListener(View.OnClickListener { v ->
            if (typeTransaction.selectedItemPosition == 0) {
                Snackbar.make(v, "Выберите тип сделки.", Snackbar.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (currencies.selectedItemPosition == 0) {
                Snackbar.make(v, "Выберите валюту сделки.", Snackbar.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val dateCheck = DateCheck(date!!.text.toString())
            val year: String
            year = if (!dateCheck.check()) {
                Snackbar.make(v, "Неправильный формат даты!", Snackbar.LENGTH_SHORT).show()
                return@OnClickListener
            } else {
                dateCheck.year
            }
            val doubleCheck = DoubleCheck(sum.text.toString())
            val sumDouble: Double
            sumDouble = if (!doubleCheck.isCheck) {
                Snackbar.make(v, "Неправильно введена сумма!", Snackbar.LENGTH_SHORT).show()
                return@OnClickListener
            } else {
                doubleCheck.numDouble
            }
            if (TextUtils.isEmpty(idTrans.text)) {
                Snackbar.make(v, "Введите наименование сделки", Snackbar.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val type = typeTransaction.selectedItem.toString()
            val dateString = date!!.text.toString()
            val id = idTrans.text.toString()
            val currency = currencies.selectedItem.toString()
            val transaction = Transaction(id, type, dateString, currency, sumDouble)
            viewModel!!.addTransaction(year, transaction)
        })
        viewModel!!.message.observe(viewLifecycleOwner, object : Observer<String?> {
            fun onChanged(message: String) {
                if (message != null) {
                    Snackbar.make(viewRoot, message, Snackbar.LENGTH_SHORT).show()
                }
            }
        })
        return viewRoot
    }

    private fun setDate(v: View) {
        DatePickerDialog(
            requireContext(), d,
            dateAndTime[Calendar.YEAR],
            dateAndTime[Calendar.MONTH],
            dateAndTime[Calendar.DAY_OF_MONTH]
        )
            .show()
    }

    private val d =
        OnDateSetListener { view, year, month, dayOfMonth ->
            dateAndTime[Calendar.YEAR] = year
            dateAndTime[Calendar.MONTH] = month
            dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
            setInitialDateTime()
        }

    private fun setInitialDateTime() {
        val mDate = dateAndTime.time
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        val dateStr = dateFormat.format(mDate)
        date!!.setText(dateStr)
    }

}