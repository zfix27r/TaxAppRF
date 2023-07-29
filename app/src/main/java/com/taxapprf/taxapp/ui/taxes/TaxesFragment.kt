package com.taxapprf.taxapp.ui.taxes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentTaxesBinding
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.checkStoragePermission
import com.taxapprf.taxapp.ui.transactions.TransactionsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TaxesFragment : BaseFragment(R.layout.fragment_taxes) {
    private val binding by viewBinding(FragmentTaxesBinding::bind)
    private val viewModel by viewModels<TaxesViewModel>()
    private val adapter = TaxesAdapter {
        object : TaxesAdapterCallback {
            override fun onClick(year: String) {
                navToTransactions(year)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonTaxesAddTrans.setOnClickListener { navToTransactionNew() }
        binding.buttonTaxesLoading.setOnClickListener { navToSystemStorage() }
        binding.recyclerYearStatements.adapter = adapter

        viewModel.taxes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICK_FILE_RESULT_CODE -> {
                viewModel.saveTaxesFromExcel(data)
            }
        }
    }

    private fun navToSystemStorage() {
        if (requireActivity().checkStoragePermission()) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/vnd.ms-excel"
            startActivityForResult(intent, PICK_FILE_RESULT_CODE)
        }
    }

    private fun navToTransactions(year: String) {
        val bundle = bundleOf(TransactionsViewModel.YEAR to year)
        findNavController().navigate(R.id.action_taxesFragment_to_transactionsFragment, bundle)
    }

    private fun navToTransactionNew() {
        findNavController().navigate(R.id.action_taxesFragment_to_newTransactionFragment)
    }

    companion object {
        private const val PICK_FILE_RESULT_CODE = 1001
    }
}