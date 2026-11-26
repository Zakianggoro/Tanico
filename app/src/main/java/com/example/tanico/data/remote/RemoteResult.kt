package com.example.tanico.data.remote

sealed interface RemoteResult<out T> {
    data class Success<T>(val data: T) : RemoteResult<T>
    data class Failure(val message: String, val cause: Throwable? = null) : RemoteResult<Nothing>
}
