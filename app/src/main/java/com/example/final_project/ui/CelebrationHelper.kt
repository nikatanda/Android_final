package com.example.final_project.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import com.example.final_project.R
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

object CelebrationHelper {

    private val emojis = listOf("🎉", "✨", "🌟", "💫", "🎊", "⭐", "🚀", "💪")

    fun showTaskCompletedCelebration(anchor: View, message: String) {
        Snackbar.make(anchor, message, Snackbar.LENGTH_SHORT).show()
        burstEmojis(anchor)
    }

    fun animateFabEntrance(fab: View) {
        fab.scaleX = 0f
        fab.scaleY = 0f
        fab.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .setInterpolator(OvershootInterpolator(2f))
            .start()
    }

    fun fadeInContent(vararg views: View) {
        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 24f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(index * 80L)
                .setDuration(350)
                .start()
        }
    }

    private fun burstEmojis(anchor: View) {
        val root = anchor.rootView as? ViewGroup ?: return
        val overlay = FrameLayout(anchor.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isClickable = false
            isFocusable = false
        }
        root.addView(overlay)

        val centerX = anchor.rootView.width / 2f
        val centerY = anchor.rootView.height / 2f

        repeat(8) {
            val emoji = TextView(anchor.context).apply {
                text = emojis.random()
                textSize = 22f + Random.nextInt(6)
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                }
                x = centerX
                y = centerY
            }
            overlay.addView(emoji)

            val angle = Random.nextDouble(0.0, 2 * Math.PI)
            val distance = 120f + Random.nextInt(80)
            val targetX = centerX + (Math.cos(angle) * distance).toFloat()
            val targetY = centerY + (Math.sin(angle) * distance).toFloat()

            val moveX = ObjectAnimator.ofFloat(emoji, View.X, centerX, targetX)
            val moveY = ObjectAnimator.ofFloat(emoji, View.Y, centerY, targetY)
            val fade = ObjectAnimator.ofFloat(emoji, View.ALPHA, 1f, 0f)
            val scale = ObjectAnimator.ofFloat(emoji, View.SCALE_X, 0.5f, 1.2f)
            val scaleY = ObjectAnimator.ofFloat(emoji, View.SCALE_Y, 0.5f, 1.2f)

            AnimatorSet().apply {
                playTogether(moveX, moveY, fade, scale, scaleY)
                duration = 700
                startDelay = it * 40L
                start()
            }
        }

        overlay.postDelayed({ root.removeView(overlay) }, 900)
    }

    fun randomCompletionMessage(context: Context): String {
        val messages = context.resources.getStringArray(R.array.completion_messages)
        return messages.random()
    }
}
