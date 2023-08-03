package com.taxapprf.taxapp.ui.transactions

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentTransactionsBinding
import com.taxapprf.taxapp.excel.CreateExcelInLocal
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.transaction.detail.TransactionDetailFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class TransactionsFragment : BaseFragment(R.layout.fragment_transactions) {
    private val binding by viewBinding(FragmentTransactionsBinding::bind)
    private val viewModel by viewModels<TransactionsViewModel>()
    private val adapter = TransactionsAdapter {
        object : TransactionsAdapterCallback {
            override fun onClick(transactionKey: String) {
                navToTransactionDetail(transactionKey)
            }
        }
    }

    private var fileName: File? = null
    private val createExcelInLocal: CreateExcelInLocal? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.loadTransactions(activityViewModel.account)

        binding.recyclerTransactions.adapter = adapter

        viewModel.transactions.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    /*    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            val recyclerView = binding!!.recyclerTransactions
            viewModel.getTransactions()
                .observe(viewLifecycleOwner, object : Observer<List<Transaction?>?> {
                    override fun onChanged(transactions: List<Transaction?>) {
                        Collections.sort(transactions)
                        recyclerViewConfig.setConfig(context, recyclerView, transactions)
                    }
                })
            val textYearTax = binding!!.textTransYearSum
            viewModel.getSumTaxes().observe(viewLifecycleOwner, object : Observer<Double?> {
                override fun onChanged(yearTax: Double) {
                    val s = String.format("Налог за %s год: %s", year, yearTax.toString())
                    textYearTax.text = s
                }
            })
            val buttonAdd: ImageButton = binding!!.buttonTransAdd
            buttonAdd.setOnClickListener { v -> findNavController(v).navigate(R.id.action_transactionsFragment_to_newTransactionFragment) }
            val buttonDelete: ImageButton = binding!!.buttonTransDeleteYear
            buttonDelete.setOnClickListener {
                val dialog = VerificationDialog()
                dialog.show(childFragmentManager, "deleteDialog")
                dialog.verificationStatus.observe(
                    viewLifecycleOwner,
                    object : Observer<Boolean?> {
                        override fun onChanged(status: Boolean) {
                            if (status) {
                                viewModel.deleteYear(year)
                            }
                        }
                    })
            }
            val buttonDownload: ImageButton = binding!!.buttonTransDownload
            buttonDownload.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    if (isStoragePermissionGranted) {
                        fileName = viewModel.downloadStatement()
                        if (fileName!!.exists()) {
                            Snackbar.make(v, "Отчет скачан.", Snackbar.LENGTH_SHORT).show()
                            val uri = FileProvider.getUriForFile(
                                context!!, context!!.applicationContext.packageName + ".provider",
                                fileName!!
                            )
                            Log.d("OLGA", "onClick download uri: " + uri.path)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            startActivity(intent)
                        } else {
                            Snackbar.make(v, "Не удалось скачать отчет.", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            })
            val buttonSend: ImageButton = binding!!.buttonTransSendEmail
            buttonSend.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    if (isStoragePermissionGranted) {
                        fileName = viewModel.createLocalStatement()
                        if (fileName!!.exists()) {
                            val uri = FileProvider.getUriForFile(
                                context!!, context!!.applicationContext.packageName + ".provider",
                                fileName!!
                            )
                            val emailIntent = Intent(Intent.ACTION_SEND)
                            Log.d("OLGA", "onClick emailIntent uri: " + uri.path)
                            emailIntent.type = "vnd.android.cursor.dir/email"
                            emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Расчёт налога от TaxApp")
                            startActivity(Intent.createChooser(emailIntent, "Send email..."))
                        } else {
                            Snackbar.make(v, "Не удалось отправить отчет.", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            })
            return viewRoot
        }*/

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        if (fileName != null) {
            fileName!!.delete()
        }
        super.onResume()
    }

    private fun navToTransactionDetail(transactionKey: String) {
        val bundle = bundleOf(TransactionDetailFragment.TRANSACTION_KEY to transactionKey)
        findNavController().navigate(R.id.action_transactionsFragment_to_transactionDetailsFragment)
    }

    //permission is automatically granted on sdk<23 upon installation
    /*    val isStoragePermissionGranted: Boolean
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    true
                } else {
                    ActivityCompat.requestPermissions(
                        activity!!,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                    )
                    false
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                true
            }*/
}