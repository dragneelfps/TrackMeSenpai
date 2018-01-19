package com.projectk.partners.clientapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.projectk.partners.clientapp.utils.SignUpIntentService
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.content_sign_up.*
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {

    companion object {
        private val TAG = "SignUpActivity"
        private val SIGNUP_REQUEST = 1
    }

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setSupportActionBar(toolbar)

        signup.setOnClickListener { signUp() }
    }

    private fun signUp() {
        if (!validate()) {
            signup.isEnabled = true
            return
        }
        signup.isEnabled = false
        progressDialog = ProgressDialog(this@SignUpActivity)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Registering...")
        progressDialog.show()

        val name = name.text.toString()
        val user = username.text.toString()
        val password = password.text.toString()
        val email = email.text.toString()

        val pendingResult = createPendingResult(SIGNUP_REQUEST, Intent(), 0)
        val intent = Intent(applicationContext, SignUpIntentService::class.java)
        intent.putExtra("name", name)
        intent.putExtra("user", user)
        intent.putExtra("password", password)
        intent.putExtra("email", email)
        intent.putExtra(SignUpIntentService.PENDING_RESULT_EXTRA, pendingResult)
        startService(intent)
        Log.d(TAG, "Started SignUp service")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGNUP_REQUEST) {
            when (resultCode) {
                SignUpIntentService.RESULT_CODE -> {
                    Log.d(TAG, "Got result from signupservice")
                    progressDialog.dismiss()
                    handleSignUpResponse(data)
                }
            }
        }
    }

    private fun handleSignUpResponse(data: Intent?) {
        if (data == null) {
            Log.d(TAG, "No result intent was sent")
            onSignUpFailed("No result intent was sent", null)
            return
        }
        val resultCode = data.getIntExtra(SignUpIntentService.RESPONSE_CODE, -1)
        when (resultCode) {
            -1 -> {
                Log.d(TAG, "No response code was sent")
                onSignUpFailed("No response code was sent", null)
            }
            SignUpIntentService.CREATED -> {
                Log.d(TAG, "Account was created")
                Toast.makeText(baseContext, "Account was created", Toast.LENGTH_LONG).show()
                onSignUpSuccess()
            }
            SignUpIntentService.VALIDATION_ERROR -> {
                Log.d(TAG, "Validation error occured")
                var errorObj = JSONObject(data.getStringExtra(SignUpIntentService.ERRORS))
                Log.d(TAG, errorObj.toString())
                onSignUpFailed("Validation error occured", errorObj)
            }
        }
    }

    private fun onSignUpSuccess() {
        name.error = null
        username.error = null
        password.error = null
        email.error = null
        setResult(MainActivity.RESULT_OK)
        finish()
    }

    private fun onSignUpFailed(info: String, errorObj: JSONObject?) {
        if (errorObj != null) {
            val errors = errorObj.getJSONObject("errors")
            if (errors.has("name")) {
                name.error = errors.getJSONObject("name").getString("message")
            } else {
                name.error = null
            }
            if (errors.has("username")) {
                username.error = errors.getJSONObject("username").getString("message")
            } else {
                username.error = null
            }
            if (errors.has("email")) {
                email.error = errors.getJSONObject("email").getString("message")
            } else {
                email.error = null
            }
            if (errors.has("password")) {
                password.error = errors.getJSONObject("password").getString("message")
            } else {
                password.error = null
            }
        }
        Toast.makeText(baseContext, info, Toast.LENGTH_LONG).show()
        signup.isEnabled = true
    }

    private fun validate(): Boolean {
        var valid = true
        val user = username.text.toString()
        val pass = password.text.toString()
        val Name = name.text.toString()
        val Email = email.text.toString()
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
        if (Name.isEmpty()) {
            name.error = "Enter a valid name"
            valid = false
        } else {
            name.error = null
        }
        if (Email.isEmpty()) {
            email.error = "Enter a valid password"
            valid = false
        } else {
            email.error = null
        }
        return valid
    }

    override fun onBackPressed() {
        setResult(MainActivity.RESULT_BACK_PRESSED)
        super.onBackPressed()
    }

}
