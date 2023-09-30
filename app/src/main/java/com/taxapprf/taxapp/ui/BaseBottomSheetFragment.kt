package com.taxapprf.taxapp.ui

import android.app.Dialog
import android.view.View
import androidx.appcompat.view.ActionMode
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.taxapprf.taxapp.ui.activity.MainActivity
import com.taxapprf.taxapp.ui.activity.MainViewModel

open class BaseBottomSheetFragment(layoutId: Int) : BottomSheetDialogFragment(layoutId),
    IBaseFragment {
    override val mainActivity
        get() = requireActivity() as MainActivity
    override val mainViewModel by activityViewModels<MainViewModel>()
    override val fragment
        get() = this
    override lateinit var baseViewModel: BaseViewModel
    override var actionMode: ActionMode? = null

    protected fun Dialog.wrapHeight(): Dialog {
        setOnShowListener {
            findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?.let {
                    val behavior = BottomSheetBehavior.from(it)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
        }

        return this
    }

    protected fun Dialog.halfHeight(): Dialog {
        setOnShowListener {
            findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?.let {
                    val behavior = BottomSheetBehavior.from(it)
                    it.updateLayoutParams {
                        val displayHeight = requireActivity().resources.displayMetrics.heightPixels
                        height = (displayHeight * 0.8).toInt()
                    }
                    behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
        }

        return this
    }

    protected fun Dialog.fullHeight(): Dialog {
        setOnShowListener {
            findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?.let {
                    val behavior = BottomSheetBehavior.from(it)
                    it.updateLayoutParams {
                        val displayHeight = requireActivity().resources.displayMetrics.heightPixels
                        height = displayHeight
                    }
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
        }

        return this
    }
}