package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.resource

import androidx.annotation.StringRes

interface Resource {
    fun getString(@StringRes stringId: Int): String
}