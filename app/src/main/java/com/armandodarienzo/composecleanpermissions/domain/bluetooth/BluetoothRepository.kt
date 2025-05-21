package com.armandodarienzo.composecleanpermissions.domain.bluetooth

import com.armandodarienzo.composecleanpermissions.domain.base.DataResult

interface BluetoothRepository {

    sealed class Error{
        data class PermissionsNotGranted(val missingPermissions: List<String>): Error()
    }

    suspend fun getPairedDevices(): DataResult<List<PairedDevice>, Error.PermissionsNotGranted>
}