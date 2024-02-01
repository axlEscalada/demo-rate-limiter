package com.demo.configuration

import java.util.concurrent.TimeUnit

class DomainConfiguration(
    val unit: TimeUnit,
    val rate: Int,
    val limit: Int
)
