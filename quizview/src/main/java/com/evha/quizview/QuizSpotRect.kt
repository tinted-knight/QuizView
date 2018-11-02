package com.evha.quizview

import android.graphics.Rect
import android.widget.TextView

data class QuizSpotRect(val rect: Rect, val startOffset: Int, val endOffset: Int) {

    val wordLength = endOffset - startOffset

    fun adjustOffset(textView: TextView) {
        rect.left += textView.left
        rect.right += textView.left
        rect.top += textView.top
        rect.bottom += textView.top
    }

}