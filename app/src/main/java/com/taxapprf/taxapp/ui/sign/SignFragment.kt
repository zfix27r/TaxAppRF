package com.taxapprf.taxapp.ui.sign

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.app.R
import com.taxapprf.taxapp.app.databinding.FragmentSignBinding
import com.taxapprf.taxapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignFragment : BaseFragment(R.layout.fragment_sign) {
    private val binding by viewBinding(FragmentSignBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.updateTitles()
        toolbar.updateMenu()

        binding.buttonSignSignIn.setOnClickListener { navToSignIn() }
        binding.buttonSignSingUp.setOnClickListener { navToSignUp() }
    }

    private fun navToSignIn() {
        findNavController().navigate(R.id.action_sign_to_sign_in)
    }

    private fun navToSignUp() {
        findNavController().navigate(R.id.action_sign_to_sign_up)
    }
}