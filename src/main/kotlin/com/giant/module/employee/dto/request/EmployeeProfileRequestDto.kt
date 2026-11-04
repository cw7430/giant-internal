package com.giant.module.employee.dto.request

import com.giant.common.api.type.SortOrder
import com.giant.module.employee.type.EmployeeProfileSortPath
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

interface EmployeeProfileRequestDto {
    @Schema(name = "EmployeeProfileListRequest")
    data class ListData(
        @field:Min(value = 1, message = "1 이상 2,000,000,000 이하만 가능합니다.")
        @field:Max(value = 2000000000, message = "1 이상 2,000,000,000 이하만 가능합니다.")
        @get:Schema(description = "페이지", example = "1")
        val page: Int = 1,

        @field:Min(value = 5, message = "5 이상 100 이하만 가능합니다.")
        @field:Max(value = 100, message = "5 이상 100 이하만 가능합니다.")
        @get:Schema(description = "페이지 당 데이트 사이즈", example = "5")
        val size: Int = 5,

        @field:Min(value = 5, message = "5 이상 10 이하만 가능합니다.")
        @field:Max(value = 10, message = "5 이상 10 이하만 가능합니다.")
        @get:Schema(description = "페이지 블록 사이즈", example = "5")
        val blockSize: Int = 5,

        @get:Schema(description = "정렬 키", example = "employee")
        val sortPath: EmployeeProfileSortPath = EmployeeProfileSortPath.EMPLOYEE,

        @get:Schema(description = "정렬 방식", example = "asc")
        val sortOrder: SortOrder = SortOrder.ASC
    )
}