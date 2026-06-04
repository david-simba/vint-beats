package com.davidsimba.vintbeats.core.util

private val sizeRegex = Regex("=w\\d+-h\\d+")

fun String?.toHighRes(size: Int = 576): String? =
    this?.replace(sizeRegex, "=w$size-h$size")
