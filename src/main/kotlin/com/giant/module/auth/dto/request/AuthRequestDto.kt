package com.giant.module.auth.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

interface AuthRequestDto {

    @Schema(name = "LoginRequest")
    data class Login(
        @field:NotBlank(message = "아이디를 입력해주세요.")
        @get:Schema(description = "아이디", example = "exampleid1234")
        val userName: String,

        @field:NotBlank(message = "비밀번호를 입력해주세요.")
        @get:Schema(description = "비빌번호", example = "examplepw1234!@")
        val password: String,

        @get:Schema(
            name = "isAuto",
            description = "장기 로그인",
            example = "false"
        )
        val isAuto: Boolean
    ): AuthRequestDto

    @Schema(name = "LogoutRequest")
    data class Logout(
        @get:Schema(description = "Refresh token")
        val refreshToken: String?
    ): AuthRequestDto

    @Schema(name = "RefreshRequest")
    data class Refresh(
        @get:Schema(
            name = "isAuto",
            description = "장기 로그인",
            example = "false"
        )
        val isAuto: Boolean
    ): AuthRequestDto
}