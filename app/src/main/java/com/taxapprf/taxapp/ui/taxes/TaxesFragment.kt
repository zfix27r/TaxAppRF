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

    private val PICKFILE_RESULT_CODE = 1001
    private var filePath: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerYearStatements.adapter = adapter

        viewModel.taxes.observe(viewLifecycleOwner) {
            println("@@@@" + it)
            adapter.submitList(it)
        }
    }


    /*        viewModel!!.getYearStatements()!!
                .observe(getViewLifecycleOwner(), object : Observer<List<YearStatement?>?> {
                    fun onChanged(yearStatements: List<YearStatement?>) {
                        recyclerViewConfig.setConfig(recyclerView, getContext(), yearStatements)
                    }
                })
            val buttonAddTrans: Button = binding.buttonTaxesAddTrans
            buttonAddTrans.setOnClickListener { v: View? ->
                findNavController(
                    v!!
                ).navigate(R.id.action_taxesFragment_to_newTransactionFragment)
            }
            val buttonLoading: Button = binding.buttonTaxesLoading
            buttonLoading.setOnClickListener {
                if (isStoragePermissionGranted()) {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "application/vnd.ms-excel"
                    startActivityForResult(intent, PICKFILE_RESULT_CODE)
                }
            }
            return viewRoot*/


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICKFILE_RESULT_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    filePath = data!!.data!!.path
                }
                try {
                    //viewModel!!.addTransactions(filePath)
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

    private fun isStoragePermissionGranted(): Boolean {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        return if (ContextCompat.checkSelfPermission(requireContext(), permission)
            == PackageManager.PERMISSION_GRANTED
        ) true
        else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), 1)
            false
        }
    }

    private fun navToTransactions(year: String) {
        val bundle = bundleOf()
        findNavController().navigate(R.id.action_taxesFragment_to_transactionsFragment)
    }
}