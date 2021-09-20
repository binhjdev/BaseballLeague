package dev.binhjdev.androidbasketballleague.binding

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import dev.binhjdev.androidbasketballleague.R

@BindingAdapter("android:src")
fun setImageViewResource(imageView: ImageView, resourceId: Int) {
    imageView.setImageResource(resourceId)
}

@BindingAdapter("android:backgroundTint")
fun setBackgroundTint(view: View, colorId: Int) {
    view.background.setTintList(
            ContextCompat.getColorStateList(view.context, colorId)
    )
}

@BindingAdapter("isOccupied")
fun bindIsOccupied(imageView: ImageView, isOccupied: Boolean) {
    imageView.setImageResource(
        if(isOccupied) R.drawable.scoreboard_status_base_occupied
        else R.drawable.scoreboard_status_base_empty
    )
}

@BindingAdapter("isVisible")
fun bindIsVisible(view: View, isVisible: Boolean) {
    view.visibility = if(isVisible) View.VISIBLE else View.GONE
}