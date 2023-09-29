package com.taxapprf.taxapp.ui.dialogs

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.taxapprf.taxapp.R

class DeleteDialogFragment : DialogFragment() {
    private val args by navArgs<DeleteDialogFragmentArgs>()

    private val prevStackSavedState by lazy {
        findNavController().previousBackStackEntry!!.savedStateHandle
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog =
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.delete_dialog_title)
            .setMessage(R.string.delete_dialog_message)
            .setPositiveButton(R.string.delete_dialog_ok) { _, _ ->
                popBackStack(true)
            }
            .setNegativeButton(R.string.delete_dialog_cancel) { _, _ ->
                popBackStack()
            }
            .create()

    private fun popBackStack(isPositive: Boolean = false) {
        prevStackSavedState[DELETE_ID] = if (isPositive) args.deleteId else null
        findNavController().popBackStack()
    }

    companion object {
        const val DELETE_ID = "delete_id"
    }
}