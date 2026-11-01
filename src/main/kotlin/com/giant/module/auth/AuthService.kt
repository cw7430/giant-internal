package com.giant.module.auth

import com.giant.common.api.exception.CustomException
import com.giant.common.api.type.ResponseCode
import com.giant.common.config.security.JwtProvider
import com.giant.common.config.security.JwtUtil
import com.giant.module.auth.dto.request.AuthRequestDto
import com.giant.module.auth.dto.response.AuthResponseDto
import com.giant.module.auth.dto.vo.AccountVo
import com.giant.module.auth.repository.AuthJooqRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

private val log = KotlinLogging.logger {}

@Service
class AuthService(
    private val jwtProvider: JwtProvider,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder,
    private val authJooqRepository: AuthJooqRepository
) {
    private fun issueTokensAndBuild(account: AccountVo, isAuto: Boolean): AuthResponseDto.Login {
        val accessClaims = jwtProvider.generateAccessToken(
            account.accountId.toString(), account.authRole.toString()
        )
        val refreshClaims = jwtProvider.generateRefreshToken(
            account.accountId.toString(), isAuto
        )
        val refreshTokenExpiresAtMs = refreshClaims.expiresAtMs
        val refreshTokenExpiresAtDate = Instant.ofEpochMilli(refreshTokenExpiresAtMs)

        authJooqRepository.createRefreshToken(
            account.accountId,
            refreshClaims.token,
            refreshTokenExpiresAtDate
        )

        return AuthResponseDto.Login(
            accessToken = accessClaims.token,
            accessTokenExpiresAtMs = accessClaims.expiresAtMs,
            refreshToken = refreshClaims.token,
            refreshTokenExpiresAtMs = refreshTokenExpiresAtMs,
            isAuto = isAuto,
            employeeCode = account.employeeCode,
            employeeName = account.employeeName,
            authRole = account.authRole,
            employeeRole = account.employeeRole,
            department = account.departmentCode,
            team = account.teamCode,
            position = account.positionCode
        )
    }

    @Transactional
    fun login(reqDto: AuthRequestDto.Login): AuthResponseDto.Login {
        val account = authJooqRepository.findAccountByUserName(reqDto.userName)
            ?: throw CustomException(ResponseCode.LOGIN_ERROR)

        if (!passwordEncoder.matches(reqDto.password, account.passwordHash)) {
            throw CustomException(ResponseCode.LOGIN_ERROR)
        }

        log.info { "Login In successfully for account ID:${account.accountId}" }

        return issueTokensAndBuild(account, reqDto.isAuto)
    }
}