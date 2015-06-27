package com.yoavst.quickapps.calculator

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yoavst.kotlin.stringResource
import com.yoavst.quickapps.R
import kotlinx.android.synthetic.calculator_fragment.*

/**
 * Created by yoavst.
 */
public class CalculatorFragment : Fragment() {
    val Sin by stringResource(R.string.sin)
    val Asin by stringResource(R.string.asin)
    val Cos by stringResource(R.string.cos)
    val Acos by stringResource(R.string.acos)
    val Tan by stringResource(R.string.tan)
    val Atan by stringResource(R.string.atan)
    val Pow by stringResource(R.string.exponentiation)
    val Sqrt by stringResource(R.string.sqrt)
    val Log by stringResource(R.string.log)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.calculator_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = (getActivity() as CCalculatorActivity)
        arrayOf(R.id.digit0, R.id.digit1, R.id.digit2, R.id.digit3,
                R.id.digit4, R.id.digit5, R.id.digit6, R.id.digit7, R.id.digit8, R.id.digit9).forEachIndexed { position, id ->
            getView().findViewById(id).setOnClickListener { activity.appendDigit(position) }
        }
        dot.setOnClickListener { activity.appendDot() }
        div.setOnClickListener { activity.appendBinaryOperator(it.getTag().toString()) }
        mul.setOnClickListener { activity.appendBinaryOperator(it.getTag().toString()) }
        plus.setOnClickListener { activity.appendBinaryPrefixOperator(it.getTag().toString()) }
        minus.setOnClickListener { activity.appendBinaryPrefixOperator(it.getTag().toString()) }
        pi.setOnClickListener { activity.appendConstant(CCalculatorActivity.PI) }
        del.setOnClickListener { activity.deleteToken() }
        del.setOnLongClickListener { activity.deleteAll(); true }
        allClear.setOnClickListener { activity.deleteAll() }
        equal.setOnClickListener { activity.calculate() }
        answer.setOnClickListener { activity.appendAnswer() }
        sin.setOnClickListener { activity.appendNamedFunction(Sin) }
        asin.setOnClickListener { activity.appendNamedFunction(Asin) }
        cos.setOnClickListener { activity.appendNamedFunction(Cos) }
        acos.setOnClickListener { activity.appendNamedFunction(Acos) }
        tan.setOnClickListener { activity.appendNamedFunction(Tan) }
        atan.setOnClickListener { activity.appendNamedFunction(Atan) }
        brackets.setOnClickListener { activity.appendBrackets() }
        power.setOnClickListener { activity.appendBinaryOperator(Pow) }
        root.setOnClickListener { activity.appendNamedFunction(Sqrt) }
        log.setOnClickListener { activity.appendNamedFunction(Log) }
    }
}