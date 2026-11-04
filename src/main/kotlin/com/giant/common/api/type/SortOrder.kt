package com.giant.common.api.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.giant.common.api.exception.CustomException
import java.util.*

enum class SortOrder {
    ASC, DESC;

    @get:JsonValue
    val value: String
        get() = name

    companion object {
        @JsonCreator
        fun from(value: String?): SortOrder? {
            if (value.isNullOrBlank()) {
                return null
            }

            try {
                return SortOrder.valueOf(value.uppercase(Locale.getDefault()))
            } catch (_: IllegalArgumentException) {
                throw CustomException(
                    ResponseCode.VALIDATION_ERROR,
                    "sortOrder",
                    "입력 가능값: ASC, DESC, 입력된 값: $value"
                )
            }
        }
    }
}