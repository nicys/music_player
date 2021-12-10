package ru.nicys.musicplayer.exceptions

import java.io.IOException

sealed class AppException(var code: String) : RuntimeException() {
    companion object {
        fun from(e: Throwable): AppException = when (e) {
            is IOException -> NetworkException
            else -> UnknownException
        }
    }
}

class ApiException(val status: Int, code: String) : AppException(code)
object NetworkException : AppException("network_exception")
object UnknownException : AppException("error_unknown")