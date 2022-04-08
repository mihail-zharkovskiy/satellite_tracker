package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.resource

import android.content.Context
import androidx.annotation.StringRes
import javax.inject.Inject

class ResourceImpl @Inject constructor(private val context: Context) : Resource {
    override fun getString(@StringRes stringId: Int): String {
        return context.getString(stringId)
    }
}