package com.bitshares.oases.preference.old

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import graphene.extension.*
import bitshareskit.serializer.UUIDSerializer
import com.bitshares.oases.MainApplication
import com.bitshares.oases.security.SecureKeyProvider
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import modulon.extensions.charset.toUnicodeByteArray
import modulon.extensions.charset.toUnicodeString
import modulon.extensions.livedata.NonNullMutableLiveData
import java.util.*
import kotlin.reflect.KClass


val sharedPreferenceName = "${MainApplication.requireContext().packageName}_preferences"

val preferenceSecureProvider = SecureKeyProvider(sharedPreferenceName, false)
val preferenceJsonConverter = Json { ignoreUnknownKeys = true }
val preferenceConverter = SerializersModule { }

val sharedPreferences = MainApplication.requireContext().getSharedPreferences("${MainApplication.requireContext().packageName}_preferences", Context.MODE_PRIVATE)


abstract class BaseSharedPreferencesLiveData<T>(private val sharedPreferences: SharedPreferences, private val key: String, val defaultValue: T) : LiveData<T>(), SharedPreferences.OnSharedPreferenceChangeListener {

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
        if (key == this.key) runCatching { super.setValue(getValue(sharedPreferences, key, defaultValue)) }
    }

    protected abstract fun getValue(sharedPreferences: SharedPreferences, key: String, defaultValue: T): T
    protected abstract fun putValue(sharedPreferences: SharedPreferences, key: String, value: T)
    override fun getValue(): T = super.getValue() ?: defaultValue

    val valueSafe get() = runCatching { getValue(sharedPreferences, key, defaultValue) }.getOrDefault(defaultValue)
    public override fun setValue(value: T) = runCatching { putValue(sharedPreferences, key, value) }.onFailure { it.printStackTrace() }.getOrDefault(Unit)
    fun isDefault() = value == defaultValue

    fun reset() {
        value = defaultValue
    }

    val default: NonNullMutableLiveData<T> get() = NonNullMutableLiveData(defaultValue)

}

class DefaultSerializableLiveData<T>(sharedPreferences: SharedPreferences, key: String, defaultValue: T, private val serializer: KSerializer<T>): BaseSharedPreferencesLiveData<T>(sharedPreferences, key, defaultValue) {

    private fun ByteArray.encodeBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP or Base64.URL_SAFE)
    private fun String.decodeBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP or Base64.URL_SAFE)

    init {
        registerListener(this)
    }

    override fun getValue(sharedPreferences: SharedPreferences, key: String, defaultValue: T): T {
        val encodedValue = sharedPreferences.getString(key, null)!!.decodeBase64()
        val hexValue = encodedValue.toUnicodeString()
        return preferenceJsonConverter.decodeFromString(serializer, hexValue)
    }

    override fun putValue(sharedPreferences: SharedPreferences, key: String, value: T) {
        val hexValue = preferenceJsonConverter.encodeToString(serializer, value).toUnicodeByteArray()
        sharedPreferences.edit {
            putString(key, hexValue.encodeBase64())
        }
    }
}


@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> livePreference(key: String, defaultValue: T): BaseSharedPreferencesLiveData<T> = DefaultSerializableLiveData(sharedPreferences, key, defaultValue, T::class.extendedSerializer())


class EncryptedSerializableLiveData<T>(sharedPreferences: SharedPreferences, key: String, defaultValue: T, private val serializer: KSerializer<T>): BaseSharedPreferencesLiveData<T>(sharedPreferences, key, defaultValue) {

    init {
        registerListener(this)
    }

    override fun getValue(sharedPreferences: SharedPreferences, key: String, defaultValue: T): T {
        val encodedValue = (sharedPreferences.getString(key, null) ?: throw NullPointerException("SharedPreferences key $key not found")).decodeBase64()
        val decryptionCipher = preferenceSecureProvider.initDecryptionCipher(encodedValue.copyOfRange(encodedValue.size - 16, encodedValue.size))
        val hexValue = decryptionCipher.doFinal(encodedValue.copyOfRange(0, encodedValue.size - 16))!!.toUnicodeString()

        return preferenceJsonConverter.decodeFromString(serializer, hexValue)
    }

    // FIXME: 2022/2/14 1644848541.464 6651-6818/com.bitshares.android W:     at android.security.keystore.AndroidKeyStoreCipherSpiBase.engineDoFinal(AndroidKeyStoreCipherSpiBase.java:519)
    //1644848541.464 6651-6818/com.bitshares.android W:     at javax.crypto.Cipher.doFinal(Cipher.java:1741)
    //1644848541.464 6651-6818/com.bitshares.android W:     at com.bitshares.android.preference.old.EncryptedSerializableLiveData.putValue(BaseSharedPreferencesLiveData.kt:114)
    //1644848541.464 6651-6818/com.bitshares.android W:     at com.bitshares.android.preference.old.BaseSharedPreferencesLiveData.setValue(BaseSharedPreferencesLiveData.kt:58)
    //1644848541.464 6651-6818/com.bitshares.android W:     at com.bitshares.android.netowrk.java_websocket.NetworkService$onNodeChange$1.invoke(NetworkService.kt:200)
    //1644848541.464 6651-6818/com.bitshares.android W:     at com.bitshares.android.netowrk.java_websocket.NetworkService$onNodeChange$1.invoke(NetworkService.kt:199)
    //1644848541.464 6651-6818/com.bitshares.android W:     at modulon.extensions.RxJavaKt$throttleLatest$1$1.invokeSuspend(RxJava.kt:66)
    override fun putValue(sharedPreferences: SharedPreferences, key: String, value: T) {
        val encryptionCipher = preferenceSecureProvider.initEncryptionCipher()
        val hexValue = preferenceJsonConverter.encodeToString(serializer, value).toUnicodeByteArray()
        val encodedValue = encryptionCipher.doFinal(hexValue)!! + encryptionCipher.iv
        sharedPreferences.edit {
            putString(key, encodedValue.encodeBase64())
        }
    }
}

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> KClass<T>.extendedSerializer(): KSerializer<T> = when (this) {
    UUID::class -> UUIDSerializer
    else -> serializer()
} as KSerializer<T>

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> liveEncryptedPreference(key: String, defaultValue: T): BaseSharedPreferencesLiveData<T> = EncryptedSerializableLiveData(sharedPreferences, key, defaultValue, T::class.extendedSerializer())

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> liveEncryptedPreference(key: String, defaultValue: Set<T>): BaseSharedPreferencesLiveData<Set<T>> = EncryptedSerializableLiveData(sharedPreferences, key, defaultValue, SetSerializer(T::class.extendedSerializer()))

@OptIn(InternalSerializationApi::class)
inline fun <reified K : Any, reified V : Any> liveEncryptedPreference(key: String, defaultValue: Map<K, V>): BaseSharedPreferencesLiveData<Map<K, V>> = EncryptedSerializableLiveData(sharedPreferences, key, defaultValue, MapSerializer(K::class.extendedSerializer(), V::class.serializer()))
