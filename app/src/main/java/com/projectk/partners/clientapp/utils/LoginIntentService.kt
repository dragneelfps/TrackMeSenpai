package com.projectk.partners.clientapp.utils

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import okhttp3.*
import org.json.JSONObject

/**
 * Created by sourabh on 1/14/2018.
 */
class LoginIntentService : IntentService(TAG) {
    companion object {
        val TAG = "LoginIntentService"
        val PENDING_RESULT_EXTRA = "pending_result"

        val RESULT_CODE = 0
        val RESPONSE_CODE = "response"
        val NULL_RESPONSE = 1
        val INCORRECT_CRED_RESPONSE = 2
        val CORRECT_CRED_RESPONSE = 3
        val ERROR_RESPONSE = 4
        val NO_INTERNET_ACCESS = 5
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "HERE at least")
        if (intent == null) {
            Log.d(TAG, "Null intent was seen")
            return
        }
        val user = intent.getStringExtra("user")
        val pass = intent.getStringExtra("pass")
        val reply = intent.getParcelableExtra<PendingIntent>(PENDING_RESULT_EXTRA)
        val result = Intent()

        val json = JSONObject()
        json.put("username", user)
        json.put("password", pass)
        val client = OkHttpClient()
        val url = HttpUrl.Builder()
                .scheme(scheme)
                .host(baseUrl)
                .addEncodedPathSegments(loginClientUrl)
                .build()
        val request = Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON, json.toString()))
                .build()
        var response: Response? = null
        try {
            response = client.newCall(request).execute()
            if (response == null) {
                result.putExtra(RESPONSE_CODE, NULL_RESPONSE)
            } else if (response.code() == 200) {
                result.putExtra(RESPONSE_CODE, CORRECT_CRED_RESPONSE)
            } else if (response.code() == 404) {
                result.putExtra(RESPONSE_CODE, INCORRECT_CRED_RESPONSE)
            } else {
                result.putExtra(RESPONSE_CODE, ERROR_RESPONSE)
            }
        } catch (exc: Exception) {
            result.putExtra(RESPONSE_CODE, NO_INTERNET_ACCESS)
        } finally {
            reply.send(this, RESULT_CODE, result)
        }
    }
}