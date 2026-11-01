package com.giant.module.auth.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.giant.common.api.exception.CustomException
import com.giant.common.api.type.ResponseCode
import java.util.Locale

enum class AuthRole {
    USER, ADMIN, LEFT;

    @get:JsonValue
    val value: String
        get() = name

    companion object {
        @JsonCreator
        fun from(value: String?): AuthRole? {
            if (value.isNullOrBlank()) {
                return null
            }
            try {
                return valueOf(value.uppercase(Locale.getDefault()))
            } catch (_: IllegalArgumentException) {
                throw CustomException(
                    ResponseCode.VALIDATION_ERROR,
                    "authRole",
                    "입력 가능값: USER, ADMIN, LEFT, 입력된 값: $value"
                )
            }
        }
    }
}