package com.biz.bizom.data.Repository


import com.biz.bizom.data.sources.apicall.SafeApiCall
import com.biz.bizom.data.sources.apicall.UserApi
import javax.inject.Inject

class HomeRepository @Inject constructor (private val api: UserApi): SafeApiCall {

    suspend fun fetchCustomUi() = safeApiCall {
        api.fetchdata()
    }

}