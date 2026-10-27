package com.giant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GiantApplication

fun main(args: Array<String>) {
	runApplication<GiantApplication>(*args)
}
