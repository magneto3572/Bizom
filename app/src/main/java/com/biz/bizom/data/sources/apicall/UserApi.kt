package com.biz.bizom.data.sources.apicall

import com.biz.bizom.domain.DataResponse
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface UserApi {

    @GET("b/9KBG")
    suspend fun fetchdata(): DataResponse

}