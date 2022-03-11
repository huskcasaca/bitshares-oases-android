@file:Suppress("DEPRECATION")

package com.bitshares.oases.security.fingerprint

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService

class FingerprintAuthentication(private val context: Context) : FingerprintManager.AuthenticationCallback() {

    companion object {

        fun canAuthenticate(context: Context): Boolean = context.getSystemService<FingerprintManager>()?.isHardwareDetected ?: false
//            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                val bioManager = context.getSystemService<BiometricManager>()
//                bioManager?.canAuthenticate() ?: BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
//            } else {
//                val bioManager = context.getSystemService<FingerprintManager>()
//                if (mFingerprintManager == null) {
//                    Log.e(BiometricManager.TAG, "Failure in canAuthenticate(). FingerprintManager was null.")
//                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
//                } else if (!mFingerprintManager.isHardwareDetected()) {
//                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
//                } else if (!mFingerprintManager.hasEnrolledFingerprints()) {
//                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
//                } else {
//                    BiometricManager.BIOMETRIC_SUCCESS
//                }
//            }
    }

    private lateinit var cancellationSignal: CancellationSignal

    fun startAuthentication(fingerprintManager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject, block: FingerprintAuthentication.() -> Unit) {
        block.invoke(this)
        cancellationSignal = CancellationSignal()
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) return
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    fun cancelAuthentication() {
        cancellationSignal.cancel()
    }

    private var onAuthError: (Int, CharSequence) -> Unit = { _, _ -> informationMessage("onAuthError") }
    private var onAuthSucceeded: (FingerprintManager.AuthenticationResult) -> Unit = { informationMessage("onAuthSucceeded") }
    private var onAuthHelp: (Int, CharSequence) -> Unit = { _, _ -> informationMessage("onAuthHelp") }
    private var onAuthFailed: () -> Unit = { informationMessage("onAuthFailed") }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) = onAuthError.invoke(errorCode, errString)
    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) = onAuthSucceeded.invoke(result)
    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) = onAuthHelp.invoke(helpCode, helpString)
    override fun onAuthenticationFailed() = onAuthFailed.invoke()

    fun FingerprintAuthentication.doOnAuthenticationError(block: (errorCode: Int, errString: CharSequence) -> Unit) {
        onAuthError = block
    }

    fun FingerprintAuthentication.doOnAuthenticationSucceeded(block: (result: FingerprintManager.AuthenticationResult) -> Unit) {
        onAuthSucceeded = block
    }

    fun FingerprintAuthentication.doOnAuthenticationHelp(block: (helpCode: Int, helpString: CharSequence) -> Unit) {
        onAuthHelp = block
    }

    fun FingerprintAuthentication.doOnAuthenticationFailed(block: () -> Unit) {
        onAuthFailed = block
    }

    private fun informationMessage(message: String) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()

}