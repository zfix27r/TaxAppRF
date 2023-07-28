package com.taxapprf.taxapp.ui.taxes

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentTaxesBinding
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.checkStoragePermission
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException


@AndroidEntryPoint
class TaxesFragment : BaseFragment(R.layout.fragment_taxes) {
    private val binding by viewBinding(FragmentTaxesBinding::bind)
    private val viewModel by viewModels<TaxesViewModel>()
    private val adapter = TaxesAdapter {
        object : TaxesAdapterCallback {
            override fun onClick(year: String) {
                /*editor.putString(Settings.YEAR.name, year!!.text.toString())
                editor.apply()
                editor.putString(Settings.TAXSUM.name, yearSum!!.text.toString())
                editor.apply()
                findNavController(v).navigate(R.id.action_taxesFragment_to_transactionsFragment)*/
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
                try {
                    if (resultCode == Activity.RESULT_OK) {
                        val filePath = data!!.data!!.path
                    }
                } catch (e: IOException) {
                    /*                    Snackbar.make(
                                            getView(),
                                            "Не удалось конвертипровать файл",
                                            Snackbar.LENGTH_SHORT
                                        ).show()*/
                }
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
        findNavController().navigate(R.id.action_taxesFragment_to_transactionsFragment)
    }

    private fun navToTransactionNew() {
        findNavController().navigate(R.id.action_taxesFragment_to_newTransactionFragment)
    }

    companion object {
        private const val PICK_FILE_RESULT_CODE = 1001
    }
}