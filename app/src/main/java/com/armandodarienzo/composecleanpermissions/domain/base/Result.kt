package com.armandodarienzo.composecleanpermissions.domain.base

import androidx.lifecycle.MutableLiveData

sealed class Result<out D, out E> {
    data class Success<out D>(val successData: D) : Result<D, Nothing>()
    data class Error(val error: AppError) : Result<Nothing, Nothing>()
    data class BusinessRuleError<out E>(val error: E) : Result<Nothing, E>()
    data object Loading : Result<Nothing, Nothing>()

    fun isSuccessful() = this is Success
    fun hasFailed() = this is Error || this is BusinessRuleError<*>
    fun isLoading() = this is Loading

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$successData]"
            is Error -> "Error[exception=$error]"
            is BusinessRuleError<*> -> "BusinessRuleError[error=$error]"
            Loading -> "Loading"
        }
    }
}

/**
 * [Success.data][Result.Success.successData] if [Result] is of type [Success][Result.Success]
 */
fun <D> Result<D, *>.successOr(fallback: D): D {
    return (this as? Result.Success<D>)?.successData ?: fallback
}

inline fun <D> Result<D, *>.onSuccess(block: (D) -> Unit): Result<D, *> {
    if (this is Result.Success<D>) {
        successData?.let { block(it) }
    }

    return this
}

inline fun <D, E> Result<D, E>.onBusinessRuleError(block: (E) -> Unit): Result<D, E> {
    if (this is Result.BusinessRuleError<E>) {
        block(error)
    }

    return this
}

inline fun <D> Result<D, *>.onError(block: (AppError) -> Unit): Result<D, *> {
    if (this is Result.Error) {
        block(error)
    }

    return this
}

inline fun <D> Result<D, *>.whenFinished(block: () -> Unit): Result<D, *> {
    block()
    return this
}

val <D> Result<D, *>.data: D?
    get() = (this as? Result.Success)?.successData

/**
 * Updates value of [liveData] if [Result] is of query [Success]
 */
inline fun <reified D> Result<D, *>.updateOnSuccess(liveData: MutableLiveData<D>) {
    if (this is Result.Success) {
        liveData.value = successData
    }
}