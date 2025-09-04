package com.armandodarienzo.composecleanpermissions.domain.base

import com.armandodarienzo.composecleanpermissions.ui.base.BaseViewModel

interface PermissionRequest<out Action: BaseViewModel.Action> {
    val permissions: List<String>
    val actionToExecute: Action
    val rationaleMessage: String?
}