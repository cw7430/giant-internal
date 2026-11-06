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

    fun findEmployeeProfileList(reqDto: EmployeeProfileRequestDto.ListData):
            List<EmployeeProfileResponseDto.ProfileListData> {
        val pfMain = PROFILE.`as`("pf_main")
        val ps = POSITION.`as`("ps")
        val tm = TEAM.`as`("tm")
        val dp = DEPARTMENT.`as`("dp")
        val ac = ACCOUNT.`as`("ac")
        val pfCr = PROFILE.`as`("pf_cr")
        val pfUd = PROFILE.`as`("pf_ud")
        val createdEmployeeName = pfCr.EMPLOYEE_NAME.`as`("created_employee_name")
        val updatedEmployeeName = pfUd.EMPLOYEE_NAME.`as`("updated_employee_name")

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

        return ctx.select(
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
        ).from(pfMain)
            .join(ps).on(pfMain.POSITION_ID.eq(ps.ID))
            .join(tm).on(pfMain.TEAM_ID.eq(tm.ID))
            .join(dp).on(tm.DEPARTMENT_ID.eq(dp.ID))
            .join(ac).on(pfMain.ID.eq(ac.ID))
            .join(pfCr).on(pfMain.CREATED_BY.eq(pfCr.ID))
            .join(pfUd).on(pfMain.UPDATED_BY.eq(pfUd.ID))
            .where(pfMain.EMPLOYEE_ROLE.ne(EmployeeRole.LEFT))
            .orderBy(orderCondition)
            .limit(reqDto.size)
            .offset((reqDto.page - 1) * reqDto.size)
            .fetch { rc ->
                EmployeeProfileResponseDto.ProfileListData(
                    employeeId = rc.required(pfMain.ID, ResponseCode.INTERNAL_SERVER_ERROR),
                    employeeCode = rc.required(pfMain.EMPLOYEE_CODE, ResponseCode.INTERNAL_SERVER_ERROR),
                    employeeRole = rc.required(pfMain.EMPLOYEE_ROLE, ResponseCode.INTERNAL_SERVER_ERROR),
                    employeeName = rc.required(pfMain.EMPLOYEE_NAME, ResponseCode.INTERNAL_SERVER_ERROR),
                    positionCode = rc.required(ps.POSITION_CODE, ResponseCode.INTERNAL_SERVER_ERROR),
                    positionName = rc.required(ps.POSITION_NAME, ResponseCode.INTERNAL_SERVER_ERROR),
                    departmentCode = rc.required(dp.DEPARTMENT_CODE, ResponseCode.INTERNAL_SERVER_ERROR),
                    departmentName = rc.required(dp.DEPARTMENT_NAME, ResponseCode.INTERNAL_SERVER_ERROR),
                    teamCode = rc.required(tm.TEAM_CODE, ResponseCode.INTERNAL_SERVER_ERROR),
                    teamName = rc.required(tm.TEAM_NAME, ResponseCode.INTERNAL_SERVER_ERROR),
                    phone = rc.required(ac.PHONE, ResponseCode.INTERNAL_SERVER_ERROR),
                    email = rc.required(ac.EMAIL, ResponseCode.INTERNAL_SERVER_ERROR),
                    createdBy = rc.required(pfMain.CREATED_BY, ResponseCode.INTERNAL_SERVER_ERROR),
                    createdEmployeeName = rc.required(createdEmployeeName, ResponseCode.INTERNAL_SERVER_ERROR),
                    updatedBy = rc.required(pfMain.UPDATED_BY, ResponseCode.INTERNAL_SERVER_ERROR),
                    updatedEmployeeName = rc.required(updatedEmployeeName, ResponseCode.INTERNAL_SERVER_ERROR),
                    createdAt = rc.required(pfMain.CREATED_AT, ResponseCode.INTERNAL_SERVER_ERROR),
                    updatedAt = rc.required(pfMain.UPDATED_AT, ResponseCode.INTERNAL_SERVER_ERROR),
                    deletedAt = rc[pfMain.DELETED_AT]
                )
            }
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
                ).from(tm)
                    .join(dp).on(tm.ID.eq(dp.ID))
                    .orderBy(tm.ID.asc())
            ).convertFrom { rs ->
                rs.mapNotNull { rc ->
                    EmployeeProfileResponseDto.Team(
                        teamId = rc.required(tm.ID, ResponseCode.INTERNAL_SERVER_ERROR),
                        teamCode = rc.required(tm.TEAM_CODE, ResponseCode.INTERNAL_SERVER_ERROR),
                        teamName = rc.required(tm.TEAM_NAME, ResponseCode.INTERNAL_SERVER_ERROR)
                    )
                }
            }
        ).from(dp).orderBy(dp.DEPARTMENT_CODE.asc()).fetch { rc ->
            EmployeeProfileResponseDto.Department(
                departmentId = rc.required(dp.ID, ResponseCode.INTERNAL_SERVER_ERROR),
                departmentCode = rc.required(dp.DEPARTMENT_CODE, ResponseCode.INTERNAL_SERVER_ERROR),
                departmentName = rc.required(dp.DEPARTMENT_NAME, ResponseCode.INTERNAL_SERVER_ERROR),
                teams = rc.value4()
            )
        }
    }

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
                positionId = rc.required(ps.ID, ResponseCode.INTERNAL_SERVER_ERROR),
                positionCode = rc.required(ps.POSITION_CODE, ResponseCode.INTERNAL_SERVER_ERROR),
                positionName = rc.required(ps.POSITION_NAME, ResponseCode.INTERNAL_SERVER_ERROR),
                basicSalary = rc.required(ps.BASIC_SALARY, ResponseCode.INTERNAL_SERVER_ERROR),
                incentiveSalary = rc.required(ps.INCENTIVE_SALARY, ResponseCode.INTERNAL_SERVER_ERROR)
            )
        }
    }

    fun countEmployeeProfileList() =
        ctx.fetchCount(
            DSL.selectFrom(PROFILE).where(
                PROFILE.EMPLOYEE_ROLE.ne(EmployeeRole.LEFT)
            )
        )
}