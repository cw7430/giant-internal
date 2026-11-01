package com.giant.module.auth.repository

import com.giant.common.api.exception.CustomException
import com.giant.common.api.type.ResponseCode
import com.giant.module.auth.dto.vo.AccountVo
import com.giant.module.auth.type.AuthRole
import org.jooq.DSLContext
import org.jooq.generated.auth.tables.references.ACCOUNT
import org.jooq.generated.auth.tables.references.REFRESH_TOKEN
import org.jooq.generated.employee.tables.references.DEPARTMENT
import org.jooq.generated.employee.tables.references.POSITION
import org.jooq.generated.employee.tables.references.PROFILE
import org.jooq.generated.employee.tables.references.TEAM
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class AuthJooqRepository(
    private val ctx: DSLContext
) {
    fun findAccountByUserName(userName: String): AccountVo.Login? {
        val ac = ACCOUNT.`as`("ac")
        val pf = PROFILE.`as`("pf")
        val tm = TEAM.`as`("tm")
        val dp = DEPARTMENT.`as`("dp")
        val ps = POSITION.`as`("ps")

        return ctx.select(
            ac.ID,
            ac.PASSWORD_HASH,
            ac.AUTH_ROLE,
            pf.EMPLOYEE_CODE,
            pf.EMPLOYEE_NAME,
            pf.EMPLOYEE_ROLE,
            dp.DEPARTMENT_CODE,
            tm.TEAM_CODE,
            ps.POSITION_CODE
        )
            .from(ac)
            .join(pf).on(ac.ID.eq(pf.ID))
            .join(tm).on(pf.TEAM_ID.eq(tm.ID))
            .join(dp).on(tm.DEPARTMENT_ID.eq(dp.ID))
            .join(ps).on(pf.POSITION_ID.eq(ps.ID))
            .where(ac.USER_NAME.eq(userName))
            .and(ac.AUTH_ROLE.ne(AuthRole.LEFT))
            .fetchOne { rc ->
                AccountVo.Login(
                    accountId = rc[ac.ID] ?: throw CustomException(ResponseCode.LOGIN_ERROR),
                    passwordHash = rc[ac.PASSWORD_HASH] ?: throw CustomException(ResponseCode.LOGIN_ERROR),
                    authRole = rc[ac.AUTH_ROLE] ?: throw CustomException(ResponseCode.LOGIN_ERROR),
                    employeeCode = rc[pf.EMPLOYEE_CODE] ?: throw CustomException(ResponseCode.LOGIN_ERROR),
                    employeeName = rc[pf.EMPLOYEE_NAME] ?: throw CustomException(ResponseCode.LOGIN_ERROR),
                    employeeRole = rc[pf.EMPLOYEE_ROLE] ?: throw CustomException(ResponseCode.LOGIN_ERROR),
                    departmentCode = rc[dp.DEPARTMENT_CODE] ?: throw CustomException(ResponseCode.LOGIN_ERROR),
                    teamCode = rc[tm.TEAM_CODE] ?: throw CustomException(ResponseCode.LOGIN_ERROR),
                    positionCode = rc[ps.POSITION_CODE] ?: throw CustomException(ResponseCode.LOGIN_ERROR),
                )
            }
    }

    fun createRefreshToken(accountId: Long, refreshToken: String, expiresAt: Instant) =
        ctx.insertInto(REFRESH_TOKEN)
            .set(REFRESH_TOKEN.ACCOUNT_ID, accountId)
            .set(REFRESH_TOKEN.TOKEN, refreshToken)
            .set(REFRESH_TOKEN.EXPIRES_AT, expiresAt)
            .execute()
}