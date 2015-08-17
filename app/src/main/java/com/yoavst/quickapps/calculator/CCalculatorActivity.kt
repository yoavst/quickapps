package com.yoavst.quickapps.calculator

import android.content.Intent
import android.os.Bundle
import com.lge.qcircle.template.QCircleBackButton
import com.udojava.evalex.Expression
import com.yoavst.kotlin.e
import com.yoavst.kotlin.stringArrayResource
import com.yoavst.kotlin.stringResource
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import com.yoavst.quickapps.tools.calculatorForceFloating
import com.yoavst.quickapps.tools.createExplicit
import kotlinx.android.synthetic.calculator_activity.answer
import kotlinx.android.synthetic.calculator_activity.text
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat
import java.util.LinkedList
import kotlin.math.div

public class CCalculatorActivity : QCircleActivity() {
    private val OpenBracket = "("
    private val CloseBracket = ")"
    private val Infinity = "\u221E"
    private val Dot by stringResource(R.string.dot)
    private val Plus by stringResource(R.string.plus)
    private val Minus by stringResource(R.string.minus)
    private val Divide by stringResource(R.string.div)
    private val Multiple by stringResource(R.string.mul)
    private val AnswerText by stringResource(R.string.ans)
    private val Error by stringResource(R.string.error)
    private val Pow by stringResource(R.string.exponentiation)
    private val Sqrt by stringResource(R.string.sqrt)
    private val Log by stringResource(R.string.log)
    private val Operators by stringArrayResource(R.array.operators)
    private val Digits = 0..9
    private val tokens: LinkedList<String> = LinkedList()
    private var showingAnswer = false
    private var lastAnswer: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        template.addElement(QCircleBackButton(this))
        setContentViewToMain(R.layout.calculator_activity)
        setContentView(template.getView())
        getFragmentManager().beginTransaction().replace(R.id.layout, CalculatorFragment()).commit()
    }

    public fun appendBrackets() {
        cleanAnswer(copyAnswer = true)
        val numberOfBrackets = calculateOpenBrackets()
        val last = tokens.lastOrNull()
        val addedText: String
        if (last == null) addedText = OpenBracket
        else if (last in Operators) addedText = OpenBracket
        else if (last == OpenBracket) addedText = OpenBracket
        else if (last == CloseBracket && numberOfBrackets == 0) {
            text.append(Multiple)
            tokens.add(Multiple)
            addedText = OpenBracket
        } else {
            addedText = if (numberOfBrackets == 0) OpenBracket else CloseBracket
        }
        tokens.add(addedText)
        text.append(addedText)
    }

    public fun appendAnswer() {
        cleanAnswer(copyAnswer = false)
        if (lastAnswer.isNotEmpty()) {
            appendConstant(AnswerText)
        }
    }

    public fun appendConstant(constant: String) {
        cleanAnswer(copyAnswer = true)
        val last = tokens.lastOrNull()
        if (last == null || last in Operators || last == OpenBracket) {
            tokens.add(constant)
            text.append(constant)
        } else {
            tokens.add(Multiple)
            text.append(Multiple)
            tokens.add(constant)
            text.append(constant)
        }
    }

    public fun appendDot() {
        cleanAnswer(copyAnswer = false)
        val last = tokens.lastOrNull()
        if (last == null || last != Dot) {
            if (last == null || last in Operators || last == OpenBracket) {
                tokens.add("0")
                text.append("0")
                tokens.add(Dot)
                text.append(Dot)
            } else if (last == CloseBracket || last.toDigit() !in Digits) {
                tokens.add(Multiple)
                text.append(Multiple)
                tokens.add("0")
                text.append("0")
                tokens.add(Dot)
                text.append(Dot)
            } else {
                val currentText = text.getText().toString()
                if (Dot !in currentText) {
                    tokens.add(Dot)
                    text.append(Dot)
                } else {
                    var lastIndex = text.length() - 1
                    var c: String
                    do {
                        c = String(charArrayOf(currentText[lastIndex]));
                        if (c == Dot) return
                        lastIndex--
                    } while (c !in Operators || lastIndex == -1)
                    tokens.add(Dot)
                    text.append(Dot)
                }
            }
        }
    }

    public fun calculate() {
        if (text.length() > 0) {
            val math = fixFormat(removeLastOperator(addMissingBrackets(text.getText().toString())))
            var expression = Expression(math).setPrecision(16)
            if (lastAnswer.isNotEmpty()) expression.with(AnswerText, lastAnswer)
            val decimal = expression.eval().stripTrailingZeros()
            val numberOfDigits = numberOfDigits(decimal)
            var text: String
            if (numberOfDigits >= 14) {
                //Force scientific notation
                text = DecimalFormat("0.00#####E00").format(decimal)
                val parts = text.split("E".toRegex());
                if (parts[0] == "1.00") text = "10" + Pow + parts[1]
                else {
                    val num = BigDecimal(parts[0])
                    if (num.isIntegerValue()) {
                        text = num.toBigInteger().toString() + Multiple + "10" + Pow + parts[1]
                    } else {
                        text = DecimalFormat("0.0#").format(num) + Multiple + "10" + Pow + parts[1]
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
                    val parts = format.split("E".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (parts[0] == "1.00" && parts[1].toInt() <= -11) text = "10" + Pow + parts[1]
                    else {
                        val num = BigDecimal(parts[0])
                        if (num.isIntegerValue()) {
                            if (parts[1].toInt() > -11) text = forces
                            else text = num.toBigInteger().toString() + Multiple + "10" + Pow + parts[1]
                        } else {
                            if (parts[1].toInt() > -11) text = forces
                            else text = DecimalFormat("0.0#").format(num) + Multiple + "10" + Pow + parts[1]
                        }
                    }
                }
            }
            answer.setText(text)
            lastAnswer = text
            showingAnswer = true
        }
    }

    private fun fixFormat(original: String?): String {
        if (original == null || original.length() == 0) return ""
        else {
            var output: String
            if (original.startsWith(Minus + OpenBracket))
                output = original.replaceFirst((Minus + "\\" + OpenBracket).toRegex(), Minus + "1" + Multiple + OpenBracket)
            else output = original
            output = output.replace(Multiple, "*").replace(Divide, "/").replace(Sqrt, "SQRT").replace(PI, "PI").replace(Log, "LOG10")
            return output
        }
    }

    private fun addMissingBrackets(original: String): String {
        var openBrackets = calculateOpenBrackets()
        if (openBrackets == 0) return original
        else {
            val builder = StringBuilder(original)
            while (openBrackets != 0) {
                builder.append(CloseBracket)
                openBrackets--
            }
            return builder.toString()
        }
    }

    private fun numberOfDigits(bigDecimal: BigDecimal): Int {
        return numberOfDigits(bigDecimal.toBigInteger())
    }

    private fun numberOfDigits(bigInteger: BigInteger): Int {
        var digits = bigInteger
        val ten = 10.bd
        var count = 0
        do {
            digits /= ten
            count++
        } while (digits != BigInteger.ZERO)
        return count
    }


    private fun removeLastOperator(original: String?): String {
        if (original == null || original.length() == 0) return ""
        else if (original[original.length() - 1] !in Operators) return original
        else return original.substring(0, original.length() - 1)
    }

    fun String.toDigit(): Int {
        try {
            return toInt()
        } catch (e: NumberFormatException) {
            return -1
        }
    }

    public fun appendDigit(digit: Int) {
        cleanAnswer(copyAnswer = false)
        val last = tokens.lastOrNull()
        if (!(last == null || last in Operators || last.toDigit() in Digits || last == OpenBracket || last == Dot)) {
            tokens.add(Multiple)
            text.append(Multiple)
        }
        tokens.add(digit.toString())
        text.append(digit.toString())
    }

    public fun appendBinaryPrefixOperator(operator: String) {
        cleanAnswer(copyAnswer = true)
        val last = tokens.lastOrNull()
        if (last == null || last !in Operators) {
            tokens.add(operator)
            text.append(operator)
        } else {
            deleteToken()
            tokens.add(operator)
            text.append(operator)
        }
    }

    public fun appendBinaryOperator(operator: String) {
        cleanAnswer(copyAnswer = true)
        val last = tokens.lastOrNull()
        if (last != null && !((last == Plus || last == Minus) && (tokens.size() == 1 || tokens.get(tokens.size() - 2) in Operators))
                && last != OpenBracket) {
            if (last in Operators || last == Dot) {
                deleteToken()
            }
            tokens.add(operator)
            text.append(operator)
        }
    }

    public fun appendNamedFunction(name: String) {
        cleanAnswer(copyAnswer = false)
        val last = tokens.lastOrNull()
        if (last != null && (last.toDigit() in Digits || last in Operators || last == CloseBracket || last == Dot)) {
            tokens.add(Multiple)
            text.append(Multiple)
        }
        tokens.add(name)
        text.append(name)
        tokens.add(OpenBracket)
        text.append(OpenBracket)
        if (lastAnswer.isNotEmpty()) {
            tokens.add(AnswerText)
            text.append(AnswerText)
        }
    }

    public fun deleteToken() {
        val last = tokens.lastOrNull() ?: return
        val data = text.getText().toString()
        substringTextFromEnd(last.length())
        tokens.removeLast()
    }


    public fun deleteAll() {
        tokens.clear()
        text.setText(null)
        answer.setText(null)
        showingAnswer = false
    }


    private fun substringTextFromEnd(count: Int) {
        val data = text.getText().toString()
        text.setText(data.substring(0, data.length() - count))
    }

    private fun cleanAnswer(copyAnswer: Boolean) {
        if (showingAnswer) {
            this.answer.setText(null)
            tokens.clear()
            if (copyAnswer) {
                text.setText(AnswerText)
                tokens.add(AnswerText)
            } else
                text.setText(null)
            showingAnswer = false
        }
    }

    private fun calculateOpenBrackets(): Int {
        val currentText = (text.getText()?.toString()).orEmpty()
        val open = OpenBracket[0]
        val close = CloseBracket[0]
        var numberOfOpen = 0
        for (c in currentText) {
            if (c == open) numberOfOpen++
            else if (c == close) numberOfOpen--
        }
        return numberOfOpen
    }

    override fun getIntentToShow(): Intent? {
        return Intent().setClassName("com.android.calculator2", "com.android.calculator2.Calculator")
                .putExtra("com.lge.app.floating.launchAsFloating", calculatorForceFloating).createExplicit(this)
    }

    private fun BigDecimal.isIntegerValue(): Boolean {
        return this.signum() == 0 || this.scale() <= 0 || this.stripTrailingZeros().scale() <= 0
    }


    companion object {
        public val PI: String = "\u03C0"
        val Int.bd: BigInteger
            get() = BigDecimal(this).toBigInteger()

    }

}