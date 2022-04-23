package bitshareskit.extensions

import android.view.ViewGroup
import androidx.core.view.children
import graphene.extension.info
import java.math.BigDecimal

fun BigDecimal.stripTrailingZerosFixes(): BigDecimal = if (compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else stripTrailingZeros()
