package com.bitshares.android.provider.local_repo

import android.os.Build

object SystemBuildRepository {

    fun getSystemABIs(): List<String> {
        return Build.SUPPORTED_ABIS.orEmpty().toList();
    }


}