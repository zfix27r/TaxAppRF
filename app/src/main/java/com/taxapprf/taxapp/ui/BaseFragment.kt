package com.taxapprf.taxapp.ui

import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.taxapprf.taxapp.ui.activity.MainActivity
import com.taxapprf.taxapp.ui.activity.MainViewModel

open class BaseFragment(layoutId: Int) : Fragment(layoutId), BaseFragmentInterface {
    override val mainActivity
        get() = requireActivity() as MainActivity

    override val mainViewModel by activityViewModels<MainViewModel>()

    override val fragment
        get() = this

    override lateinit var baseViewModel: BaseViewModel

    override var actionMode: ActionMode? = null

    protected val currentStackSavedState
        get() = findNavController().currentBackStackEntry!!.savedStateHandle

    protected val fab
        get() = mainActivity.binding.appBarMain.fab

    protected val toolbar
        get() = mainActivity.toolbar

    protected val drawer
        get() = mainActivity.drawer
}