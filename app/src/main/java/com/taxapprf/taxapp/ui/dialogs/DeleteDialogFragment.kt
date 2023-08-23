package com.taxapprf.taxapp.ui.dialogs

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.taxapprf.taxapp.R

class DeleteDialogFragment : DialogFragment() {
    private val prevStackSavedState by lazy {
        findNavController().previousBackStackEntry!!.savedStateHandle
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog =
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.delete_dialog_title)
            .setMessage(R.string.delete_dialog_message)
            .setPositiveButton(R.string.delete_dialog_ok) { _, _ ->
                popBackStackWithBundle(true)
            }
            .setNegativeButton(R.string.delete_dialog_cancel) { _, _ ->
                popBackStackWithBundle(false)
            }
            .create()

    private fun popBackStackWithBundle(state: Boolean) {
        prevStackSavedState[DELETE_ACCEPTED] = state
        findNavController().popBackStack()
    }

    companion object {
        const val DELETE_ACCEPTED = "delete_accepted"
    }
}