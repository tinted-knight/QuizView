package com.evha.quizview

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.FrameLayout

class QuizView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val answers by lazy { quizAnswers.map { it.text.toString() } }

    var spotFocusChangeListener: OnFocusChangeListener? = null
        set(value) {
            field = value
            quizAnswers.forEach { it.onFocusChangeListener = field }
        }

    lateinit var viewCheck: View

    private var etPrefixTag = "et_tag_"

    private val quizAnswers = ArrayList<EditText>()

    private val textView: QuizTextView

    private val editTextLayoutId: Int

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
            editText.apply {
                background = null
                // Limit EditText length to specific spot's word length
                filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it.wordLength - 2))
                tag = etPrefixTag + tagIndex++
                id = View.generateViewId()
                onFocusChangeListener = spotFocusChangeListener
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
        val size = quizAnswers.size
        for (i in 0 until size - 1)
            quizAnswers[i].nextFocusForwardId = quizAnswers[i + 1].id

        // If viewCheck then from last jump to viewCheck and to first
        // else from last jump to first
        if (::viewCheck.isInitialized) {
            quizAnswers[size - 1].nextFocusForwardId = viewCheck.id
            viewCheck.nextFocusForwardId = quizAnswers[0].id
        } else {
            quizAnswers[size - 1].nextFocusForwardId = quizAnswers[0].id
        }
    }

}