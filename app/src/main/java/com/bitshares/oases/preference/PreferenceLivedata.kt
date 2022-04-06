package com.bitshares.oases.preference

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import graphene.extension.*
import com.bitshares.oases.security.SecureKeyProvider
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import modulon.extensions.charset.toUnicodeByteArray
import modulon.extensions.charset.toUnicodeString
import modulon.extensions.livedata.NonNullMutableLiveData

class EncryptedPreference(
    context: Context,
    preference: SharedPreferences = context.getSharedPreferences(context.sharedPreferenceName, Context.MODE_PRIVATE)
): SharedPreferences by preference {

    companion object {
        private val Context.sharedPreferenceName get() = "${packageName}_preferences"
    }
    val secureProvider: SecureKeyProvider = SecureKeyProvider(context.sharedPreferenceName, false)

}

private val preferenceJsonConverter = Json { ignoreUnknownKeys = true }
private val preferenceConverter = SerializersModule { }

abstract class AbstractPreferenceLiveData<T>(
    private val sharedPreferences: EncryptedPreference,
    private val key: String, val defaultValue: T
) : LiveData<T>(), SharedPreferences.OnSharedPreferenceChangeListener {

    protected fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        runCatching { getValue(sharedPreferences, key, defaultValue) }.onSuccess {
            super.setValue(it)
        }.onFailure {
            it.printStackTrace()
            reset()
            super.setValue(defaultValue)
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == this.key) runCatching { super.setValue(getValue(this.sharedPreferences, key, defaultValue)) }
    }

    protected abstract fun getValue(sharedPreferences: EncryptedPreference, key: String, defaultValue: T): T
    protected abstract fun putValue(sharedPreferences: EncryptedPreference, key: String, value: T)
    override fun getValue(): T = super.getValue() ?: defaultValue

    val valueSafe get() = runCatching { getValue(sharedPreferences, key, defaultValue) }.getOrDefault(defaultValue)
    public override fun setValue(value: T) = runCatching { putValue(sharedPreferences, key, value) }.onFailure { it.printStackTrace() }.getOrDefault(Unit)
    fun isDefault() = value == defaultValue

    fun reset() { value = defaultValue }
    val default: NonNullMutableLiveData<T> get() = NonNullMutableLiveData(defaultValue)
}

class DefaultPreferenceLiveData<T>(
    sharedPreferences: EncryptedPreference,
    key: String, defaultValue: T,
    private val serializer: KSerializer<T>
): AbstractPreferenceLiveData<T>(sharedPreferences, key, defaultValue) {

    private fun ByteArray.encodeBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP or Base64.URL_SAFE)
    private fun String.decodeBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP or Base64.URL_SAFE)

    init {
        registerListener(this)
    }

    override fun getValue(sharedPreferences: EncryptedPreference, key: String, defaultValue: T): T {
        val encodedValue = sharedPreferences.getString(key, null)!!.decodeBase64()
        val hexValue = encodedValue.toUnicodeString()
        return preferenceJsonConverter.decodeFromString(serializer, hexValue)
    }

    override fun putValue(sharedPreferences: EncryptedPreference, key: String, value: T) {
        val hexValue = preferenceJsonConverter.encodeToString(serializer, value).toUnicodeByteArray()
        sharedPreferences.edit {
            putString(key, hexValue.encodeBase64())
        }
    }
}

class EncryptedPreferenceLiveData<T>(
    sharedPreferences: EncryptedPreference,
    key: String,
    defaultValue: T,
    private val serializer: KSerializer<T>
): AbstractPreferenceLiveData<T>(sharedPreferences, key, defaultValue) {

    init {
        registerListener(this)
    }

    override fun getValue(sharedPreferences: EncryptedPreference, key: String, defaultValue: T): T {
        val encodedValue = (sharedPreferences.getString(key, null) ?: throw NullPointerException("SharedPreferences key $key not found")).decodeBase64()
        val decryptionCipher = sharedPreferences.secureProvider.initDecryptionCipher(encodedValue.copyOfRange(encodedValue.size - 16, encodedValue.size))
        val hexValue = decryptionCipher.doFinal(encodedValue.copyOfRange(0, encodedValue.size - 16))!!.toUnicodeString()

        return preferenceJsonConverter.decodeFromString(serializer, hexValue)
    }

    override fun putValue(sharedPreferences: EncryptedPreference, key: String, value: T) {
        val encryptionCipher = sharedPreferences.secureProvider.initEncryptionCipher()
        val hexValue = preferenceJsonConverter.encodeToString(serializer, value).toUnicodeByteArray()
        val encodedValue = encryptionCipher.doFinal(hexValue)!! + encryptionCipher.iv
        sharedPreferences.edit {
            putString(key, encodedValue.encodeBase64())
        }
    }
}