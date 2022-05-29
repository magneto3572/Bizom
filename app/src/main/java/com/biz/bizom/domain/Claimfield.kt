package com.biz.bizom.domain

data class Claimfield(
    val Claimfieldoption: ArrayList<Claimfieldoption>,
    val created: String,
    val id: String,
    val isdependant: String,
    val label: String,
    val modified: String,
    val name: String,
    val required: String,
    val type: String
)