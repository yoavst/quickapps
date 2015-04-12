package com.yoavst.quickapps.simon

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import com.lge.qcircle.template.QCircleDialog
import com.lge.qcircle.template.QCircleTemplate
import com.lge.qcircle.template.TemplateTag
import com.lge.qcircle.template.TemplateType
import com.yoavst.kotlin.colorRes
import com.yoavst.kotlin.postDelayed
import com.yoavst.kotlin.stringResource
import com.yoavst.quickapps.R
import com.yoavst.quickapps.util.QCircleActivity
import com.yoavst.util.qCircleToast
import kotlinx.android.synthetic.simon_circle_layout.round
import java.util.ArrayList
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public class CSimonActivity : QCircleActivity() {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    val roundText: String by stringResource(R.string.round)
    var isGameRunning = false
    var isShowing = false
    var position = 0
    var handler = Handler()
    var game: SimonGame? = null
    var currentlyPlaying = MediaPlayer()
    var ids = intArray(R.id.red, R.id.blue, R.id.green, R.id.yellow)
    var pressedResources = intArray(R.color.md_red_500, R.color.md_blue_500, R.color.md_green_500, R.color.md_yellow_500)
    var regularResources = intArray(R.color.md_red_700, R.color.md_blue_700, R.color.md_green_700, R.color.md_yellow_700)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = template.getLayoutById(TemplateTag.CONTENT_MAIN)
        layout.addView(LayoutInflater.from(this).inflate(R.layout.simon_circle_layout, layout, false))
        setContentView(template.getView())
        for (id in ids) {
            findViewById(id).setOnClickListener { v ->
                if (isGameRunning && !isShowing) {
                    playByColor(SimonGame.Color.generateFrom((v.getTag() as String).toInt()))
                    val b = game?.press(SimonGame.Color.generateFrom((v.getTag() as String).toInt()))
                    if (b == null) {
                        handler.postDelayed(1000) {
                            newRound()
                        }
                    } else if (!b) lose()
                }
            }
            findViewById(id).setOnTouchListener { v, motionEvent ->
                if (isGameRunning && !isShowing) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        v.setBackgroundColor(colorRes(pressedResources[Integer.parseInt(v.getTag() as String)]))
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        v.setBackgroundColor(colorRes(regularResources[Integer.parseInt(v.getTag() as String)]))
                    }
                }
                false
            }
        }
        findViewById(R.id.back).setOnClickListener { v ->
            finish()
            template.unregisterReceiver()
        }
        findViewById(R.id.restart).setOnClickListener { v ->
            handler.removeCallbacksAndMessages(null)
            for (color in SimonGame.Color.values()) {
                fakeUnPress(color)
            }
            qCircleToast(R.string.start_game)
            handler.postDelayed(1500) {
                game = SimonGame()
                isGameRunning = true
                newRound()
            }
        }
    }

    fun newRound() {
        position = 0
        val colors = game!!.generateNext()
        round.setText(roundText.format(game!!.getRound()))
        GameRunnable(colors).run()
    }

    inner class GameRunnable(var colors: ArrayList<SimonGame.Color>) : Runnable {
        override fun run() {
            isShowing = true
            playByColor(colors.get(position))
            fakePress(colors.get(position))
            position++
            handler.postDelayed(1000) {
                fakeUnPress(colors.get(position - 1))
                if (position < colors.size()) {
                    handler.postDelayed(this, 200)
                } else
                    isShowing = false
            }
        }
    }

    fun lose() {
        isGameRunning = false
        if (preferences.highScoreInSimon().getOr(0) < game!!.getRound()) {
            preferences.highScoreInSimon().put(game!!.getRound())
        }
        val dialog = QCircleDialog.Builder()
                .setMode(QCircleDialog.DialogMode.Ok)
                .setTitle(getString(R.string.game_over))
                .setText(getString(R.string.your_score, game!!.getRound()))
                .setPositiveButtonListener { v ->
                    round.setText("")
                }
                .setImage(getResources().getDrawable(R.drawable.game_over))
                .create()
        dialog.show(this, template)
    }

    fun fakePress(color: SimonGame.Color) {
        val view = getViewByColor(color)
        when (color) {
            SimonGame.Color.Red -> view.setBackgroundColor(colorRes(pressedResources[0]))
            SimonGame.Color.Blue -> view.setBackgroundColor(colorRes(pressedResources[1]))
            SimonGame.Color.Green -> view.setBackgroundColor(colorRes(pressedResources[2]))
            SimonGame.Color.Yellow -> view.setBackgroundColor(colorRes(pressedResources[3]))
        }
    }

    fun fakeUnPress(color: SimonGame.Color) {
        val view = getViewByColor(color)
        when (color) {
            SimonGame.Color.Red -> view.setBackgroundColor(colorRes(regularResources[0]))
            SimonGame.Color.Blue -> view.setBackgroundColor(colorRes(regularResources[1]))
            SimonGame.Color.Green -> view.setBackgroundColor(colorRes(regularResources[2]))
            SimonGame.Color.Yellow -> view.setBackgroundColor(colorRes(regularResources[3]))
        }
    }

    fun getViewByColor(color: SimonGame.Color): View {
        return when (color) {
            SimonGame.Color.Red -> findViewById(ids[0])
            SimonGame.Color.Blue -> findViewById(ids[1])
            SimonGame.Color.Green -> findViewById(ids[2])
            else -> findViewById(ids[3])
        }
    }

    fun playByColor(color: SimonGame.Color) {
        try {
            currentlyPlaying.stop()
        } catch (ignored: Exception) {
        }

        currentlyPlaying.release()
        when (color) {
            SimonGame.Color.Red -> {
                currentlyPlaying = MediaPlayer.create(this, R.raw.a)
                currentlyPlaying.setOnCompletionListener { mediaPlayer -> mediaPlayer.release() }
                currentlyPlaying.start()
            }
            SimonGame.Color.Blue -> {
                currentlyPlaying = MediaPlayer.create(this, R.raw.b)
                currentlyPlaying.setOnCompletionListener { mediaPlayer -> mediaPlayer.release() }
                currentlyPlaying.start()
            }
            SimonGame.Color.Green -> {
                currentlyPlaying = MediaPlayer.create(this, R.raw.c)
                currentlyPlaying.setOnCompletionListener { mediaPlayer -> mediaPlayer.release() }
                currentlyPlaying.start()
            }
            SimonGame.Color.Yellow -> {
                currentlyPlaying = MediaPlayer.create(this, R.raw.d)
                currentlyPlaying.setOnCompletionListener { mediaPlayer -> mediaPlayer.release() }
                currentlyPlaying.start()
            }
        }

    }

    protected override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        try {
            currentlyPlaying.stop()
            currentlyPlaying.release()
        } catch (ignored: Exception) {
        }

    }

    override fun getIntentToShow(): Intent? = null
}