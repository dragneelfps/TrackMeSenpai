package com.projectk.partners.clientapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.projectk.partners.clientapp.utils.LoginIntentService
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var progressDialog: ProgressDialog

    companion object {
        private val TAG = "LoginActivity"
        private val LOGIN_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        login.setOnClickListener { login() }
    }

    private fun login() {
        if (!validate()) {
            login.isEnabled = true
            return
        }
        login.isEnabled = false

        progressDialog = ProgressDialog(this@LoginActivity)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Authenticating...")
        progressDialog.show()

        val username = username.text.toString()
        val password = password.text.toString()
        val pendingResult = createPendingResult(LOGIN_REQUEST, Intent(), 0)
        val intent = Intent(applicationContext, LoginIntentService::class.java)
        intent.putExtra("user", username)
        intent.putExtra("pass", password)
        intent.putExtra(LoginIntentService.PENDING_RESULT_EXTRA, pendingResult)
        startService(intent)
        Log.d(TAG, "Starting Login service")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_REQUEST) {
            when (resultCode) {
                LoginIntentService.RESULT_CODE -> {
                    Log.d(TAG, "Got result from loginservice")
                    progressDialog.dismiss()
                    handlerAuthentication(data)
                }
            }
        }
    }

    private fun handlerAuthentication(data: Intent?) {
        if (data == null) {
            Log.d(TAG, "No result intent was sent")
            onLoginFailed("No result intent was sent")
            return
        }
        val responseCode = data.getIntExtra(LoginIntentService.RESPONSE_CODE, -1)

        when (responseCode) {
            -1 -> {
                Log.d(TAG, "No response code was sent")
                onLoginFailed("No response code was sent")
            }
            LoginIntentService.CORRECT_CRED_RESPONSE -> {
                Log.d(TAG, "Correct Credentials")
                Toast.makeText(this, "Correct Credentials", Toast.LENGTH_LONG).show()
                onLoginSuccess()
            }
            LoginIntentService.INCORRECT_CRED_RESPONSE -> {
                Log.d(TAG, "Incorrect Credentials")
                Toast.makeText(this, "Incorrect Credentials", Toast.LENGTH_LONG).show()
                onLoginFailed("Incorrect Credentials")
            }
            LoginIntentService.ERROR_RESPONSE -> {
                Log.d(TAG, "Error during attempting logging in")
                Toast.makeText(this, "Error during attempting logging in ", Toast.LENGTH_LONG).show()
                onLoginFailed("Error during attempting logging in")
            }
            LoginIntentService.NULL_RESPONSE -> {
                Log.d(TAG, "Server sent null response")
                Toast.makeText(this, "Server sent null response", Toast.LENGTH_LONG).show()
                onLoginFailed("Server sent null response")
            }
            LoginIntentService.NO_INTERNET_ACCESS -> {
                Log.d(TAG, "Network Error")
                Toast.makeText(this, "Network Error", Toast.LENGTH_LONG).show()
                onLoginFailed("Network Error")
            }
            LoginIntentService.SERVER_NOT_FOUND -> {
                Log.d(TAG, "Server not found")
                Toast.makeText(this, "Server not found", Toast.LENGTH_LONG).show()
                onLoginFailed("Server not found")
            }
        }
    }

    override fun onBackPressed() {
        setResult(MainActivity.RESULT_BACK_PRESSED)
        super.onBackPressed()
    }

    private fun onLoginSuccess() {
        login.isEnabled = true
        finish()
    }

    private fun onLoginFailed(info: String) {
        Toast.makeText(baseContext, info, Toast.LENGTH_LONG).show()
        login.isEnabled = true
    }

    private fun validate(): Boolean {
        var valid = true
        val user = username.text.toString()
        val pass = password.text.toString()
        if (user.isEmpty()) {
            username.error = "Enter a valid username"
            valid = false
        } else {
            username.error = null
        }
        if (pass.isEmpty()) {
            password.error = "Enter a valid password"
            valid = false
        } else {
            password.error = null
        }
        return valid
    }

}
