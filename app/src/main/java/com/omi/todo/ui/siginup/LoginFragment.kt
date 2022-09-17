package com.omi.todo.ui.siginup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.omi.todo.MainActivity
import com.omi.todo.R
import com.omi.todo.databinding.FragmentLoginBinding
import com.omi.todo.utils.Constant.Companion.EMAIL_EMPTY_TEXT
import com.omi.todo.utils.Constant.Companion.EMAIL_INVALID_TEXT
import com.omi.todo.utils.Constant.Companion.LOGIN_TEXT
import com.omi.todo.utils.Constant.Companion.PASSWORD_EMPTY_TEXT
import com.omi.todo.utils.Constant.Companion.PASSWORD_GET_WRONG_TEXT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding.btnLogin.setOnClickListener {
            loginUser()
        }
        binding.btnSignUp.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun loginUser() {

        val email = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        if (validation(email, password)) {
            binding.progressBar.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                binding.progressBar.visibility = View.INVISIBLE
                                startActivity(Intent(this@LoginFragment.requireContext(),
                                    MainActivity::class.java))
                                Snackbar.make(binding.root, LOGIN_TEXT, Snackbar.LENGTH_SHORT)
                                    .show()
                            } else {
                                binding.progressBar.visibility = View.INVISIBLE
                                Snackbar.make(binding.root,
                                    PASSWORD_GET_WRONG_TEXT,
                                    Snackbar.LENGTH_SHORT).show()
                                binding.txtError.text = PASSWORD_GET_WRONG_TEXT
                            }
                        }
                } catch (e: Exception) {
                    binding.progressBar.visibility = View.INVISIBLE
                    withContext(Dispatchers.Main) {
                        Log.d(LOGIN_TEXT, e.localizedMessage)
                    }
                }
            }
        }
    }

    private fun validation(email: String, password: String): Boolean {
        return if (email.isEmpty()) {
            binding.txtEmail.error = EMAIL_EMPTY_TEXT
            false
        } else if (password.isEmpty()) {
            binding.txtPassword.error = PASSWORD_EMPTY_TEXT
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtEmail.error = EMAIL_INVALID_TEXT
            false
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}