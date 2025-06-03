package com.armandodarienzo.composecleanpermissions.domain.bluetooth

import com.armandodarienzo.composecleanpermissions.domain.base.DataResult
import com.armandodarienzo.composecleanpermissions.domain.base.Result
import com.armandodarienzo.composecleanpermissions.domain.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetPairedDevicesUseCase (
    private val bluetoothRepository: BluetoothRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<Unit, GetPairedDevicesUseCase.GetPairedDevicesSuccess, GetPairedDevicesUseCase.GetPairedDevicesError>(dispatcher) {

    sealed class GetPairedDevicesError {
        data object NoDevicesFound : GetPairedDevicesError()
        data object BluetoothShutDown : GetPairedDevicesError()
        data class MissingPermissions(val permissions: List<String>) : GetPairedDevicesError()
    }

    sealed class GetPairedDevicesSuccess {
        data class DevicesData(val devices: List<PairedDevice>) : GetPairedDevicesSuccess()
    }

    override suspend fun execute(parameters: Unit): Result<GetPairedDevicesSuccess, GetPairedDevicesError> {
        try {

            when (val devicesFetchOp = bluetoothRepository.getPairedDevices()) {
                is DataResult.Error ->
                    return Result.BusinessRuleError(
                        GetPairedDevicesError.MissingPermissions(devicesFetchOp.error.missingPermissions)
                    )

                is DataResult.Success -> {
                    devicesFetchOp.data.ifEmpty { null }?.let{
                        return Result.Success(GetPairedDevicesSuccess.DevicesData(it))
                    } ?:
                    return Result.BusinessRuleError(GetPairedDevicesError.NoDevicesFound)
                }
            }

        } catch (e: IllegalStateException) {
            return Result.BusinessRuleError(GetPairedDevicesError.BluetoothShutDown)
        }

    }

}