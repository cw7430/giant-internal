package com.giant.common.config.security.vo

data class TokenResponseClaim(
    val token: String,
    val expiresAtMs: Long
)
