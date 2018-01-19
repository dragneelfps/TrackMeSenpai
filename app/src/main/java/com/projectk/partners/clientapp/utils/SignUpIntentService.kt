package com.projectk.partners.clientapp.utils

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import okhttp3.*
import org.json.JSONObject

class SignUpIntentService : IntentService(TAG) {
    companion object {
        val TAG = "SignUpIntentService"
        val PENDING_RESULT_EXTRA = "pending_result"
        val RESPONSE_CODE = "response"
        val RESULT_CODE = 0
        val NULL_RESPONSE = 1
        val NO_INTERNET_ACCESS = 3
        val SERVER_NOT_FOUND = 4
        val CREATED = 5
        val VALIDATION_ERROR = 6
        val ERRORS = "errors"
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(SignUpIntentService.TAG, "SignUp Intent Service started")
        if (intent == null) {
            Log.d(SignUpIntentService.TAG, "Null intent was seen")
            return
        }

        val name = intent.getStringExtra("name")
        val user = intent.getStringExtra("user")
        val pass = intent.getStringExtra("password")
        val email = intent.getStringExtra("email")
        val reply = intent.getParcelableExtra<PendingIntent>(SignUpIntentService.PENDING_RESULT_EXTRA)
        val result = Intent()

        val json = JSONObject()
        json.put("username", user)
        json.put("password", pass)
        json.put("name", name)
        json.put("email", email)

        val client = OkHttpClient()
        val url = HttpUrl.Builder()
                .scheme(scheme)
                .host(baseUrl)
                .addEncodedPathSegments(registerClientUrl)
                .build()
        val request = Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON, json.toString()))
                .build()
        var response: Response?
        try {
            response = client.newCall(request).execute()
            if (response == null) {
                result.putExtra(RESPONSE_CODE, NULL_RESPONSE)
            } else if (response.code() == 201) {
                result.putExtra(RESPONSE_CODE, CREATED)
            } else if (response.code() == 404) {
                result.putExtra(RESPONSE_CODE, SERVER_NOT_FOUND)
            } else if (response.code() == 400) {
                result.putExtra(RESPONSE_CODE, VALIDATION_ERROR)
                result.putExtra(ERRORS, response.body()!!.string())
            }
        } catch (exc: Exception) {
            result.putExtra(SignUpIntentService.RESPONSE_CODE, SignUpIntentService.NO_INTERNET_ACCESS)
        } finally {
            reply.send(this, SignUpIntentService.RESULT_CODE, result)
        }


    }
}