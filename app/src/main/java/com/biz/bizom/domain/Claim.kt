package com.biz.bizom.domain

data class Claim(
    val Claimtype: Claimtype,
    val Claimtypedetail: ArrayList<Claimtypedetail>
)