package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.extention

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import developer.mihailzharkovskiy.sputniki_v_kosmose.R

fun Context.toast(text: String, gravity: Int = Gravity.BOTTOM, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).apply {
        setGravity(gravity, 0, 0)
        show()
    }
}

fun Number.toDp() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics
)

/**@param anchorView - view над которой будет показан snack bar**/
fun Fragment.showSnackBarAlarm(alarmMessage: String, rootView: View, anchorView: View): Snackbar {
    val snack = Snackbar.make(rootView, alarmMessage, Snackbar.LENGTH_LONG)
    with(snack) {
        this.anchorView = anchorView
        view.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_alarm)
        setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        setActionTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        setAction(requireContext().getString(R.string.sba_ok)) { this.dismiss() }
        show()
    }
    return snack
}