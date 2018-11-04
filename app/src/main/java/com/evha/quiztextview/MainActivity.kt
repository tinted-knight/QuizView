package com.evha.quiztextview

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.evha.quizview.QuizSpot
import com.evha.quizview.QuizView
import com.evha.quizview.logi

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAnswers = findViewById<Button>(R.id.btnAnswers)
        val quizView = findViewById<QuizView>(R.id.quizView)

        quizView.apply {
            setQuizSpots(prepareSpotsWithUnderscopes())
            spotFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) v.tag.toString().logi("focusListener")
            }
            // Try to comment this line and watch nextFocusForward behavior
            viewCheck = btnAnswers
        }

        btnAnswers.setOnClickListener {
            quizView.answers.forEach { answer -> answer.logi("ans: ") }
        }
    }

    private fun prepareSpotsWithUnderscopes(): List<QuizSpot> =
        ArrayList<QuizSpot>().apply {
            add(QuizSpot(0, 6))
            add(QuizSpot(10, 14))
            add(QuizSpot(22, 33))
            add(QuizSpot(93, 98))
        }

}
