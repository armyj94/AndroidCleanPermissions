package com.armandodarienzo.composecleanpermissions.data

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.armandodarienzo.composecleanpermissions.domain.base.DataResult
import com.armandodarienzo.composecleanpermissions.domain.bluetooth.BluetoothRepository
import com.armandodarienzo.composecleanpermissions.domain.bluetooth.PairedDevice
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothRepositoryImpl @Inject constructor(@ApplicationContext private val mContext: Context)
    : BluetoothRepository {

        override suspend fun getPairedDevices(): DataResult<List<PairedDevice>, BluetoothRepository.Error.PermissionsNotGranted> {

            val bluetoothManager = mContext.getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter = bluetoothManager.adapter

            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    DataResult.Error(
                        BluetoothRepository.Error.PermissionsNotGranted(
                            listOf(
                                Manifest.permission.BLUETOOTH_CONNECT
                            )
                        )
                    )
                } else {
                    DataResult.Error(
                        BluetoothRepository.Error.PermissionsNotGranted(
                            listOf(
                                Manifest.permission.BLUETOOTH
                            )
                        )
                    )
                }
            }

            val devices = bluetoothAdapter?.bondedDevices ?: emptySet()

            return DataResult.Success(
                devices.map { device ->
                    PairedDevice(
                        name = device.name ?: "Unknown",
                        address = device.address
                    )
                }
            )
        }

}