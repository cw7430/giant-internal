package com.giant.module.employee.profile.dto.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.giant.common.api.request.PageRequest
import com.giant.common.api.type.SortOrder
import com.giant.module.employee.profile.type.EmployeeProfileSortPath
import com.giant.module.employee.profile.type.EmployeeRole
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*

interface EmployeeProfileRequestDto {
    @Schema(name = "UpsertEmployeeProfileRequest")
    open class UpsertEmployeeProfile(
        @field:NotBlank(message = "사원 이름을 입력해주세요.")
        @get:Schema(description = "사원 이름", example = "이사장")
        open val employeeName: String,

        @field:NotBlank(message = "직급을 선택해주세요.")
        @field:Pattern(
            regexp = "^PSN\\d{2,3}$",
            message = "직급 코드 형식이 올바르지 않습니다."
        )
        @get:Schema(description = "직급 코드", example = "PSN10")
        open val positionCode: String,

        @field:NotBlank(message = "팀을 선택해주세요.")
        @field:Pattern(
            regexp = "^TM\\d{3}$",
            message = "팀 코드 형식이 올바르지 않습니다."
        )
        @get:Schema(description = "팀 코드", example = "TM100")
        open val teamCode: String,

        @field:NotNull(message = "직책을 선택해주세요.")
        @get:Schema(description = "직책 권한", example = "EMPLOYEE")
        open val employeeRole: EmployeeRole
    ) {
        @get:JsonIgnore
        @get:AssertTrue(
            message = "입력 가능값: DEPARTMENT_CHIEF, TEAM_CHIEF, EMPLOYEE"
        )
        val isEmployeeRoleAllowed: Boolean
            get() = employeeRole != EmployeeRole.LEFT
    }

    @Schema(name = "EmployeeProfileListRequest")
    data class ListData(
        @get:Schema(description = "페이지", example = "1")
        override val page: Int = 1,

        @get:Schema(description = "페이지 당 데이트 사이즈", example = "5")
        override val size: Int = 5,

        @get:Schema(description = "페이지 블록 사이즈", example = "5")
        override val blockSize: Int = 5,

        @get:Schema(description = "정렬 키", example = "EMPLOYEE")
        val sortPath: EmployeeProfileSortPath = EmployeeProfileSortPath.EMPLOYEE,

        @get:Schema(description = "정렬 방식", example = "ASC")
        val sortOrder: SortOrder = SortOrder.ASC
    ) : PageRequest(page, size, blockSize), EmployeeProfileRequestDto

    @Schema(name = "CreateEmployeeProfileRequest")
    data class Create(
        @field:NotBlank(message = "사번을 생성해주세요.")
        @field:Pattern(
            regexp = "^EMP\\d{3,}$",
            message = "사번 형식이 올바르지 않습니다."
        )
        @get:Schema(description = "사번", example = "EMP001")
        val employeeCode: String,

        override val employeeName: String,

        @field:NotBlank(message = "전화번호를 입력해주세요.")
        @field:Pattern(
            regexp = "^(010|011|016|017|018|019)-\\d{3,4}-\\d{4}$",
            message = "전화번호 형식이 올바르지 않습니다."
        )
        @get:Schema(description = "휴대전화 번호", example = "010-1234-5678")
        val phone: String,

        @field:NotBlank(message = "이메일을 입력해주세요.")
        @field:Email(message = "이메일 형식이 올바르지 않습니다.")
        @get:Schema(description = "이메일", example = "example@example.com")
        val email: String,

        override val positionCode: String,

        override val teamCode: String,

        override val employeeRole: EmployeeRole

    ) : EmployeeProfileRequestDto, UpsertEmployeeProfile(
        employeeName, positionCode, teamCode, employeeRole
    )

    @Schema(name = "UpdateEmployeeProfileRequest")
    data class Update(
        override val employeeName: String,
        override val positionCode: String,
        override val teamCode: String,
        override val employeeRole: EmployeeRole
    ) : EmployeeProfileRequestDto, UpsertEmployeeProfile(
        employeeName, positionCode, teamCode, employeeRole
    )
}