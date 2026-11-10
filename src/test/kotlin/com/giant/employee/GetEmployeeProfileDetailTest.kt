package com.giant.employee

import com.giant.common.api.type.ResponseCode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GetEmployeeProfileDetailTest : EmployeeControllerTest() {

    @Test
    @DisplayName("직원 상세 조회 - 성공")
    fun success() {
        val accessToken = authTestUtil.getTestToken(HR_LOGIN_DATA).accessToken
        get(URL)
            .key().auth(accessToken).send().andExpect(status().isOk())
    }

    @Test
    @DisplayName("직원 상세 조회 - 인증 오류")
    fun failWithUnauthorized() {
        get(URL)
            .key().send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.UNAUTHORIZED.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 상세 조회 - 잘못 된 토큰")
    fun failWithInvalidToken() {
        get(URL).key()
            .auth(INVALID_TOKEN)
            .send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.INVALID_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 상세 조회 - 만료 된 토큰")
    fun failWithExpiredToken() {
        val accessToken = authTestUtil.generateExpiredAccessToken(HR_LOGIN_DATA)
        get(URL)
            .key().auth(accessToken).send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.EXPIRED_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 상세 조회 - Api Key 오류")
    fun failWithKeyError() {
        val accessToken = authTestUtil.getTestToken(HR_LOGIN_DATA).accessToken
        get(URL)
            .auth(accessToken).send().andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.KEY_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 상세 조회 - 없는 리소스")
    fun failWithResourceNotFound() {
        val accessToken = authTestUtil.getTestToken(HR_LOGIN_DATA).accessToken
        get(INVALID_URL)
            .key().auth(accessToken).send().andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(ResponseCode.RESOURCE_NOT_FOUND.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    companion object {
        private const val URL = "$EMPLOYEE_URL/profiles/1"
        private const val INVALID_URL = "$EMPLOYEE_URL/profiles/2"
    }
}