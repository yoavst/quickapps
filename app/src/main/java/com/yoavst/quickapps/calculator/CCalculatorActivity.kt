package com.yoavst.quickapps.calculator

import com.lge.qcircle.template.QCircleTemplate
import kotlin.properties.Delegates
import com.lge.qcircle.template.TemplateType
import com.yoavst.quickapps.util.QCircleActivity
import android.content.Intent
import com.yoavst.quickapps.PrefManager
import com.yoavst.util.createExplicit
import android.widget.TextView
import com.yoavst.quickapps.R
import android.os.Bundle
import android.view.View
import java.math.BigInteger
import java.math.BigDecimal
import kotlin.math.div
import java.text.DecimalFormat
import com.lge.qcircle.template.TemplateTag
import com.yoavst.quickapps.Expression
import android.support.v4.view.ViewPager
import com.lge.qcircle.template.QCircleBackButton
import com.yoavst.kotlin.stringArrayResource
import com.yoavst.kotlin.stringResource
import kotlinx.android.synthetic.calculator_circle_layout.*

/**
 * Created by Yoav.
 */
public class CCalculatorActivity : QCircleActivity() {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    private val OPEN_BRACKET = "("
    private val CLOSE_BRACKET = ")"
    private val INFINITY = "\u221E"
    val DOT by stringResource(R.string.dot)
    val PLUS by stringResource(R.string.plus)
    val MINUS by stringResource(R.string.minus)
    val MULTIPLE by stringResource(R.string.mul)
    val DIVIDE by stringResource(R.string.div)
    val POW by stringResource(R.string.exponentiation)
    val ERROR by stringResource(R.string.error)
    val SIN by stringResource(R.string.sin)
    val ASIN by stringResource(R.string.asin)
    val COS by stringResource(R.string.cos)
    val ACOS by stringResource(R.string.acos)
    val TAN by stringResource(R.string.tan)
    val ATAN by stringResource(R.string.atan)
    val ANSWER_TEXT by stringResource(R.string.ans)
    val OPERATORS by stringArrayResource(R.array.operators)
    var showingAnswer = false
    var lastAnswer: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        template.addElement(QCircleBackButton(this))
        val main = template.getLayoutById(TemplateTag.CONTENT_MAIN)
        val layout = getLayoutInflater().inflate(R.layout.calculator_circle_layout, main, false)
        main.addView(layout)
        setContentView(template.getView())
        pager.setAdapter(CalculatorAdapter(getFragmentManager()))
    }

    fun onNumberClicked(view: View) {
        cleanAnswer(copyAnswer = false)
        val currentText = text.getText().toString()
        if (view.getTag() != DOT) {
            if (!currentText.startsWith(ANSWER_TEXT) || currentText.length() != ANSWER_TEXT.length())
                text.append(view.getTag() as CharSequence)
        } else {
            if (currentText.length() == 0) text.append("0" + DOT)
            else if (currentText.startsWith(ANSWER_TEXT) && currentText.length() == ANSWER_TEXT.length()) {
                // Do nothing
            } else {
                val lastChar = getLastChar()
                if (lastChar.isOperator() || lastChar == OPEN_BRACKET) {
                    text.append("0" + DOT)
                } else if (DOT !in currentText) text.append(DOT)
                else {
                    var lastIndex = text.length() - 1
                    var c: String
                    do {
                        c = String(charArray(currentText[lastIndex]));
                        if (c == DOT) return
                        lastIndex--
                    } while (!c.isOperator() || lastIndex == -1)
                    text.append(DOT)
                }
            }
        }
    }

    fun onOperatorClicked(view: View) {
        cleanAnswer(copyAnswer = true)
        if (text.length() == 0) {
            if (view.getTag() == MINUS)
                text.append(view.getTag() as CharSequence)
        } else if (!getLastChar().isOperator()) {
            if (getLastChar() != OPEN_BRACKET) {
                text.append(view.getTag() as CharSequence)
            }

        } else if (text.length() != 1) {
            // it must be MINUS (-) than (if length == 1).
            val lastChar = getLastChar()
            val beforeLastChar = String(charArray(text.getText()[text.length() - 2]))
            if (view.getTag() == MINUS) {
                if (lastChar == PLUS) deleteLastChar()
                if (lastChar != MINUS) text.append(MINUS)
            } else {
                if (lastChar != MINUS || !beforeLastChar.isOperator()) {
                    deleteLastChar()
                    text.append(view.getTag() as CharSequence)
                }
            }
        }
    }

    fun onAnsClicked() {
        cleanAnswer(copyAnswer = false)
        if (text.length() == 0 || getLastChar().isOperator() && text.length() + 3 <= 16) {
            text.append(ANSWER_TEXT)
        }
    }

    fun onPiClicked(view: View) {
        onNumberClicked(view)
    }


    fun onTrigoClicked(view: View) {
        cleanAnswer(copyAnswer = false)
        val function = view.getTag() as CharSequence
        if (text.length() == 0 || (getLastChar().isOperator() && text.length() + function.length() <= 12)) {
            text.append(function.toString() + OPEN_BRACKET)
        }
    }

    fun deleteLastChar() {
        answer.setText(null)
        if (text.length() != 0) {
            if (text.length() >= 3) {
                if (text.getLastChars(3) == ANSWER_TEXT) {
                    text.setText(text.subStringToEnd(3))
                    return
                }
            }
            text.setText(text.subStringToEnd(1))
            if (text.length() >= 4) {
                val lastFour = text.getLastChars(4)
                if (lastFour == ASIN || lastFour == ACOS || lastFour == ATAN) {
                    text.setText(text.subStringToEnd(4))
                    return
                }
            }
            if (text.length() >= 3) {
                val lastThree = text.getLastChars(3)
                if (lastThree == SIN || lastThree == COS || lastThree == TAN) {
                    text.setText(text.subStringToEnd(3))
                }
            }
        }
        showingAnswer = false
    }


    fun onBracketsClicked() {
        cleanAnswer(copyAnswer = true)
        val numberOfBrackets = calculateOpenBrackets()
        if (numberOfBrackets == 0) {
            if (text.length() == 0 || getLastChar().isOperator())
                text.append(OPEN_BRACKET)
            else text.append(MULTIPLE + OPEN_BRACKET)
        } else {
            val lastChar = getLastChar()
            if (lastChar.isOperator() || lastChar == OPEN_BRACKET) {
                text.append(OPEN_BRACKET)
            } else if (lastChar == CLOSE_BRACKET) {
                text.append(MULTIPLE + OPEN_BRACKET)
            } else {
                text.append(CLOSE_BRACKET)
            }
        }
    }

    fun compute() {
        if (text.length() != 0) {
            if (showingAnswer) {
                if (ANSWER_TEXT !in text.getText().toString())
                    return
                val ans = answer.getText().toString()
                if (ERROR in ans || INFINITY in ans) return
                lastAnswer = ans
            }
            val math = fixFormat(removeLastOperator(addMissingBrackets(text.getText().toString())))
            try {
                var expression = Expression(math).with(ANSWER_TEXT, lastAnswer).with(PI, BigDecimal.valueOf(Math.PI)).setPrecision(16)
                val decimal = expression.eval().stripTrailingZeros()
                val numberOfDigits = numberOfDigits(decimal)
                var text: String
                if (numberOfDigits >= 14) {
                    //Force scientific notation
                    text = DecimalFormat("0.00#####E00").format(decimal)
                    val parts = text.split("E");
                    if (parts[0] == "1.00") text = "10" + POW + parts[1]
                    else {
                        val num = BigDecimal(parts[0])
                        if (num.isIntegerValue()) {
                            text = num.toBigInteger().toString() + MULTIPLE + "10" + POW + parts[1]
                        } else {
                            text = DecimalFormat("0.0#").format(num) + MULTIPLE + "10" + POW + parts[1]
                        }
                    }
                } else if (numberOfDigits >= 7) {
                    text = decimal.toPlainString()
                } else {
                    val possible = decimal.toString()
                    val forces = decimal.toPlainString()
                    if (forces == possible) text = forces
                    else {
                        val format = DecimalFormat("0.00#####E00").format(decimal)
                        val parts = format.split("E")
                        if (parts[0] == "1.00" && parts[1].toInt() <= -11) text = "10" + POW + parts[1]
                        else {
                            val num = BigDecimal(parts[0])
                            if (num.isIntegerValue()) {
                                if (parts[1].toInt() > -11) text = forces
                                else text = num.toBigInteger().toString() + MULTIPLE + "10" + POW + parts[1]
                            } else {
                                if (parts[1].toInt() > -11) text = forces
                                else text = DecimalFormat("0.0#").format(num) + MULTIPLE + "10" + POW + parts[1]
                            }
                        }
                    }
                }
                answer.setText(text)

            } catch (e: RuntimeException) {
                if (e.getMessage() != null && e.getMessage()!!.toLowerCase().contains("division by zero")) {
                    answer.setText(INFINITY)
                } else answer.setText(ERROR)
                e.printStackTrace()
            }
            showingAnswer = true
        }
    }

    private fun fixFormat(original: String?): String {
        if (original == null || original.length() == 0) return ""
        else {
            var output: String
            if (original.startsWith(MINUS + OPEN_BRACKET))
                output = original.replaceFirst(MINUS + "\\" + OPEN_BRACKET, MINUS + "1" + MULTIPLE + OPEN_BRACKET)
            else output = original
            output = output.replace(MULTIPLE, "*").replace(DIVIDE, "/")
            return output
        }
    }

    private fun removeLastOperator(original: String?): String {
        if (original == null || original.length() == 0) return ""
        else if (!getLastChar().isOperator()) return original
        else return original.substring(0, original.length() - 1)
    }

    private fun calculateOpenBrackets(): Int {
        val currentText = (text.getText()?.toString()).orEmpty()
        val open = OPEN_BRACKET[0]
        val close = CLOSE_BRACKET[0]
        var numberOfOpen = 0
        for (c in currentText) {
            if (c == open) numberOfOpen++
            else if (c == close) numberOfOpen--
        }
        return numberOfOpen
    }

    private fun addMissingBrackets(original: String): String {
        var openBrackets = calculateOpenBrackets()
        if (openBrackets == 0) return original
        else {
            val builder = StringBuilder(original)
            while (openBrackets != 0) {
                builder.append(CLOSE_BRACKET)
                openBrackets--
            }
            return builder.toString()
        }
    }

    private fun cleanAnswer(copyAnswer: Boolean) {
        if (showingAnswer) {
            val answer = answer.getText().toString()
            this.answer.setText(null)
            if (copyAnswer) {
                text.setText(ANSWER_TEXT)
            } else
                text.setText(null)
            if (answer.isNotEmpty() && !(ERROR in answer || INFINITY in answer)) lastAnswer = answer
            showingAnswer = false
        }
    }

    fun clearAll(): Boolean {
        text.setText(null)
        answer.setText(null)
        showingAnswer = false
        return true
    }

    private fun getLastChar(): String {
        return String(charArray(text.getText().charAt(text.length() - 1)));
    }

    private fun numberOfDigits(bigDecimal: BigDecimal): Int {
        return numberOfDigits(bigDecimal.toBigInteger())
    }

    private fun numberOfDigits(bigInteger: BigInteger): Int {
        var digits = bigInteger
        val ten = 10.bd
        var count = 0
        do {
            digits = digits / ten
            count++
        } while (digits != BigInteger.ZERO)
        return count
    }

    override fun getIntentToShow(): Intent? {
        return Intent().setClassName("com.android.calculator2", "com.android.calculator2.Calculator")
                .putExtra("com.lge.app.floating.launchAsFloating", PrefManager(this).calculatorForceFloating().getOr(false)).createExplicit(this)
    }

    private fun BigDecimal.isIntegerValue(): Boolean {
        return this.signum() == 0 || this.scale() <= 0 || this.stripTrailingZeros().scale() <= 0
    }

    fun TextView.subStringToEnd(fromEnd: Int): String = text.getText().toString().substring(0, text.getText().toString().length() - fromEnd)
    fun TextView.getLastChars(fromEnd: Int): String = text.getText().toString().substring(text.getText().toString().length() - fromEnd)

    fun CharSequence?.isOperator(): Boolean {
        if (this == null) return false
        for (operator in OPERATORS)
            if (operator.contentEquals(this)) return true;
        return false
    }

    val Int.bd: BigInteger
        get() = BigDecimal(this).toBigInteger()

    companion object {
        public val PI: String = "\u03C0"
    }
}