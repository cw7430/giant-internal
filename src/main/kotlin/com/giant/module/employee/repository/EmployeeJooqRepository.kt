package com.giant.module.employee.repository

import com.giant.common.api.exception.CustomException
import com.giant.common.api.type.ResponseCode
import com.giant.common.api.type.SortOrder
import com.giant.module.employee.dto.request.EmployeeProfileRequestDto
import com.giant.module.employee.dto.response.EmployeeProfileResponseDto
import com.giant.module.employee.type.EmployeeProfileSortPath
import com.giant.module.employee.type.EmployeeRole
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.SortField
import org.jooq.generated.auth.tables.references.ACCOUNT
import org.jooq.generated.employee.sequences.EMPLOYEE_CODE_SEQ
import org.jooq.generated.employee.tables.references.DEPARTMENT
import org.jooq.generated.employee.tables.references.POSITION
import org.jooq.generated.employee.tables.references.PROFILE
import org.jooq.generated.employee.tables.references.TEAM
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.springframework.stereotype.Repository

@Repository
class EmployeeJooqRepository(
    private val ctx: DSLContext
) {
    private fun <T : Any> Record.required(
        field: Field<T?>,
        code: ResponseCode = ResponseCode.INTERNAL_SERVER_ERROR
    ): T = this.get(field) ?: throw CustomException(code)

    private fun baseEmployeeProfileQuery() =
        ctx.select(
            pfMain.ID,
            pfMain.EMPLOYEE_CODE,
            pfMain.EMPLOYEE_ROLE,
            pfMain.EMPLOYEE_NAME,
            ps.POSITION_CODE,
            ps.POSITION_NAME,
            tm.TEAM_CODE,
            tm.TEAM_NAME,
            dp.DEPARTMENT_CODE,
            dp.DEPARTMENT_NAME,
            ac.PHONE,
            ac.EMAIL,
            pfMain.CREATED_BY,
            createdEmployeeName,
            pfMain.UPDATED_BY,
            updatedEmployeeName,
            pfMain.CREATED_AT,
            pfMain.UPDATED_AT,
            pfMain.DELETED_AT
        )
            .from(pfMain)
            .join(ps).on(pfMain.POSITION_ID.eq(ps.ID))
            .join(tm).on(pfMain.TEAM_ID.eq(tm.ID))
            .join(dp).on(tm.DEPARTMENT_ID.eq(dp.ID))
            .join(ac).on(pfMain.ID.eq(ac.ID))
            .join(pfCr).on(pfMain.CREATED_BY.eq(pfCr.ID))
            .join(pfUd).on(pfMain.UPDATED_BY.eq(pfUd.ID))
            .where(pfMain.EMPLOYEE_ROLE.ne(EmployeeRole.LEFT))

    private fun Record.toEmployeeProfileData() =
        EmployeeProfileResponseDto.ProfileData(
            employeeId = required(pfMain.ID),
            employeeCode = required(pfMain.EMPLOYEE_CODE),
            employeeRole = required(pfMain.EMPLOYEE_ROLE),
            employeeName = required(pfMain.EMPLOYEE_NAME),
            positionCode = required(ps.POSITION_CODE),
            positionName = required(ps.POSITION_NAME),
            departmentCode = required(dp.DEPARTMENT_CODE),
            departmentName = required(dp.DEPARTMENT_NAME),
            teamCode = required(tm.TEAM_CODE),
            teamName = required(tm.TEAM_NAME),
            phone = required(ac.PHONE),
            email = required(ac.EMAIL),
            createdBy = required(pfMain.CREATED_BY),
            createdEmployeeName = required(createdEmployeeName),
            updatedBy = required(pfMain.UPDATED_BY),
            updatedEmployeeName = required(updatedEmployeeName),
            createdAt = required(pfMain.CREATED_AT),
            updatedAt = required(pfMain.UPDATED_AT),
            deletedAt = this[pfMain.DELETED_AT]
        )


    fun findEmployeeProfileList(reqDto: EmployeeProfileRequestDto.ListData):
            List<EmployeeProfileResponseDto.ProfileData> {
        val sortFields = when (reqDto.sortPath) {
            EmployeeProfileSortPath.POSITION -> listOf(
                ps.POSITION_CODE,
                pfMain.EMPLOYEE_CODE,
                dp.DEPARTMENT_CODE,
                pfMain.CREATED_AT
            )

            EmployeeProfileSortPath.DEPARTMENT -> listOf(
                dp.DEPARTMENT_CODE,
                pfMain.EMPLOYEE_CODE,
                ps.POSITION_CODE,
                pfMain.CREATED_AT
            )

            else -> listOf(
                pfMain.EMPLOYEE_CODE,
                ps.POSITION_CODE,
                dp.DEPARTMENT_CODE,
                pfMain.CREATED_AT
            )
        }

        val orderCondition: List<SortField<*>> =
            sortFields.map { field ->
                if (reqDto.sortOrder == SortOrder.DESC) {
                    field.desc()
                } else {
                    field.asc()
                }
            }

        return baseEmployeeProfileQuery()
            .orderBy(orderCondition)
            .limit(reqDto.size)
            .offset((reqDto.page - 1) * reqDto.size)
            .fetch { it.toEmployeeProfileData() }
    }

    fun findEmployeeProfileByEmployeeId(employeeId: Long): EmployeeProfileResponseDto.ProfileData? {
        return baseEmployeeProfileQuery()
            .and(pfMain.ID.eq(employeeId))
            .fetchOne { it.toEmployeeProfileData() }
    }

    fun findDepartmentList(): List<EmployeeProfileResponseDto.Department> {
        val dp = DEPARTMENT.`as`("dp")
        val tm = TEAM.`as`("tm")

        return ctx.select(
            dp.ID,
            dp.DEPARTMENT_CODE,
            dp.DEPARTMENT_NAME,
            multiset(
                DSL.select(
                    tm.ID,
                    tm.TEAM_CODE,
                    tm.TEAM_NAME
                )
                    .from(tm)
                    .where(tm.DEPARTMENT_ID.eq(dp.ID))
                    .orderBy(tm.ID.asc())
            ).convertFrom { rs ->
                rs.mapNotNull { rc ->
                    EmployeeProfileResponseDto.Team(
                        teamId = rc.required(tm.ID),
                        teamCode = rc.required(tm.TEAM_CODE),
                        teamName = rc.required(tm.TEAM_NAME)
                    )
                }
            }
        ).from(dp).orderBy(dp.DEPARTMENT_CODE.asc()).fetch { rc ->
            EmployeeProfileResponseDto.Department(
                departmentId = rc.required(dp.ID),
                departmentCode = rc.required(dp.DEPARTMENT_CODE),
                departmentName = rc.required(dp.DEPARTMENT_NAME),
                teams = rc.value4()
            )
        }
    }

    fun findTeamCodeByEmployeeId(employeeId: Long): String? =
        ctx.select(tm.TEAM_CODE)
            .from(pfMain)
            .join(tm).on(
                pfMain.TEAM_ID.eq(tm.ID).and(pfMain.ID.eq(employeeId))
            )
            .fetchSingle()
            .value1()

    fun findPositionList(): List<EmployeeProfileResponseDto.Position> {
        val ps = POSITION.`as`("ps")

        return ctx.select(
            ps.ID,
            ps.POSITION_CODE,
            ps.POSITION_NAME,
            ps.BASIC_SALARY,
            ps.INCENTIVE_SALARY
        ).from(ps).orderBy(ps.POSITION_CODE.desc()).fetch { rc ->
            EmployeeProfileResponseDto.Position(
                positionId = rc.required(ps.ID),
                positionCode = rc.required(ps.POSITION_CODE),
                positionName = rc.required(ps.POSITION_NAME),
                basicSalary = rc.required(ps.BASIC_SALARY),
                incentiveSalary = rc.required(ps.INCENTIVE_SALARY)
            )
        }
    }

    fun findEmployeeCodeSeqNextVal(): Long? =
        ctx.select(EMPLOYEE_CODE_SEQ.nextval())
            .fetchSingle()
            .value1()

    fun countEmployeeProfileList() =
        ctx.fetchCount(
            DSL.selectFrom(pfMain).where(
                pfMain.EMPLOYEE_ROLE.ne(EmployeeRole.LEFT)
            )
        )

    fun existEmployeeProfileByEmployeeCode(employeeCode: String) =
        ctx.fetchExists(
            DSL.selectOne().from(pfMain)
                .where(
                    pfMain.EMPLOYEE_CODE.eq(employeeCode)
                )
        )

    fun createProfile(
        employeeId: Long,
        employeeCode: String,
        employeeName: String,
        teamCode: String,
        positionCode: String,
        employeeRole: EmployeeRole,
        createdEmployeeId: Long
    ) =
        ctx.insertInto(pfMain)
            .set(pfMain.ID, employeeId)
            .set(pfMain.EMPLOYEE_CODE, employeeCode)
            .set(pfMain.EMPLOYEE_NAME, employeeName)
            .set(
                pfMain.TEAM_ID,
                DSL.select(tm.ID)
                    .from(tm)
                    .where(tm.TEAM_CODE.eq(teamCode))
                    .asField()
            )
            .set(
                pfMain.POSITION_ID,
                DSL.select(ps.ID)
                    .from(ps)
                    .where(ps.POSITION_CODE.eq(positionCode))
                    .asField()
            )
            .set(pfMain.EMPLOYEE_ROLE, employeeRole)
            .set(pfMain.CREATED_BY, createdEmployeeId)
            .set(pfMain.UPDATED_BY, createdEmployeeId)
            .execute()

    companion object {
        private val pfMain = PROFILE.`as`("pf_main")
        private val ps = POSITION.`as`("ps")
        private val tm = TEAM.`as`("tm")
        private val dp = DEPARTMENT.`as`("dp")
        private val ac = ACCOUNT.`as`("ac")
        private val pfCr = PROFILE.`as`("pf_cr")
        private val pfUd = PROFILE.`as`("pf_ud")

        private val createdEmployeeName =
            pfCr.EMPLOYEE_NAME.`as`("created_employee_name")

        private val updatedEmployeeName =
            pfUd.EMPLOYEE_NAME.`as`("updated_employee_name")
    }
}