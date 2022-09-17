package com.omi.todo.ui

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.omi.todo.R
import com.omi.todo.databinding.FragmentTaskBinding
import com.omi.todo.dto.User
import com.omi.todo.utils.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference(Constant.USER_NODE)
        binding.btnSaveInfo.setOnClickListener { navigate ->
            taskUser()
        }
    }

    private fun taskUser() {
        val name = binding.etUsername.text.toString()
        val age = binding.etAge.text.toString()
        val dateOfBirth = binding.etDateOfBirth.text.toString()
        val email = binding.etEmail.text.toString()
        binding.apply {
            if (validation(name, age, dateOfBirth, email)) {
                binding.progressBar.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val usersDetails = User(name, age, dateOfBirth, email)
                        getInfo(usersDetails)
                    } catch (e: Exception) {
                        binding.progressBar.visibility = View.INVISIBLE
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@TaskFragment.requireContext(),
                                e.message,
                                Toast.LENGTH_SHORT)
                        }
                    }
                }
            }
        }
    }

    private fun getInfo(usersDetails: User) {
        usersDetails?.let { user ->
            database.child(Constant.TASK_NODE)
                .push()
                .setValue(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.progressBar.visibility = View.INVISIBLE
                        val mainFragment = MainFragment()
                        val transaction = activity?.supportFragmentManager?.beginTransaction()
                        transaction?.replace(R.id.newsNavHostFragment, mainFragment)
                        transaction?.commit()
                    } else {
                        binding.progressBar.visibility = View.INVISIBLE
                        Snackbar.make(binding.root,
                            Constant.TASK_SVAE_TEXT,
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
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = Constant.EMAIL_INVALID_TEXT
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