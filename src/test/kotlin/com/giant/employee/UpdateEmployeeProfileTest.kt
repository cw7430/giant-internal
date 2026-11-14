package com.giant.employee

import com.giant.common.api.type.ResponseCode
import com.giant.module.employee.dto.request.EmployeeProfileRequestDto
import com.giant.module.employee.type.EmployeeRole
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UpdateEmployeeProfileTest : EmployeeControllerTest() {

    @Test
    @DisplayName("직원 정보 수정 - 성공")
    fun success() {
        val accessToken = authTestUtil.getTestToken(HR_LOGIN_DATA).accessToken
        patch(URL)
            .key().auth(accessToken).body(DATA).send().andExpect(status().isNoContent())
    }

    @Test
    @DisplayName("직원 정보 수정 - 잘못 된 입력 값")
    fun failWithValidationError() {
        val accessToken = authTestUtil.getTestToken(HR_LOGIN_DATA).accessToken
        patch(URL)
            .key().auth(accessToken).body(INVALID_DATA).send().andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ResponseCode.VALIDATION_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 정보 수정 - 인증 오류")
    fun failWithUnauthorized() {
        patch(URL)
            .key().body(DATA).send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.UNAUTHORIZED.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 정보 수정 - 잘못 된 토큰")
    fun failWithInvalidToken() {
        patch(URL)
            .key().auth(INVALID_TOKEN).body(DATA).send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.INVALID_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 정보 수정 - 만료 된 토큰")
    fun failWithExpiredToken() {
        val accessToken = authTestUtil.generateExpiredAccessToken(HR_LOGIN_DATA)
        patch(URL)
            .key().auth(accessToken).body(DATA).send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.EXPIRED_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 정보 수정 - 권한 오류")
    fun failWithForbidden() {
        val accessToken = authTestUtil.getTestToken(SALARY_LOGIN_DATA).accessToken
        patch(URL)
            .key().auth(accessToken).body(DATA).send().andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.FORBIDDEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 정보 수정 - Api Key 오류")
    fun failWithKeyError() {
        val accessToken = authTestUtil.getTestToken(HR_LOGIN_DATA).accessToken
        patch(URL)
            .auth(accessToken).body(DATA).send().andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.KEY_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 등록 - 없는 리소스")
    fun failWithDuplicateResource() {
        val accessToken = authTestUtil.getTestToken(HR_LOGIN_DATA).accessToken
        patch(INVALID_URL)
            .key().auth(accessToken).body(DATA).send().andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(ResponseCode.RESOURCE_NOT_FOUND.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    companion object {
        private const val URL = "$EMPLOYEE_URL/profiles/1"
        private const val INVALID_URL = "$EMPLOYEE_URL/profiles/100000"

        private val DATA = EmployeeProfileRequestDto.Update(
            employeeName = "이대표",
            positionCode = "PSN10",
            teamCode = "TM100",
            employeeRole = EmployeeRole.DEPARTMENT_CHIEF
        )

        private val INVALID_DATA = EmployeeProfileRequestDto.Update(
            employeeName = " ",
            positionCode = "3423rg",
            teamCode = "4982748923r",
            employeeRole = EmployeeRole.DEPARTMENT_CHIEF
        )
    }
}