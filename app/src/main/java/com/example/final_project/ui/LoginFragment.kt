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
import com.example.final_project.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels {
        AppViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        InputUtils.enableMultilingualInput(binding.etUsername)
        InputUtils.enableMultilingualPasswordInput(binding.etPassword)

        CelebrationHelper.fadeInContent(
            binding.tvLoginTitle,
            binding.tvLoginSubtitle,
            binding.tilUsername,
            binding.tilPassword,
            binding.btnLogin,
            binding.tvSignUp
        )

        binding.btnLogin.setOnClickListener {
            authViewModel.login(
                binding.etUsername.text.toString(),
                binding.etPassword.text.toString()
            )
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        authViewModel.authEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                AuthEvent.LoggedIn -> {
                    authViewModel.clearEvent()
                    findNavController().navigate(R.id.action_loginFragment_to_tasksFragment)
                }
                is AuthEvent.Error -> {
                    val message = when (event.error) {
                        AuthError.EMPTY_FIELDS -> getString(R.string.error_empty_fields)
                        AuthError.INVALID_CREDENTIALS -> getString(R.string.error_invalid_credentials)
                        AuthError.USER_EXISTS -> getString(R.string.error_user_exists)
                        AuthError.UNKNOWN -> getString(R.string.error_invalid_credentials)
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
