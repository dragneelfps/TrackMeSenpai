package com.projectk.partners.clientapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "MainActivity"
        val REQUEST_LOGIN = 0
        val REQUEST_SIGNUP = 1
        val RESULT_OK = 2
        val RESULT_BACK_PRESSED = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        login_btn.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivityForResult(intent, REQUEST_LOGIN)
        }
        signup_btn.setOnClickListener {
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivityForResult(intent, REQUEST_SIGNUP)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGNUP) {
            when (resultCode) {
                RESULT_OK -> {
                    Log.d(TAG, "Successfully created account")
                    Toast.makeText(baseContext, "Successfully created account", Toast.LENGTH_SHORT).show()
                }
                RESULT_BACK_PRESSED -> {
                    Log.d(TAG, "Back pressed from Sign Up Activity")
                    Toast.makeText(baseContext, "Back pressed from Sign Up Activity", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == REQUEST_LOGIN) {
            when (resultCode) {
                RESULT_BACK_PRESSED -> {
                    Log.d(TAG, "Back pressed from Login Activity")
                    Toast.makeText(baseContext, "Back pressed from Login Activity", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
