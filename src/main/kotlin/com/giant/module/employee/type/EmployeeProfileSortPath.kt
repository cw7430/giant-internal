package com.giant.module.employee.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.giant.common.api.exception.CustomException
import com.giant.common.api.type.ResponseCode
import java.util.*

enum class EmployeeProfileSortPath {
    EMPLOYEE, POSITION, DEPARTMENT;

    @get:JsonValue
    val value: String
        get() = name

    companion object {
        @JsonCreator
        fun from(value: String?): EmployeeProfileSortPath? {
            if (value.isNullOrBlank()) {
                return null
            }

            try {
                return EmployeeProfileSortPath.valueOf(value.uppercase(Locale.getDefault()))
            } catch (_: IllegalArgumentException) {
                throw CustomException(
                    ResponseCode.VALIDATION_ERROR,
                    "employeeProfileSortPath",
                    "입력 가능값: EMPLOYEE, POSITION, DEPARTMENT, 입력된 값: $value"
                )
            }
        }
    }
}