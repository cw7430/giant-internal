package com.giant.module.employee.profile.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.giant.common.api.exception.CustomException
import com.giant.common.api.type.ResponseCode
import java.util.*

enum class EmployeeRole {
    DEPARTMENT_CHIEF, TEAM_CHIEF, EMPLOYEE, LEFT;

    @get:JsonValue
    val value: String
        get() = name

    companion object {
        @JsonCreator
        fun from(value: String?): EmployeeRole? {
            if (value.isNullOrBlank()) {
                return null
            }
            try {
                return valueOf(value.uppercase(Locale.getDefault()))
            } catch (_: IllegalArgumentException) {
                throw CustomException(
                    ResponseCode.VALIDATION_ERROR,
                    "employeeRole",
                    "유효하지 않은 직원 역할, 입력된 값: $value"
                )
            }
        }
    }
}