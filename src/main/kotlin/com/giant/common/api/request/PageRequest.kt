package com.giant.common.api.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(name = "PageRequest")
open class PageRequest(
    @field:Min(value = 1, message = "1 이상 2,000,000,000 이하만 가능합니다.")
    @field:Max(value = 2000000000, message = "1 이상 2,000,000,000 이하만 가능합니다.")
    @get:Schema(description = "페이지", example = "1")
    open val page: Int = 1,

    @field:Min(value = 5, message = "5 이상 100 이하만 가능합니다.")
    @field:Max(value = 100, message = "5 이상 100 이하만 가능합니다.")
    @get:Schema(description = "페이지 당 데이트 사이즈", example = "5")
    open val size: Int = 5,

    @field:Min(value = 5, message = "5 이상 10 이하만 가능합니다.")
    @field:Max(value = 10, message = "5 이상 10 이하만 가능합니다.")
    @get:Schema(description = "페이지 블록 사이즈", example = "5")
    open val blockSize: Int = 5,
)