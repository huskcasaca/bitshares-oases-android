package com.bitshares.oases.preference

import androidx.appcompat.app.AppCompatDelegate

enum class DarkMode(val mode: Int) {
    FOLLOW_SYSTEM   (AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
    OFF             (AppCompatDelegate.MODE_NIGHT_NO),
    ON              (AppCompatDelegate.MODE_NIGHT_YES),
    AUTO_BATTERY    (AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY),
}