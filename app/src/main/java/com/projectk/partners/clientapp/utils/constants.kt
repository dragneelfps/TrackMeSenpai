package com.projectk.partners.clientapp.utils

import okhttp3.MediaType

/**
 * Created by sourabh on 1/14/2018.
 */


val scheme = "http"
//Use ngrok to tunnel the registration api server and update this string
val baseUrl = "271cbb94.ngrok.io"


val loginClientUrl = "api/client/login"
val registerClientUrl = "api/client"
val adminIdUrl = "api/admin/id"
val linkAdminUrl = "api/client/idcode"


val JSON = MediaType.parse("application/json; charset=utf-8")