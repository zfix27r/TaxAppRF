package com.taxapprf.taxapp.ui.dialogs

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.taxapprf.taxapp.R

class DeleteDialogFragment : DialogFragment() {
    private val prevStackSavedState by lazy {
        findNavController().previousBackStackEntry!!.savedStateHandle
    }

    private var transactionId: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionId = savedInstanceState?.getInt(DELETE_TRANSACTION_ID)
        if (transactionId == null) negativePopBack()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog =
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.delete_dialog_title)
            .setMessage(R.string.delete_dialog_message)
            .setPositiveButton(R.string.delete_dialog_ok) { _, _ ->
                positivePopBack()
            }
            .setNegativeButton(R.string.delete_dialog_cancel) { _, _ ->
                negativePopBack()
            }
            .create()

    private fun positivePopBack() {
        prevStackSavedState[DELETE_TRANSACTION_ID] = transactionId
        findNavController().popBackStack()
    }

    private fun negativePopBack() {
        prevStackSavedState[DELETE_TRANSACTION_ID] = null
        findNavController().popBackStack()
    }

    companion object {
        const val DELETE_TRANSACTION_ID = "delete_transaction_id"
    }
}