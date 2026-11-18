package com.giant.module.employee.attendance.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class AttendanceJooqRepository(
    private val ctx: DSLContext
) {
}