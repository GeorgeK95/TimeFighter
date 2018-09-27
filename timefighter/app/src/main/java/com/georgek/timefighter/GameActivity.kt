/*
 * Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.georgek.timefighter

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


class GameActivity : AppCompatActivity() {

    companion object {
        private const val FIRST_PLAYER_SCORE_KEY = "F_SCORE_KEY"
        private const val SECOND_PLAYER_SCORE_KEY = "S_SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
        private const val BACKGROUND_COLOR = "BACKGROUND_COLOR"
    }

    val STATUS_CODE_ZERO = 0

    internal lateinit var gameScoreTextView: TextView
    internal lateinit var timeLeftTextView: TextView

    internal lateinit var fActionBtn: Button
    internal lateinit var sActionBtn: Button

    internal var gameStarted = false

    internal lateinit var countDownTimer: CountDownTimer
    internal var initialCountDown: Long = 60000
    internal var countDownInterval: Long = 1000
    internal var timeLeft = 60

    internal var firstPlayerScore = 0
    internal var secondPlayerScore = 0

    var currentColor = Color.LTGRAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        this.gameScoreTextView = findViewById(R.id.score_tv)
        this.timeLeftTextView = findViewById(R.id.time_tv)
        this.fActionBtn = findViewById(R.id.first_player_btn)
        this.sActionBtn = findViewById(R.id.second_player_btn)

        this.fActionBtn.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
            this.incrementScore(true)
        }

        this.sActionBtn.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
            this.incrementScore(false)
        }


        if (savedInstanceState != null) {
            this.timeLeft = savedInstanceState.getInt(TIME_LEFT_KEY)
            this.firstPlayerScore = savedInstanceState.getInt(FIRST_PLAYER_SCORE_KEY)
            this.secondPlayerScore = savedInstanceState.getInt(SECOND_PLAYER_SCORE_KEY)
            this.currentColor = savedInstanceState.getInt(BACKGROUND_COLOR)

            this.restoreGame();
        } else {
            this.resetGame()
        }

        this.setColorToLayout(findViewById(R.id.main_constraint_layout), currentColor)

    }

    private fun setColorToLayout(layout: ViewGroup, currentColor: Int) {
        layout.setBackgroundColor(currentColor);
    }

    private fun restoreGame() {
        val restoredScore = getString(R.string.your_score_s,
                Integer.toString(this.firstPlayerScore), Integer.toString(this.secondPlayerScore))
        gameScoreTextView.text = restoredScore

        val restoredTime = getString(R.string.time_left_s,
                Integer.toString(timeLeft))
        timeLeftTextView.text = restoredTime

        countDownTimer = object : CountDownTimer((timeLeft * 1000).toLong(),
                countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished.toInt() / 1000
                val timeLeftString = getString(R.string.time_left_s,
                        Integer.toString(timeLeft))
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }

        }

        countDownTimer.start()
        gameStarted = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(FIRST_PLAYER_SCORE_KEY, this.firstPlayerScore)
        outState.putInt(SECOND_PLAYER_SCORE_KEY, this.secondPlayerScore)
        outState.putInt(TIME_LEFT_KEY, timeLeft)
        outState.putInt(BACKGROUND_COLOR, currentColor)

        countDownTimer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.change_color_item) {
            changeColor()
        } else if (item.itemId == R.id.about_item) {
            openAboutDialog()
        } else if (item.itemId == R.id.exit_item) {
            finish();
            System.exit(STATUS_CODE_ZERO);
        }
        return true
    }

    private fun changeColor() {
        val layout: ConstraintLayout = findViewById(R.id.main_constraint_layout);

        if (currentColor.equals(Color.LTGRAY)) {
            setColorToLayout(layout, Color.DKGRAY)
            currentColor = Color.DKGRAY
        } else {
            setColorToLayout(layout, Color.LTGRAY)
            currentColor = Color.LTGRAY
        }
    }

    private fun closeGame() {
        finish();
        System.exit(0);
    }

    private fun openAboutDialog() {
        val dialogTitle = getString(R.string.app_name)
        val dialogMessage = getString(R.string.app_description)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

    private fun incrementScore(firstPlayer: Boolean) {
        if (!gameStarted) startGame()

        if (firstPlayer) this.firstPlayerScore++
        else this.secondPlayerScore++


        val newScore = getString(R.string.your_score_s, Integer.toString(this.firstPlayerScore),
                Integer.toString(this.secondPlayerScore))
        this.gameScoreTextView.text = newScore
    }

    private fun resetGame() {
        this.firstPlayerScore = 0
        this.secondPlayerScore = 0

        val initialScore = getString(R.string.your_score_s, Integer.toString(this.firstPlayerScore),
                Integer.toString(this.secondPlayerScore))
        this.gameScoreTextView.text = initialScore

        val initialTimeLeft = getString(R.string.time_left_s, Integer.toString(this.timeLeft))
        this.timeLeftTextView.text = initialTimeLeft

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onFinish() {
                endGame()
            }

            override fun onTick(timeTillFinish: Long) {
                timeLeft = timeTillFinish.toInt() / 1000

                val timeLeftString = getString(R.string.time_left_s, Integer.toString(timeLeft))
                timeLeftTextView.text = timeLeftString
            }

        }

        gameStarted = false
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame() {
        var winner = "First Player"
        if (this.secondPlayerScore > this.firstPlayerScore) {
            winner = "Second Player"
        } else if (this.secondPlayerScore == this.firstPlayerScore) {
            winner = "Nobody"
        }

        Toast.makeText(this, getString(R.string.game_over_message, winner), Toast.LENGTH_LONG).show()
        this.resetGame()
    }
}
