package com.giant.module.auth.repository

import com.giant.common.api.exception.CustomException
import com.giant.common.api.type.ResponseCode
import com.giant.module.auth.dto.vo.AccountVo
import com.giant.module.auth.type.AuthRole
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.generated.auth.tables.references.ACCOUNT
import org.jooq.generated.auth.tables.references.REFRESH_TOKEN
import org.jooq.generated.employee.tables.references.DEPARTMENT
import org.jooq.generated.employee.tables.references.POSITION
import org.jooq.generated.employee.tables.references.PROFILE
import org.jooq.generated.employee.tables.references.TEAM
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class AuthJooqRepository(
    private val ctx: DSLContext
) {
    private fun <T : Any> Record.required(
        field: Field<T?>,
        code: ResponseCode = ResponseCode.INTERNAL_SERVER_ERROR
    ): T = this.get(field) ?: throw CustomException(code)

    private fun joinForLoginData() =
        ac.join(pf).on(ac.ID.eq(pf.ID))
            .join(tm).on(pf.TEAM_ID.eq(tm.ID))
            .join(dp).on(tm.DEPARTMENT_ID.eq(dp.ID))
            .join(ps).on(pf.POSITION_ID.eq(ps.ID))

    fun findAccountByUserName(userName: String): AccountVo.Login? =
        ctx.select(
            ac.ID,
            ac.PASSWORD_HASH,
            ac.AUTH_ROLE,
            pf.EMPLOYEE_CODE,
            pf.EMPLOYEE_NAME,
            pf.EMPLOYEE_ROLE,
            dp.DEPARTMENT_CODE,
            tm.TEAM_CODE,
            ps.POSITION_CODE
        ).from(joinForLoginData())
            .where(
                ac.USER_NAME.eq(userName),
                ac.AUTH_ROLE.ne(AuthRole.LEFT)
            )
            .fetchOne { rc ->
                AccountVo.Login(
                    accountId = rc.required(ac.ID, ResponseCode.LOGIN_ERROR),
                    passwordHash = rc.required(ac.PASSWORD_HASH, ResponseCode.LOGIN_ERROR),
                    authRole = rc.required(ac.AUTH_ROLE, ResponseCode.LOGIN_ERROR),
                    employeeCode = rc.required(pf.EMPLOYEE_CODE, ResponseCode.LOGIN_ERROR),
                    employeeName = rc.required(pf.EMPLOYEE_NAME, ResponseCode.LOGIN_ERROR),
                    employeeRole = rc.required(pf.EMPLOYEE_ROLE, ResponseCode.LOGIN_ERROR),
                    departmentCode = rc.required(dp.DEPARTMENT_CODE, ResponseCode.LOGIN_ERROR),
                    teamCode = rc.required(tm.TEAM_CODE, ResponseCode.LOGIN_ERROR),
                    positionCode = rc.required(ps.POSITION_CODE, ResponseCode.LOGIN_ERROR)
                )
            }

    fun findAccountById(accountId: Long): AccountVo.Refresh? =
        ctx.select(
            ac.ID,
            ac.AUTH_ROLE,
            pf.EMPLOYEE_CODE,
            pf.EMPLOYEE_NAME,
            pf.EMPLOYEE_ROLE,
            dp.DEPARTMENT_CODE,
            tm.TEAM_CODE,
            ps.POSITION_CODE
        )
            .from(joinForLoginData())
            .where(
                ac.ID.eq(accountId),
                ac.AUTH_ROLE.ne(AuthRole.LEFT)
            )
            .fetchOne { rc ->
                AccountVo.Refresh(
                    accountId = rc.required(ac.ID, ResponseCode.UNAUTHORIZED),
                    authRole = rc.required(ac.AUTH_ROLE, ResponseCode.UNAUTHORIZED),
                    employeeCode = rc.required(pf.EMPLOYEE_CODE, ResponseCode.UNAUTHORIZED),
                    employeeName = rc.required(pf.EMPLOYEE_NAME, ResponseCode.UNAUTHORIZED),
                    employeeRole = rc.required(pf.EMPLOYEE_ROLE, ResponseCode.UNAUTHORIZED),
                    departmentCode = rc.required(dp.DEPARTMENT_CODE, ResponseCode.UNAUTHORIZED),
                    teamCode = rc.required(tm.TEAM_CODE, ResponseCode.UNAUTHORIZED),
                    positionCode = rc.required(ps.POSITION_CODE, ResponseCode.UNAUTHORIZED)
                )
            }

    fun existRefreshTokenByAccountIdAndToken(accountId: Long, refreshToken: String): Boolean =
        ctx.fetchExists(
            DSL.selectOne().from(rt)
                .where(
                    rt.ACCOUNT_ID.eq(accountId),
                    rt.TOKEN.eq(refreshToken)
                )
        )

    fun createAccountAndGet(
        userName: String,
        passwordHash: String,
        phone: String,
        email: String
    ) =
        ctx.insertInto(ac)
            .set(ac.USER_NAME, userName)
            .set(ac.PASSWORD_HASH, passwordHash)
            .set(ac.PHONE, phone)
            .set(ac.EMAIL, email)
            .returning()
            .fetchOne()

    fun createRefreshToken(accountId: Long, refreshToken: String, expiresAt: Instant) =
        ctx.insertInto(rt)
            .set(rt.ACCOUNT_ID, accountId)
            .set(rt.TOKEN, refreshToken)
            .set(rt.EXPIRES_AT, expiresAt)
            .execute()

    fun deleteRefreshTokenByRefreshToken(refreshToken: String) =
        ctx.deleteFrom(rt)
            .where(rt.TOKEN.eq(refreshToken))
            .execute()

    companion object {
        private val ac = ACCOUNT.`as`("ac")
        private val pf = PROFILE.`as`("pf")
        private val tm = TEAM.`as`("tm")
        private val dp = DEPARTMENT.`as`("dp")
        private val ps = POSITION.`as`("ps")
        private val rt = REFRESH_TOKEN.`as`("rt")
    }
}