package modulon.extensions.reflect

import java.lang.reflect.Field
import java.lang.reflect.Method

fun Any?.accessField(name: String, clazz: Class<*> = this!!::class.java): Field {
    return runCatching { clazz.getDeclaredField(name) }.getOrElse { runCatching { clazz.getField(name) }.getOrElse { accessField(name, clazz.superclass ?: throw NoSuchFieldException()) } }.apply { isAccessible = true }
}

fun Any?.accessMethod(name: String, clazz: Class<*> = this!!::class.java): Method {
    return runCatching { clazz.getDeclaredMethod(name) }.getOrElse { runCatching { clazz.getMethod(name) }.getOrElse { accessMethod(name, clazz.superclass ?: throw NoSuchMethodException()) } }.apply { isAccessible = true }
}

fun Any?.setField(name: String, value: Any?) {
    accessField(name).set(this, value)
}

fun Any?.invokeField(name: String): Any? {
    return accessField(name).get(this)
}

fun Any?.invokeMethod(name: String, vararg args: Any?): Any? {
    return accessMethod(name).invoke(this, args)
}

fun Any?.invokeMethod(name: String): Any? {
    return accessMethod(name).invoke(this)
}

fun <T> Any?.invokeFieldAs(name: String): T {
    return accessField(name).get(this) as T
}