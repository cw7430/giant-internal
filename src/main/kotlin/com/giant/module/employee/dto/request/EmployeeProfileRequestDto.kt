package com.giant.module.employee.dto.request

import com.giant.common.api.request.PageRequest
import com.giant.common.api.type.SortOrder
import com.giant.module.employee.type.EmployeeProfileSortPath
import io.swagger.v3.oas.annotations.media.Schema

interface EmployeeProfileRequestDto {
    @Schema(name = "EmployeeProfileListRequest")
    data class ListData(
        @get:Schema(description = "페이지", example = "1")
        override val page: Int = 1,

        @get:Schema(description = "페이지 당 데이트 사이즈", example = "5")
        override val size: Int = 5,

        @get:Schema(description = "페이지 블록 사이즈", example = "5")
        override val blockSize: Int = 5,

        @get:Schema(description = "정렬 키", example = "employee")
        val sortPath: EmployeeProfileSortPath = EmployeeProfileSortPath.EMPLOYEE,

        @get:Schema(description = "정렬 방식", example = "asc")
        val sortOrder: SortOrder = SortOrder.ASC
    ): PageRequest(page, size, blockSize)
}