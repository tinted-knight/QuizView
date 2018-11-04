package com.evha.quizview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import java.lang.IllegalArgumentException

class QuizTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    internal val quizSpotRects = ArrayList<QuizSpotRect>()
    lateinit var quizSpots: List<QuizSpot>
    lateinit var drawable: Drawable

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        quizSpotRects.forEach {
            drawable.setBounds(it.rect.left, it.rect.top, it.rect.right, it.rect.bottom)
            drawable.draw(canvas)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        calculateSpots()
    }

    private fun calculateSpots() {
        require(quizSpots.isNotEmpty()) {
            throw IllegalArgumentException("Looks like `quizSpots` is empty")
        }
        quizSpotRects.clear()
        quizSpots.forEach {
            quizSpotRects.add(QuizSpotRect(calculateMeasures(it.start, it.end), it.start, it.end))
        }
    }

    // This function from here https://stackoverflow.com/a/11934366/8460732
    // and simplified a lot
    private fun calculateMeasures(startOffset: Int, endOffset: Int): Rect {
        val textViewLayout = this.layout
        val parentTextViewRect = Rect()

        val startXCoordinatesOfSpot = textViewLayout.getPrimaryHorizontal(startOffset)
        val endXCoordinatesOfSpot = textViewLayout.getPrimaryHorizontal(endOffset)

        val currentLineStartOffset = textViewLayout.getLineForOffset(startOffset)
        textViewLayout.getLineBounds(currentLineStartOffset, parentTextViewRect)

        val parentTextViewLocation = intArrayOf(0, 0)
        getLocationOnScreen(parentTextViewLocation)

        parentTextViewRect.apply {
            bottom = getLineBottomWithoutExtraPadding(currentLineStartOffset)
            top = getLineTopWithoutExtraPadding(currentLineStartOffset)
            left += (startXCoordinatesOfSpot + compoundPaddingLeft - scrollX).toInt()
            right = (parentTextViewRect.left + endXCoordinatesOfSpot - startXCoordinatesOfSpot).toInt()
            val lineHeightCorrection = spToPx(lineSpacingExtra) / 4
            top += lineHeightCorrection
        }

        return parentTextViewRect
    }

    // This two functions are from
    // here https://medium.com/androiddevelopers/drawing-a-rounded-corner-background-on-text-5a610a95af5
    // and here https://github.com/googlesamples/android-text/blob/master/RoundedBackground-Kotlin/lib/src/main/java/com/android/example/text/styling/roundedbg/LayoutExtensions.kt
    // with some changes
    private fun getLineBottomWithoutExtraPadding(line: Int): Int {
        val lineBottom = layout.getLineBottom(line)
        val isLastLine = (line == layout.lineCount - 1)

        val lineSpacingAdd = layout.spacingAdd
        val lineSpacingMultiplier = layout.spacingMultiplier
        val hasExtraSpacing = lineSpacingAdd != 0f || lineSpacingMultiplier != 1f

        val result =
            if (!hasExtraSpacing || isLastLine) {
                lineBottom
            } else {
                val extra =
                    if (lineSpacingMultiplier != 1f) {
                        val lineHeight = layout.getLineTop(line + 1) - layout.getLineTop(line)
                        lineHeight - (lineHeight - lineSpacingAdd) / lineSpacingMultiplier
                    } else lineSpacingAdd
                (lineBottom - extra).toInt()
            }

        val parentTextViewTopOffset = this.scrollY + this.compoundPaddingTop

        return result + parentTextViewTopOffset
    }

    private fun getLineTopWithoutExtraPadding(line: Int): Int {
        val lineTop = layout.getLineTop(line)

        val lineSpacingAdd = layout.spacingAdd
        val lineSpacingMultiplier = layout.spacingMultiplier

        val extra =
            if (lineSpacingMultiplier != 1f) {
                val lineHeight = layout.getLineTop(line + 1) - layout.getLineTop(line)
                lineHeight - (lineHeight - lineSpacingAdd) / lineSpacingMultiplier
            } else lineSpacingAdd

        val result = (lineTop - extra / 1.5).toInt()
        val parentTextViewTopOffset = this.scrollY + this.compoundPaddingTop

        return result + parentTextViewTopOffset
    }

    private fun spToPx(sp: Float): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, this.resources.displayMetrics).toInt()

}