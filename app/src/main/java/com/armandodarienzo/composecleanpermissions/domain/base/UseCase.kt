package com.armandodarienzo.composecleanpermissions.domain.base

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class UseCase<in Parameters, Success, BusinessRuleError>
    (private val dispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(parameters: Parameters): Result<Success, BusinessRuleError> {
        return try {
            withContext(dispatcher) {
                execute(parameters)
            }
        } catch (e: Throwable) {
            Log.e("UseCase", "An error occurred while executing the use case", e)
            Result.Error(e.mapToAppError())
        }
    }

    protected abstract suspend fun execute(parameters: Parameters):
            Result<Success, BusinessRuleError>
}