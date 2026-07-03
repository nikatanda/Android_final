package com.example.final_project.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.final_project.R
import com.example.final_project.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels {
        AppViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        InputUtils.enableMultilingualInput(binding.etUsername)
        InputUtils.enableMultilingualPasswordInput(binding.etPassword)

        CelebrationHelper.fadeInContent(
            binding.tvSignUpTitle,
            binding.tvSignUpSubtitle,
            binding.tilUsername,
            binding.tilPassword,
            binding.btnSignUp,
            binding.tvLogin
        )

        binding.btnSignUp.setOnClickListener {
            authViewModel.register(
                binding.etUsername.text.toString(),
                binding.etPassword.text.toString()
            )
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        authViewModel.authEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                AuthEvent.Registered -> {
                    Toast.makeText(
                        requireContext(),
                        R.string.registration_success,
                        Toast.LENGTH_SHORT
                    ).show()
                    authViewModel.clearEvent()
                    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                }
                is AuthEvent.Error -> {
                    val message = when (event.error) {
                        AuthError.EMPTY_FIELDS -> getString(R.string.error_empty_fields)
                        AuthError.USER_EXISTS -> getString(R.string.error_user_exists)
                        else -> getString(R.string.error_invalid_credentials)
                    }
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    authViewModel.clearEvent()
                }
                else -> Unit
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
