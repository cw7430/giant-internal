package com.giant.module.employee.attendance.dto.response

import com.giant.module.employee.attendance.type.AttendanceStatus
import io.swagger.v3.oas.annotations.media.Schema
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer
import java.time.Instant
import java.time.LocalDate

interface AttendanceResponseDto {
    @Schema(name = "AttendanceResponse")
    data class Attendance(
        @get:Schema(description = "일련번호", example = "1", type = "string")
        @get:JsonSerialize(using = ToStringSerializer::class)
        val attendanceId: Long,

        @get:Schema(description = "사원 일련번호", example = "1", type = "string")
        @get:JsonSerialize(using = ToStringSerializer::class)
        val employeeId: Long,

        @get:Schema(description = "출근일", example = "2026-03-02")
        val commuteDate: LocalDate,

        @get:Schema(description = "출근 시간", example = "2026-03-02T00:00:00.000Z")
        val commuteTime: Instant,

        @get:Schema(description = "퇴근 시간", example = "2026-03-02T09:00:00.000Z")
        val quitTime: Instant,

        @get:Schema(description = "근태 상태", example = "PRESENT")
        val attendanceStatus: AttendanceStatus,

        @get:Schema(description = "비고", example = "null", nullable = true)
        val remark: String?,

        @get:Schema(description = "작성자 일련번호", example = "1", type = "string", nullable = true)
        @get:JsonSerialize(using = ToStringSerializer::class)
        val createdBy: Long,

        @get:Schema(description = "작성자 이름", example = "이사장", nullable = true)
        val createdEmployeeName: String,

        @get:Schema(description = "수정자 일련번호", example = "1", type = "string", nullable = true)
        @get:JsonSerialize(using = ToStringSerializer::class)
        val updatedBy: Long,

        @get:Schema(description = "수정자 이름", example = "이사장", nullable = true)
        val updatedEmployeeName: String,

        @get:Schema(description = "생성 일시", example = "2026-03-02T00:00:00.000Z")
        val createdAt: Instant,

        @get:Schema(description = "수정 일시", example = "2026-03-02T00:00:00.000Z")
        val updatedAt: Instant,
    ): AttendanceResponseDto
}