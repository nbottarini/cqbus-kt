package dev.botta.cqbus

enum class MiddlewarePriorities(val value: Int) {
    VeryHigh(10_000),
    High(1_000),
    Normal(0),
    Low(-1_000),
    VeryLow(-10_000),
}
