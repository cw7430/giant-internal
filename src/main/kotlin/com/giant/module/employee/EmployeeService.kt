package com.giant.module.employee

import com.giant.common.api.exception.CustomException
import com.giant.common.api.response.PageResponse
import com.giant.common.api.type.ResponseCode
import com.giant.common.config.security.JwtUtil
import com.giant.module.auth.repository.AuthJooqRepository
import com.giant.module.employee.dto.request.EmployeeProfileRequestDto
import com.giant.module.employee.dto.response.EmployeeProfileResponseDto
import com.giant.module.employee.repository.EmployeeJooqRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class EmployeeService(
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder,
    private val authJooqRepository: AuthJooqRepository,
    private val employeeJooqRepository: EmployeeJooqRepository
) {
    private fun checkProfilePermission(employeeId: Long) {
        val teamCode = employeeJooqRepository.findTeamCodeByEmployeeId(employeeId)
            ?: throw CustomException(ResponseCode.UNAUTHORIZED)

        if (!ALLOWED_PROFILE_TEAMS.contains(teamCode)) {
            throw CustomException(ResponseCode.FORBIDDEN)
        }
    }

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

    fun getEmployeeProfileDetail(employeeId: Long): EmployeeProfileResponseDto.Detail {
        val accountId = jwtUtil.getCurrentUserId()
        val employeeProfile = employeeJooqRepository.findEmployeeProfileByEmployeeId(employeeId)
            ?: throw CustomException(ResponseCode.RESOURCE_NOT_FOUND)
        val departments = employeeJooqRepository.findDepartmentList()
        val positions = employeeJooqRepository.findPositionList()

        log.info { "Employee Profile requested by account ID:${accountId}" }

        return EmployeeProfileResponseDto.Detail.of(
            employeeProfile, departments, positions,
            allowedProfileTeams = ALLOWED_PROFILE_TEAMS
        )
    }

    fun getEmployeeCode(): EmployeeProfileResponseDto.EmployeeCode {
        val accountId = jwtUtil.getCurrentUserId()
        val seq = employeeJooqRepository.findEmployeeCodeSeqNextVal()
            ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR)

        log.info { "Employee Code requested by account ID:${accountId}" }

        return EmployeeProfileResponseDto.EmployeeCode(
            employeeCode = "EMP-${seq.toString().padStart(3, '0')}"
        )
    }

    @Transactional
    fun createEmployeeProfile(reqDto: EmployeeProfileRequestDto.Create) {
        val accountId = jwtUtil.getCurrentUserId()

        checkProfilePermission(accountId)

        if (employeeJooqRepository.existEmployeeProfileByEmployeeCode(reqDto.employeeCode)) {
            throw CustomException(ResponseCode.DUPLICATE_RESOURCE)
        }

        val passwordHash = passwordEncoder.encode(reqDto.employeeCode)
            ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR)

        val account = authJooqRepository.createAccountAndGet(
            userName = reqDto.employeeCode,
            passwordHash,
            phone = reqDto.phone,
            email = reqDto.email
        ) ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR)

        val createdEmployeeId = account.id
            ?: throw CustomException(ResponseCode.INTERNAL_SERVER_ERROR)

        employeeJooqRepository.createProfile(
            employeeId = createdEmployeeId,
            employeeCode = reqDto.employeeCode,
            employeeName = reqDto.employeeName,
            teamCode = reqDto.teamCode,
            positionCode = reqDto.positionCode,
            employeeRole = reqDto.employeeRole,
            createdEmployeeId = accountId
        )

        log.info { "Create Employee requested by account ID:${accountId}, created employee ID:${createdEmployeeId}" }
    }

    @Transactional
    fun updateEmployeeProfile(employeeId: Long, reqDto: EmployeeProfileRequestDto.Update) {
        val accountId = jwtUtil.getCurrentUserId()

        checkProfilePermission(accountId)

        if (!employeeJooqRepository.existActiveEmployeeProfileByEmployeeId(employeeId)) {
            throw CustomException(ResponseCode.RESOURCE_NOT_FOUND)
        }

        employeeJooqRepository.updateProfile(
            employeeId,
            employeeName = reqDto.employeeName,
            teamCode = reqDto.teamCode,
            positionCode = reqDto.positionCode,
            employeeRole = reqDto.employeeRole,
            updatedEmployeeId = accountId
        )

        log.info { "Update Employee requested by account ID:${accountId}, updated employee ID:${employeeId}" }
    }

    companion object {
        private val ALLOWED_PROFILE_TEAMS = setOf("TM100", "TM200")
    }
}