package com.armandodarienzo.composecleanpermissions.domain.base

import java.io.IOException

class NetworkException(val code: StatusCode) : IOException()