package com.armandodarienzo.composecleanpermissions.domain.base

import com.armandodarienzo.composecleanpermissions.ui.base.MviProcessor

interface PermissionRequest<out Action: MviProcessor.MviAction> {
    val permissions: List<String>
    val actionToExecute: Action
    val rationaleMessage: String?
}