package com.giant.common.api.response

data class ValidationError(
    val field: String,
    val message: String
)