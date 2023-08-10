package com.taxapprf.taxapp.ui.sign

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentSignBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignFragment : Fragment(R.layout.fragment_sign) {
    private val binding by viewBinding(FragmentSignBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSignIn.setOnClickListener { navToSignIn() }
        binding.buttonSignUp.setOnClickListener { navToSignUp() }
    }

    private fun navToSignIn() {
        findNavController().navigate(R.id.action_sign_to_sign_in)
    }

    private fun navToSignUp() {
        findNavController().navigate(R.id.action_sign_to_sign_up)
    }
}