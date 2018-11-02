package com.evha.quizview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.getStringOrThrow

class AttrReadHelper (context: Context, attrSet: AttributeSet?) {

    val drawable: Drawable
    val quizString: String
    val textViewLayoutId: Int
    val editTextLayoutId: Int

    init {
        val typedArray = context.obtainStyledAttributes(attrSet, R.styleable.QuizView, 0, R.style.QuizView)

        drawable = typedArray.getDrawableOrThrow(R.styleable.QuizView_quizSpotDrawable)
        quizString = typedArray.getStringOrThrow(R.styleable.QuizView_quizString)
        textViewLayoutId = typedArray.getResourceIdOrThrow(R.styleable.QuizView_quizTextViewLayout)
        editTextLayoutId = typedArray.getResourceIdOrThrow(R.styleable.QuizView_quizEditTextLayout)

        typedArray.recycle()
    }

}