package com.evha.quizview

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.FrameLayout

// TODO: Add to QuizView attributes spotSuffix and spotPrefix parameters (e.g. underscopes)

class QuizView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // TODO: option to set this prefix
    private var etPrefixTag = "et_tag_"

    private val quizAnswers = ArrayList<EditText>()

    val answers by lazy { quizAnswers.map { it.text.toString() } }

    private val textView: QuizTextView

    private val editTextLayoutId: Int

    var focusChangeListener: OnFocusChangeListener? = null
        set(value) {
            field = value
            quizAnswers.forEach { it.onFocusChangeListener = field }
        }

    init {
        val attrReadHelper = AttrReadHelper(context, attrs)
        editTextLayoutId = attrReadHelper.editTextLayoutId

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.quiz_view, this, true)

        textView = inflater.inflate(attrReadHelper.textViewLayoutId, this, false) as QuizTextView
        textView.apply {
            text = attrReadHelper.quizString
            drawable = attrReadHelper.drawable
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    drawEditTexts()
                }
            })
        }
        this.addView(textView)
    }

    fun setQuizSpots(values: List<QuizSpot>) {
        textView.quizSpots = values
    }

    private fun drawEditTexts() {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var tagIndex = 0
        textView.quizSpotRects.forEach {
            it.adjustOffset(textView)
            val editText = inflater.inflate(editTextLayoutId, this, false) as EditText
            // TODO: Add to QuizView attributes spotSuffix and spotPrefix parameters (e.g. underscopes)
            editText.apply {
                background = null
                // Limit EditText length to specific spot's word length
                filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.wordLength - 2))
                tag = etPrefixTag + tagIndex++
                id = View.generateViewId()
                onFocusChangeListener = focusChangeListener
                addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
                    view.apply {
                        left = it.rect.left
                        top = it.rect.top
                        right = it.rect.right
                        bottom = it.rect.bottom
                    }
                }
            }
            this.addView(editText)
            quizAnswers.add(editText)
        }
        reorderForNavigation()
    }

    private fun reorderForNavigation() {
        val maxId = quizAnswers.size - 1

        for (i in 1 until maxId)
            quizAnswers[i].nextFocusForwardId = quizAnswers[i + 1].id

        // TODO: option to set View id (e.g. Check button) to be after all EditText's
        // From last jump to first
        quizAnswers[maxId].nextFocusForwardId = quizAnswers[0].id
    }

}