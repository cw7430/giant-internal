package com.giant.employee

import com.giant.BaseIntegrationTest
import com.giant.module.auth.dto.request.AuthRequestDto

class EmployeeControllerTest : BaseIntegrationTest() {
    companion object {
        protected const val EMPLOYEE_URL = "$BASE_URL/hr"

        @JvmStatic
        protected val HR_LOGIN_DATA = AuthRequestDto.Login(
            "EMP004",
            "EMP004",
            false
        )

        @JvmStatic
        protected val SALARY_LOGIN_DATA = AuthRequestDto.Login(
            "EMP030",
            "EMP030",
            false
        )
    }
}