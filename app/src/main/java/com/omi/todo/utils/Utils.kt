package com.omi.todo.utils

class Utils {
    companion object {


        /* private fun validation(email: String, password: String): Boolean {
             return if (email.isEmpty()) {
                 binding.txtEmail.error = "Email Should Not Be Empty"
                 false
             } else if (password.isEmpty()) {
                 binding.txtPassword.error = "Password Should Not Be Empty"
                 false
             } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                 binding.txtEmail.error = "Invalid Email Address"
                 false
             } else if (password.length <= 7) {
                 binding.txtPassword.error = "Minimum 6 Character Password"
                 false
             } else if (!password.matches(".*[A-Z].*".toRegex())) {
                 binding.txtPassword.error = "Must Contain 1 Upper-case Character"
                 false
             } else if (!password.matches(".*[a-z].*".toRegex())) {
                 binding.txtPassword.error = "Must Contain 1 Lower-case Character"
                 false
             } else if (!password.matches(".*[@#\$%^&+=].*".toRegex())) {
                 binding.txtPassword.error = "Must Contain 1 Special Character (@#\$%^&+=)"
                 false
             } else {
                 binding.progressBar.visibility = View.INVISIBLE
                 true
             }
         }*/
    }
}