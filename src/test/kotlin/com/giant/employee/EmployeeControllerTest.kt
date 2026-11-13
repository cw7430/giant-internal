package com.giant.employee

import com.giant.BaseIntegrationTest
import com.giant.module.auth.dto.request.AuthRequestDto
import com.giant.module.employee.EmployeeService
import org.springframework.beans.factory.annotation.Autowired

class EmployeeControllerTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var employeeService: EmployeeService

    protected fun getEmployeeCode(): String =
        employeeService.getEmployeeCode().employeeCode

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