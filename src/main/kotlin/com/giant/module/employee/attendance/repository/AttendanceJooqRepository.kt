package com.giant.module.employee.attendance.repository

import com.giant.common.api.exception.CustomException
import com.giant.common.api.type.ResponseCode
import com.giant.module.employee.attendance.dto.response.AttendanceResponseDto
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.generated.employee.tables.references.ATTENDANCE
import org.jooq.generated.employee.tables.references.DEPARTMENT
import org.jooq.generated.employee.tables.references.POSITION
import org.jooq.generated.employee.tables.references.PROFILE
import org.jooq.generated.employee.tables.references.TEAM
import org.springframework.stereotype.Repository
import java.time.YearMonth

@Repository
class AttendanceJooqRepository(
    private val ctx: DSLContext
) {
    private fun <T : Any> Record.required(
        field: Field<T?>,
        code: ResponseCode = ResponseCode.INTERNAL_SERVER_ERROR
    ): T = this.get(field) ?: throw CustomException(code)

    private fun baseAttendanceQuery() =
        ctx.select(
            ad.ID,
            ad.EMPLOYEE_ID,
            pfMain.EMPLOYEE_NAME,
            ps.POSITION_NAME,
            dp.DEPARTMENT_NAME,
            tm.TEAM_NAME,
            ad.COMMUTE_DATE,
            ad.COMMUTE_TIME,
            ad.QUIT_TIME,
            ad.ATTENDANCE_STATUS,
            ad.REMARK,
            ad.CREATED_BY,
            createdEmployeeName,
            ad.UPDATED_BY,
            updatedEmployeeName,
            ad.CREATED_AT,
            ad.UPDATED_AT
        ).from(ad)
            .join(pfMain).on(ad.EMPLOYEE_ID.eq(pfMain.ID))
            .join(ps).on(pfMain.POSITION_ID.eq(ps.ID))
            .join(tm).on(pfMain.TEAM_ID.eq(tm.ID))
            .join(dp).on(tm.DEPARTMENT_ID.eq(dp.ID))
            .join(pfCr).on(ad.CREATED_BY.eq(pfCr.ID))
            .join(pfUd).on(ad.UPDATED_BY.eq(pfUd.ID))

    private fun Record.toAttendanceData() =
        AttendanceResponseDto.Attendance(
            attendanceId = required(ad.ID),
            employeeId = required(ad.EMPLOYEE_ID),
            employeeName = required(pfMain.EMPLOYEE_NAME),
            positionName = required(ps.POSITION_NAME),
            departmentName = required(dp.DEPARTMENT_NAME),
            teamName = required(tm.TEAM_NAME),
            commuteDate = required(ad.COMMUTE_DATE),
            commuteTime = this[ad.COMMUTE_TIME],
            quitTime = this[ad.QUIT_TIME],
            attendanceStatus = required(ad.ATTENDANCE_STATUS),
            remark = this[ad.REMARK],
            createdBy = required(ad.CREATED_BY),
            createdEmployeeName = required(createdEmployeeName),
            updatedBy = required(ad.UPDATED_BY),
            updatedEmployeeName = required(updatedEmployeeName),
            createdAt = required(ad.CREATED_AT),
            updatedAt = required(ad.UPDATED_AT)
        )

    fun findAttendanceListByCommuteMonth(commuteMonth: YearMonth):
            List<AttendanceResponseDto.Attendance> {
        val startCommuteDate = commuteMonth.atDay(1)
        val endCommuteDate = commuteMonth.plusMonths(1).atDay(1)

        return baseAttendanceQuery().where(
            ad.COMMUTE_DATE.ge(startCommuteDate),
            ad.COMMUTE_DATE.lt(endCommuteDate)
        ).fetch { it.toAttendanceData() }
    }

    companion object {
        private val ad = ATTENDANCE.`as`("ad")
        private val pfMain = PROFILE.`as`("pf_main")
        private val ps = POSITION.`as`("ps")
        private val tm = TEAM.`as`("tm")
        private val dp = DEPARTMENT.`as`("dp")
        private val pfCr = PROFILE.`as`("pf_cr")
        private val pfUd = PROFILE.`as`("pf_ud")

        private val createdEmployeeName =
            pfCr.EMPLOYEE_NAME.`as`("created_employee_name")

        private val updatedEmployeeName =
            pfUd.EMPLOYEE_NAME.`as`("updated_employee_name")
    }
}