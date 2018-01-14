package com.projectk.partners.clientapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.projectk.partners.clientapp.utils.*

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.lang.ref.WeakReference

class LoginActivity : AppCompatActivity() {

    lateinit var progressDialog: ProgressDialog

    companion object {
        private val TAG = "LoginActivity"
        private val REQUEST_SIGNUP = 0
        private val LOGIN_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        login.setOnClickListener { login() }
        register.setOnClickListener {
            //            val intent = Intent(applicationContext,SignUpActivity::class.java)
//            startActivityForResult(intent, REQUEST_SIGNUP)
        }
    }

    private fun login() {
        Log.d(TAG, "Login")
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
        Log.d(TAG, "End")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LOGIN_REQUEST) {
            when (resultCode) {
                LoginIntentService.RESULT_CODE -> {
                    Log.d(TAG, "Got result from loginservice")
                    progressDialog.dismiss()
                    handlerAuthentication(data)
                }
            }
        }

        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                this.finish()
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
        }


    }

    override fun onBackPressed() {
        moveTaskToBack(true)
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
