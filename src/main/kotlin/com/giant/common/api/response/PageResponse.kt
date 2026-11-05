package com.giant.common.api.response

import com.giant.common.api.request.PageRequest
import io.swagger.v3.oas.annotations.media.Schema
import kotlin.math.ceil

@Schema(name = "PageResponse")
data class PageResponse<T>(
    @get:Schema(description = "데이터")
    val contents: List<T>,

    @get:Schema(description = "데이터 갯수", example = "100")
    val totalElements: Long,

    @get:Schema(description = "전체 페이지", example = "20")
    val totalPages: Int,

    @get:Schema(description = "현재 페이지", example = "1")
    val currentPage: Int,

    @get:Schema(description = "페이지 당 데이터 사이즈", example = "10")
    val size: Int,

    @get:Schema(description = "시작 페이지", example = "1")
    val startPage: Int,

    @get:Schema(description = "끝 페이지", example = "5")
    val endPage: Int,

    @get:Schema(description = "다음 버튼 존재 여부", example = "true")
    val hasNext: Boolean,

    @get:Schema(description = "이전 버튼 존재 여부", example = "false")
    val hasPrevious: Boolean
) {
    companion object {
        fun <T> of(contents: List<T>, pageRequest: PageRequest, totalElements: Long): PageResponse<T> {
            val totalPages = when (totalElements) {
                0L -> 1
                else -> ceil(totalElements.toDouble() / pageRequest.size).toInt()
            }

            val startPage: Int = (
                    ((pageRequest.page - 1) / pageRequest.blockSize) * pageRequest.blockSize + 1)
            val endPage = (startPage + pageRequest.blockSize - 1).coerceAtMost(totalPages)

            return PageResponse(
                contents,
                totalElements,
                totalPages,
                currentPage = pageRequest.page,
                pageRequest.size,
                startPage,
                endPage,
                hasNext = endPage < totalPages,
                hasPrevious = startPage > 1
            )
        }
    }
}