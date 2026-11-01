package com.giant.module.auth

import com.giant.common.api.doc.ErrorResponseDoc
import com.giant.module.auth.dto.request.AuthRequestDto
import com.giant.module.auth.dto.response.AuthResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth Controller", description = "계정 API")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    @Operation(summary = "로그인")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "로그인 성공", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = AuthResponseDto.Login::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "400", description = "입력값 오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.BadRequest::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "401", description = "잘못 된 계정정보", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.LoginError::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "403", description = "Api Key 오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.KeyError::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "500", description = "기타오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.InternalServerError::class)
                )
            ]
        )
    )
    fun login(@RequestBody @Valid reqDto: AuthRequestDto.Login): ResponseEntity<AuthResponseDto.Login> =
        ResponseEntity.ok(authService.login(reqDto))

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    @ApiResponses(
        ApiResponse(
            responseCode = "204", description = "로그아웃 성공"
        ),
        ApiResponse(
            responseCode = "500", description = "기타오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.InternalServerError::class)
                )
            ]
        )
    )
    fun logout(@RequestBody reqDto: AuthRequestDto.Logout): ResponseEntity<Void> {
        authService.logout(reqDto)
        return ResponseEntity.noContent().build()
    }
}