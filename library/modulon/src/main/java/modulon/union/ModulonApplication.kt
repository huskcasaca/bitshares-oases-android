package modulon.union

import android.app.Application
import android.content.Context

abstract class ModulonApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    fun requireContext(): Context = applicationContext
}