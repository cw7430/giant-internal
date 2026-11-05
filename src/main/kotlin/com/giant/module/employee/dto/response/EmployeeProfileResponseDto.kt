package com.giant.module.employee.dto.response

import com.giant.common.api.response.PageResponse
import com.giant.module.employee.type.EmployeeRole
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer
import java.time.Instant

interface EmployeeProfileResponseDto {
    @Schema(name = "EmployeeProfileResponse")
    open class Profile(
        @get:Schema(description = "일련번호", example = "1", type = "string")
        @get:JsonSerialize(using = ToStringSerializer::class)
        open val employeeId: Long,

        @get:Schema(description = "사번", example = "EMP001")
        open val employeeCode: String,

        @get:Schema(description = "직책 권한", example = "EMPLOYEE")
        open val employeeRole: EmployeeRole,

        @get:Schema(description = "사원 이름", example = "이사장")
        open val employeeName: String,

        @get:Schema(description = "직급 코드", example = "PSN10")
        open val positionCode: String,

        @get:Schema(description = "직급 이름", example = "대표")
        open val positionName: String,

        @get:Schema(description = "부서 코드", example = "DPT100")
        open val departmentCode: String,

        @get:Schema(description = "부서 이름", example = "경영지원부")
        open val departmentName: String,

        @get:Schema(description = "팀 코드", example = "TM100")
        open val teamCode: String,

        @get:Schema(description = "팀 이름", example = "경영팀")
        open val teamName: String,

        @get:Schema(description = "휴대전화 번호", example = "010-1234-5678")
        open val phone: String,

        @get:Schema(description = "이메일", example = "example@example.com")
        open val email: String,

        @get:Schema(description = "작성자 일련번호", example = "1", type = "string", nullable = true)
        @get:JsonSerialize(using = ToStringSerializer::class)
        open val createdBy: Long,

        @get:Schema(description = "작성자 이름", example = "이사장", nullable = true)
        open val createdEmployeeName: String,

        @get:Schema(description = "수정자 일련번호", example = "1", type = "string", nullable = true)
        @get:JsonSerialize(using = ToStringSerializer::class)
        open val updatedBy: Long,

        @get:Schema(description = "수정자 이름", example = "이사장", nullable = true)
        open val updatedEmployeeName: String,

        @get:Schema(description = "입사일", example = "2006-03-02T12:30:44.461Z")
        open val createdAt: Instant,

        @get:Schema(description = "수정일", example = "2006-03-02T12:30:44.461Z")
        open val updatedAt: Instant,

        @get:Schema(description = "퇴사일", example = "null", nullable = true)
        open val deletedAt: Instant
    )

    @Schema(name = "TeamResponse")
    data class Team(
        @get:Schema(description = "일련번호", example = "1", type = "string")
        @get:JsonSerialize(using = ToStringSerializer::class)
        val teamId: Long,

        @get:Schema(description = "팀 코드", example = "TM100")
        val teamCode: String,

        @get:Schema(description = "팀 이름", example = "경영팀")
        val teamName: String
    )

    @Schema(name = "DepartmentResponse")
    data class Department(
        @get:Schema(description = "일련번호", example = "1", type = "string")
        @get:JsonSerialize(using = ToStringSerializer::class)
        val departmentId: Long,

        @get:Schema(description = "부서 코드", example = "DPT100")
        val departmentCode: String,

        @get:Schema(description = "부서 이름", example = "경영지원부")
        val departmentName: String,

        @get:ArraySchema(schema = Schema(implementation = Team::class))
        val teams: List<Team>
    )

    @Schema(name = "PositionResponse")
    data class Position(
        @get:Schema(description = "일련번호", example = "1", type = "string")
        @get:JsonSerialize(using = ToStringSerializer::class)
        val positionId: Long,

        @get:Schema(description = "직급 코드", example = "PSN10")
        val positionCode: String,

        @get:Schema(description = "직급 이름", example = "대표")
        val positionName: String,

        @get:Schema(description = "기본급", example = "1000000", type = "string")
        @get:JsonSerialize(using = ToStringSerializer::class)
        val basicSalary: Long,

        @get:Schema(description = "성과급", example = "100000", type = "string")
        @get:JsonSerialize(using = ToStringSerializer::class)
        val incentiveSalary: Long
    )

    data class ProfileListData(
        override val employeeId: Long,
        override val employeeCode: String,
        override val employeeRole: EmployeeRole,
        override val employeeName: String,
        override val positionCode: String,
        override val positionName: String,
        override val departmentCode: String,
        override val departmentName: String,
        override val teamCode: String,
        override val teamName: String,
        override val phone: String,
        override val email: String,
        override val createdBy: Long,
        override val createdEmployeeName: String,
        override val updatedBy: Long,
        override val updatedEmployeeName: String,
        override val createdAt: Instant,
        override val updatedAt: Instant,
        override val deletedAt: Instant
    ) : Profile(
        employeeId,
        employeeCode,
        employeeRole,
        employeeName,
        positionCode,
        positionName,
        departmentCode,
        departmentName,
        teamCode,
        teamName,
        phone,
        email,
        createdBy,
        createdEmployeeName,
        updatedBy,
        updatedEmployeeName,
        createdAt,
        updatedAt,
        deletedAt
    )

    @Schema(name = "EmployeeProfileListResponse")
    data class ListData(
        val employeeProfiles: PageResponse<ProfileListData>,

        @get:ArraySchema(schema = Schema(implementation = Department::class))
        val departments: List<Department>,

        @get:ArraySchema(schema = Schema(implementation = Position::class))
        val positions: List<Position>,

        @get:Schema(description = "허용된 팀", example = "[\"TM100\", \"TM200\"]")
        val allowedProfileTeams: Set<String>
    ) : EmployeeProfileResponseDto

    @Schema(name = "EmployeeProfileDetailResponse")
    data class Detail(
        override val employeeId: Long,
        override val employeeCode: String,
        override val employeeRole: EmployeeRole,
        override val employeeName: String,
        override val positionCode: String,
        override val positionName: String,
        override val departmentCode: String,
        override val departmentName: String,
        override val teamCode: String,
        override val teamName: String,
        override val phone: String,
        override val email: String,
        override val createdBy: Long,
        override val createdEmployeeName: String,
        override val updatedBy: Long,
        override val updatedEmployeeName: String,
        override val createdAt: Instant,
        override val updatedAt: Instant,
        override val deletedAt: Instant,

        @get:ArraySchema(schema = Schema(implementation = Department::class))
        val departments: List<Department>,

        @get:ArraySchema(schema = Schema(implementation = Position::class))
        val positions: List<Position>,

        @get:Schema(description = "허용된 팀", example = "[\"TM100\", \"TM200\"]")
        val allowedProfileTeams: Set<String>
    ) : Profile(
        employeeId,
        employeeCode,
        employeeRole,
        employeeName,
        positionCode,
        positionName,
        departmentCode,
        departmentName,
        teamCode,
        teamName,
        phone,
        email,
        createdBy,
        createdEmployeeName,
        updatedBy,
        updatedEmployeeName,
        createdAt,
        updatedAt,
        deletedAt
    ), EmployeeProfileResponseDto
}