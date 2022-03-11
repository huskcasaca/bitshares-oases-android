package com.bitshares.oases

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class RuntimeProvider : ContentProvider() {

    companion object {
        val INSTANCE: RuntimeProvider by lazy {
            RuntimeProvider()
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        throw UnsupportedOperationException()
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException()
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException()
    }

}

fun runtime() {
    /* no-op */
}