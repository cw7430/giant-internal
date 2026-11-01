package com.giant.module.auth.dto.vo

import com.giant.module.auth.type.AuthRole
import com.giant.module.employee.type.EmployeeRole

sealed interface AccountVo {
    val accountId: Long
    val authRole: AuthRole
    val employeeCode: String
    val employeeName: String
    val employeeRole: EmployeeRole
    val departmentCode: String
    val teamCode: String
    val positionCode: String

    data class Refresh(
        override val accountId: Long,
        override val authRole: AuthRole,
        override val employeeCode: String,
        override val employeeName: String,
        override val employeeRole: EmployeeRole,
        override val departmentCode: String,
        override val teamCode: String,
        override val positionCode: String
    ) : AccountVo

    data class Login(
        override val accountId: Long,
        val passwordHash: String,
        override val authRole: AuthRole,
        override val employeeCode: String,
        override val employeeName: String,
        override val employeeRole: EmployeeRole,
        override val departmentCode: String,
        override val teamCode: String,
        override val positionCode: String
    ) : AccountVo
}