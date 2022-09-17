package com.omi.todo.ui.siginup

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.omi.todo.R
import com.omi.todo.databinding.FragmentRegisterBinding
import com.omi.todo.dto.User
import com.omi.todo.utils.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterFragment : Fragment() {


    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding.btnSignUp.setOnClickListener { navigate ->
            registerUser(navigate)
        }
        binding.btnLogin.setOnClickListener {
            it.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun registerUser(navigate: View) {
        val name = binding.etUsername.text.toString()
        val age = binding.etAge.text.toString()
        val dateOfBirth = binding.etDateOfBirth.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if (validation(name, age, dateOfBirth, email, password)) {
            binding.progressBar.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val usersDetails = User(name, age, dateOfBirth, email, password)
                            getInfo(usersDetails, navigate)
                        } else {
                            binding.progressBar.visibility = View.INVISIBLE
                            Snackbar.make(binding.root,
                                "Email Already Exist",
                                Snackbar.LENGTH_SHORT).show()
                            binding.txtError.text = "Email Already Exist"
                        }
                    }
                } catch (e: Exception) {
                    binding.progressBar.visibility = View.INVISIBLE
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterFragment.requireContext(),
                            e.message,
                            Toast.LENGTH_SHORT)
                    }
                }
            }
        }
    }


    private fun getInfo(usersDetails: User, navigate: View) {
        usersDetails?.let { user ->
            FirebaseDatabase.getInstance().getReference(Constant.USER_NODE)
                .child(Constant.PERSONAL_INFO_NODE)
                .child(FirebaseAuth.getInstance().currentUser?.uid ?: "")
                .setValue(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.progressBar.visibility = View.INVISIBLE
                        navigate.findNavController()
                            .navigate(R.id.action_registerFragment_to_loginFragment)
                        Snackbar.make(binding.root,
                            Constant.REGISTER_TEXT,
                            Snackbar.LENGTH_SHORT).show()
                    } else {
                        binding.progressBar.visibility = View.INVISIBLE
                        Snackbar.make(binding.root,
                            Constant.NOT_GETTING_REGISTER_TEXT,
                            Snackbar.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validation(
        name: String,
        age: String,
        dateOfBirth: String,
        email: String,
        password: String,
    ): Boolean {
        return if (name.isEmpty()) {
            binding.etUsername.error = Constant.NAME_EMPTY_TEXT
            false
        } else if (age.isEmpty()) {
            binding.etAge.error = Constant.AGE_EMPTY_TEXT
            false
        } else if (dateOfBirth.isEmpty()) {
            binding.etDateOfBirth.error = Constant.DATE_OF_BIRTH_EMPTY_TEXT
            false
        } else if (email.isEmpty()) {
            binding.etEmail.error = Constant.EMAIL_EMPTY_TEXT
            false
        } else if (password.isEmpty()) {
            binding.etPassword.error = Constant.PASSWORD_EMPTY_TEXT
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = Constant.EMAIL_INVALID_TEXT
            false
        } else if (password.length <= 6) {
            binding.etPassword.error = Constant.MININUM_6_CHARACTER_PASSWORD_TEXT
            false
        } else if (!password.matches(".*[A-Z].*".toRegex())) {
            binding.etPassword.error = Constant.MUST_CONTIAN_1_UPPER_TEXT
            false
        } else if (!password.matches(".*[a-z].*".toRegex())) {
            binding.etPassword.error = Constant.MUST_CONTIAN_1_LOWER_TEXT
            false
        } else if (!password.matches(".*[@#\$%^&+=].*".toRegex())) {
            binding.etPassword.error = Constant.MUST_CONTIAN_1_SPECIAL_CHARACTER_TEXT
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