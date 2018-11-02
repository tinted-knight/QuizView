package com.evha.quizview

import android.util.Log

private const val logTag = "QUIZ_TAGG"

fun String.logi(prefix: String = "") = Log.i(logTag, "$prefix: $this")

fun String.logw(prefix: String = "") = Log.w(logTag, "$prefix: $this")


