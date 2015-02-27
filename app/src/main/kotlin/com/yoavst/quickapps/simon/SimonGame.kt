package com.yoavst.quickapps.simon

import java.util.ArrayList
import java.util.Random
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public class SimonGame {

    var colors = ArrayList<Color>()
    var ongoingColors: ArrayList<Color> by Delegates.notNull()
    var position: Int = 0

    enum class Color {
        Red
        Blue
        Green
        Yellow

        class object {
            public fun generate(): Color {
                return generateFrom(random.nextInt(4))
            }

            public fun generateFrom(num: Int): Color {
                when (num) {
                    0 -> return Red
                    1 -> return Blue
                    2 -> return Green
                    else -> return Yellow
                }
            }
        }
    }

    fun generateNext(): ArrayList<Color> {
        position = 0
        ongoingColors = ArrayList<Color>()
        colors.add(Color.generate())
        return colors
    }

    fun press(color: Color): Boolean? {
        if (colors.size() <= ongoingColors.size())
            return null
        else {
            ongoingColors.add(color)
            val b = color == colors.get(position)
            position++
            return if (b && colors.size() <= position) null else b
        }
    }

    fun getRound(): Int {
        return colors.size()
    }

    class object {
        var random = Random()
    }
}
