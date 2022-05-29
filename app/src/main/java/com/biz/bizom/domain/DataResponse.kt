package com.biz.bizom.domain

import com.biz.bizom.domain.Claim

data class DataResponse(
    val Claims: ArrayList<Claim>,
    val Reason: String,
    val Result: Boolean
)