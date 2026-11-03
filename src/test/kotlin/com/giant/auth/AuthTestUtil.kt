package com.giant.auth

import com.giant.common.api.exception.CustomException
import com.giant.common.api.type.ResponseCode
import com.giant.module.auth.AuthService
import com.giant.module.auth.dto.request.AuthRequestDto
import com.giant.module.auth.dto.response.AuthResponseDto
import com.giant.module.auth.dto.vo.AccountVo
import com.giant.module.auth.repository.AuthJooqRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestComponent
import java.util.*

@TestComponent
class AuthTestUtil {
    @Autowired
    private lateinit var authJooqRepository: AuthJooqRepository

    @Autowired
    private lateinit var authService: AuthService

    @Value($$"${security.api-key}")
    private lateinit var apiKey: String

    @Value($$"${jwt.access.secret}")
    private lateinit var accessSecretKey: String

    @Value($$"${jwt.refresh.secret}")
    private lateinit var refreshSecretKey: String

    private fun makeExpiredLoginData(reqDto: AuthRequestDto.Login): Triple<AccountVo, Date, Date> {
        val loginData = authJooqRepository.findAccountByUserName(reqDto.userName)
            ?: throw CustomException(ResponseCode.LOGIN_ERROR)
        val now = Date()
        val expiry = Date(now.time - 1)
        return Triple(loginData, now, expiry)
    }

    fun getTestToken(reqDto: AuthRequestDto.Login): AuthResponseDto.Login =
        authService.login(reqDto)

    fun getTestApiKey(): String = apiKey

    fun generateExpiredAccessToken(reqDto: AuthRequestDto.Login): String {
        val data = makeExpiredLoginData(reqDto)

        return Jwts.builder()
            .subject(data.first.accountId.toString())
            .claim("role", data.first.authRole.toString())
            .issuedAt(data.second)
            .expiration(data.third)
            .signWith(
                Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecretKey)),
                Jwts.SIG.HS256
            )
            .compact()
    }

    fun generateExpiredRefreshToken(reqDto: AuthRequestDto.Login): String {
        val data = makeExpiredLoginData(reqDto)

        return Jwts.builder()
            .subject(data.first.accountId.toString())
            .issuedAt(data.second)
            .expiration(data.third)
            .signWith(
                Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecretKey)),
                Jwts.SIG.HS256
            )
            .compact()
    }
}