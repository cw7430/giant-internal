package com.giant.module.employee.dto.request

import com.giant.common.api.request.PageRequest
import com.giant.common.api.type.SortOrder
import com.giant.module.employee.type.EmployeeProfileSortPath
import com.giant.module.employee.type.EmployeeRole
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

interface EmployeeProfileRequestDto {
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

    @Schema(name = "CreateEmployeeProfile")
    data class Create(
        @field:NotBlank(message = "사번을 생성해주세요.")
        @field:Pattern(
            regexp = "^EMP\\d{3,}$",
            message = "사번 형식이 올바르지 않습니다."
        )
        @get:Schema(description = "사번", example = "EMP001")
        val employeeCode: String,

        @field:NotBlank(message = "사원 이름을 입력해주세요.")
        @get:Schema(description = "사원 이름", example = "이사장")
        val employeeName: String,

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

        @field:NotBlank(message = "직급을 선택해주세요.")
        @field:Pattern(
            regexp = "^PSN\\d{2,3}$",
            message = "직급 코드 형식이 올바르지 않습니다."
        )
        @get:Schema(description = "직급 코드", example = "PSN10")
        val positionCode: String,

        @field:NotBlank(message = "팀을 선택해주세요.")
        @field:Pattern(
            regexp = "^TM\\d{3}$",
            message = "팀 코드 형식이 올바르지 않습니다."
        )
        @get:Schema(description = "팀 코드", example = "TM100")
        val teamCode: String,

        @field:NotNull(message = "직책을 선택해주세요.")
        @get:Schema(description = "직책 권한", example = "EMPLOYEE")
        val employeeRole: EmployeeRole
    ) : EmployeeProfileRequestDto
}