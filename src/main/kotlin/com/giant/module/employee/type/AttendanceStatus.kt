package com.giant.module.employee.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.giant.common.api.exception.CustomException
import com.giant.common.api.type.ResponseCode
import java.util.*

enum class AttendanceStatus {
    PRESENT,
    LATE,
    ABSENT,
    ANNUAL_LEAVE,
    HALF_DAY_AM,
    HALF_DAY_PM;

    @get:JsonValue
    val value: String
        get() = name

    companion object {
        @JsonCreator
        fun from(value: String?): AttendanceStatus? {
            if (value.isNullOrBlank()) {
                return null
            }
            try {
                return valueOf(value.uppercase(Locale.getDefault()))
            } catch (_: IllegalArgumentException) {
                throw CustomException(
                    ResponseCode.VALIDATION_ERROR,
                    "employeeRole",
                    "입력 가능값: PRESENT, LATE, ABSENT, ANNUAL_LEAVE, HALF_DAY_AM, HALF_DAY_PM, 입력된 값: $value"
                )
            }
        }
    }
}