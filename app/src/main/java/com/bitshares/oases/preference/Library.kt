package com.bitshares.oases.preference

import com.bitshares.oases.R
import kotlinx.serialization.Serializable

@Serializable
data class Library(
    val name: String,
    val author: String,
    val version: String,
    val license: String,
    val licenseFile: String,
    val url: String,
    val icon: Int = R.drawable.ic_logo_empty,
)
