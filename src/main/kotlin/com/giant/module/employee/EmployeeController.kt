package com.giant.module.employee

import com.giant.common.api.doc.ErrorResponseDoc
import com.giant.module.employee.dto.request.EmployeeProfileRequestDto
import com.giant.module.employee.dto.response.EmployeeProfileResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/hr")
@Tag(name = "Employee Controller", description = "인사 API")
class EmployeeController(
    private val employeeService: EmployeeService
) {
    @GetMapping("/profiles")
    @Operation(summary = "직원목록")
    @SecurityRequirement(name = "access-token")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "직원 목록 조회 성공", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = EmployeeProfileRequestDto.ListData::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "400", description = "입력값 오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.BadRequest::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "401", description = "인증오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        oneOf = [
                            ErrorResponseDoc.Unauthorized::class,
                            ErrorResponseDoc.ExpiredToken::class,
                            ErrorResponseDoc.InvalidToken::class
                        ]
                    )
                )
            ]
        ),
        ApiResponse(
            responseCode = "403", description = "Api Key 오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.KeyError::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "500", description = "기타오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.InternalServerError::class)
                )
            ]
        )
    )
    fun getEmployeeProfileList(@ModelAttribute @Valid reqDto: EmployeeProfileRequestDto.ListData):
            ResponseEntity<EmployeeProfileResponseDto.ListData> =
        ResponseEntity.ok(employeeService.getEmployeeProfileList(reqDto))

    @GetMapping("/profiles/{employeeId}")
    @Operation(summary = "직원상세")
    @SecurityRequirement(name = "access-token")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "직원 상세 조회 성공", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = EmployeeProfileResponseDto.Detail::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "401", description = "인증오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        oneOf = [
                            ErrorResponseDoc.Unauthorized::class,
                            ErrorResponseDoc.ExpiredToken::class,
                            ErrorResponseDoc.InvalidToken::class
                        ]
                    )
                )
            ]
        ),
        ApiResponse(
            responseCode = "403", description = "Api Key 오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.KeyError::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "404", description = "없는 리소스", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.ResourceNotFound::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "500", description = "기타오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.InternalServerError::class)
                )
            ]
        )
    )
    fun getEmployeeProfileDetail(@PathVariable employeeId: Long):
            ResponseEntity<EmployeeProfileResponseDto.Detail> =
        ResponseEntity.ok(employeeService.getEmployeeProfileDetail(employeeId))


    @GetMapping("/employee-code")
    @Operation(summary = "사번 생성")
    @SecurityRequirement(name = "access-token")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "사번 생성 성공", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = EmployeeProfileResponseDto.EmployeeCode::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "401", description = "인증오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(
                        oneOf = [
                            ErrorResponseDoc.Unauthorized::class,
                            ErrorResponseDoc.ExpiredToken::class,
                            ErrorResponseDoc.InvalidToken::class
                        ]
                    )
                )
            ]
        ),
        ApiResponse(
            responseCode = "403", description = "Api Key 오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.KeyError::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "500", description = "기타오류", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponseDoc.InternalServerError::class)
                )
            ]
        )
    )
    fun getEmployeeCode(): ResponseEntity<EmployeeProfileResponseDto.EmployeeCode> =
        ResponseEntity.ok(employeeService.getEmployeeCode())
}