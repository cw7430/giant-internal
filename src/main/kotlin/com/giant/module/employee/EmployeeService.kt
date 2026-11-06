package com.giant.module.employee

import com.giant.common.api.response.PageResponse
import com.giant.common.config.security.JwtUtil
import com.giant.module.auth.repository.AuthJooqRepository
import com.giant.module.employee.dto.request.EmployeeProfileRequestDto
import com.giant.module.employee.dto.response.EmployeeProfileResponseDto
import com.giant.module.employee.repository.EmployeeJooqRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class EmployeeService(
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder,
    private val authJooqRepository: AuthJooqRepository,
    private val employeeJooqRepository: EmployeeJooqRepository
) {
    fun getEmployeeProfileList(reqDto: EmployeeProfileRequestDto.ListData):
            EmployeeProfileResponseDto.ListData {
        val accountId = jwtUtil.getCurrentUserId()
        val employeeProfiles = employeeJooqRepository.findEmployeeProfileList(reqDto)
        val departments = employeeJooqRepository.findDepartmentList()
        val positions = employeeJooqRepository.findPositionList()
        val totalElements = employeeJooqRepository.countEmployeeProfileList().toLong()

        log.info { "Employee Profiles requested by account ID:${accountId}" }

        return EmployeeProfileResponseDto.ListData(
            employeeProfiles = PageResponse.of(
                contents = employeeProfiles,
                pageRequest = reqDto,
                totalElements
            ),
            departments,
            positions,
            allowedProfileTeams = ALLOWED_PROFILE_TEAMS
        )
    }

    companion object {
        private val ALLOWED_PROFILE_TEAMS = setOf("TM100", "TM200")
    }
}