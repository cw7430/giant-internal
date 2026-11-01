package com.giant.module.auth.dto.response

import com.giant.module.auth.type.AuthRole
import com.giant.module.employee.type.EmployeeRole
import io.swagger.v3.oas.annotations.media.Schema

interface AuthResponseDto {
    @Schema(name = "LoginResponse")
    data class Login(
        @get:Schema(description = "Access token")
        val accessToken: String,

        @get:Schema(description = "Access token 만료시간", example = "1773042557262")
        val accessTokenExpiresAtMs: Long,

        @get:Schema(description = "Refresh token")
        val refreshToken: String,

        @get:Schema(description = "Refresh token 만료시간", example = "1773042557262")
        val refreshTokenExpiresAtMs: Long,

        @get:Schema(description = "장기 갱신", example = "false")
        val isAuto: Boolean,

        @get:Schema(description = "사번", example = "EMP001")
        val employeeCode:String,

        @get:Schema(description = "사원 이름", example = "이사장")
        val employeeName:String,

        @get:Schema(description = "계정 권한", example = "USER")
        val authRole: AuthRole,

        @get:Schema(description = "직책 권한", example = "EMPLOYEE")
        val employeeRole: EmployeeRole,

        @get:Schema(description = "부서 코드", example = "DPT100")
        val department:String,

        @get:Schema(description = "팀 코드", example = "TM100")
        val team:String,

        @get:Schema(description = "직급 코드", example = "PSN10")
        val position:String
    ): AuthResponseDto
}