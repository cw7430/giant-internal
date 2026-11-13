package com.giant.employee

import com.giant.common.api.type.ResponseCode
import com.giant.module.employee.dto.request.EmployeeProfileRequestDto
import com.giant.module.employee.type.EmployeeRole
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CreateEmployeeProfileTest : EmployeeControllerTest() {

    private fun getData(): EmployeeProfileRequestDto.Create {
        val employeeCode = getEmployeeCode()

        return EmployeeProfileRequestDto.Create(
            employeeCode = employeeCode,
            employeeName = "테스트",
            phone = "010-0000-0000",
            email = "email@email.com",
            positionCode = "PSN80",
            teamCode = "TM601",
            employeeRole = EmployeeRole.EMPLOYEE
        )
    }


    @Test
    @DisplayName("직원 등록 - 성공")
    fun success() {
        val accessToken = authTestUtil.getTestToken(HR_LOGIN_DATA).accessToken
        val data = getData()
        post(URL)
            .key().auth(accessToken).body(data).send().andExpect(status().isNoContent())
    }

    @Test
    @DisplayName("직원 등록 - 잘못 된 입력 값")
    fun failWithValidationError() {
        val accessToken = authTestUtil.getTestToken(HR_LOGIN_DATA).accessToken
        post(URL)
            .key().auth(accessToken).body(INVALID_DATA).send().andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(ResponseCode.VALIDATION_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 등록 - 인증 오류")
    fun failWithUnauthorized() {
        val data = getData()
        post(URL)
            .key().body(data).send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.UNAUTHORIZED.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 등록 - 잘못 된 토큰")
    fun failWithInvalidToken() {
        val data = getData()
        post(URL)
            .key().auth(INVALID_TOKEN).body(data).send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.INVALID_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 등록 - 만료 된 토큰")
    fun failWithExpiredToken() {
        val accessToken = authTestUtil.generateExpiredAccessToken(HR_LOGIN_DATA)
        val data = getData()
        post(URL)
            .key().auth(accessToken).body(data).send().andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ResponseCode.EXPIRED_TOKEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 등록 - 권한 오류")
    fun failWithForbidden() {
        val accessToken = authTestUtil.getTestToken(SALARY_LOGIN_DATA).accessToken
        val data = getData()
        post(URL)
            .key().auth(accessToken).body(data).send().andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.FORBIDDEN.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 등록 - Api Key 오류")
    fun failWithKeyError() {
        val accessToken = authTestUtil.getTestToken(HR_LOGIN_DATA).accessToken
        val data = getData()
        post(URL)
            .auth(accessToken).body(data).send().andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ResponseCode.KEY_ERROR.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    @Test
    @DisplayName("직원 등록 - 중복된 사번")
    fun failWithDuplicateResource() {
        val accessToken = authTestUtil.getTestToken(HR_LOGIN_DATA).accessToken
        post(URL)
            .key().auth(accessToken).body(DUPLICATED_DATA).send().andExpect(status().isConflict)
            .andExpect(jsonPath("$.code").value(ResponseCode.DUPLICATE_RESOURCE.code))
            .andExpect(jsonPath("$.message").isNotEmpty())
    }

    companion object {
        private const val URL = "$EMPLOYEE_URL/profiles"

        private val DUPLICATED_DATA =
            EmployeeProfileRequestDto.Create(
                employeeCode = "EMP001",
                employeeName = "테스트",
                phone = "010-0000-0000",
                email = "email@email.com",
                positionCode = "PSN80",
                teamCode = "TM601",
                employeeRole = EmployeeRole.EMPLOYEE
            )

        private val INVALID_DATA =
            EmployeeProfileRequestDto.Create(
                employeeCode = "46541",
                employeeName = " ",
                phone = "wad2132",
                email = "af21r",
                positionCode = "3423rg",
                teamCode = "4982748923r",
                employeeRole = EmployeeRole.EMPLOYEE
            )
    }
}