package com.bitshares.oases.preference

import bitshareskit.serializer.UUIDSerializer
import com.bitshares.oases.MainApplication
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.serializer
import modulon.union.ModulonApplication
import java.util.*
import kotlin.reflect.KClass

abstract class AbstractPreferenceManager(application: MainApplication) {

    protected val sharedPreferences: EncryptedPreference = EncryptedPreference(application)

    protected inline fun <reified T : Any> livePreferenceInternal(key: String, defaultValue: T): AbstractPreferenceLiveData<T> =
        DefaultPreferenceLiveData(sharedPreferences, key, defaultValue, T::class.extendedSerializerInternal())

    @OptIn(InternalSerializationApi::class)
    protected inline fun <reified T : Any> KClass<T>.extendedSerializerInternal(): KSerializer<T> = when (this) {
        UUID::class -> UUIDSerializer
        else -> serializer()
    } as KSerializer<T>

    @OptIn(InternalSerializationApi::class)
    protected inline fun <reified T : Any> liveEncryptedPreference(key: String, defaultValue: T): AbstractPreferenceLiveData<T> =
        EncryptedPreferenceLiveData(sharedPreferences, key, defaultValue, T::class.extendedSerializerInternal())

    @OptIn(InternalSerializationApi::class)
    protected inline fun <reified T : Any> liveEncryptedPreference(key: String, defaultValue: Set<T>): AbstractPreferenceLiveData<Set<T>> =
        EncryptedPreferenceLiveData(sharedPreferences, key, defaultValue, SetSerializer(T::class.extendedSerializerInternal()))

    @OptIn(InternalSerializationApi::class)
    protected inline fun <reified K : Any, reified V : Any> liveEncryptedPreference(key: String, defaultValue: Map<K, V>): AbstractPreferenceLiveData<Map<K, V>> =
        EncryptedPreferenceLiveData(sharedPreferences, key, defaultValue, MapSerializer(K::class.extendedSerializerInternal(), V::class.serializer()))

}