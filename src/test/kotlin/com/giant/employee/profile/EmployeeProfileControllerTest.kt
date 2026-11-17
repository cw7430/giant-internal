package com.giant.employee.profile

import com.giant.employee.EmployeeControllerTest

class EmployeeProfileControllerTest : EmployeeControllerTest() {
    companion object {
        protected const val PROFILE_URL = "$EMPLOYEE_URL/profiles"
    }
}