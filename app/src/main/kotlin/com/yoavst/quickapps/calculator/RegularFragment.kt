package com.yoavst.quickapps.calculator

import android.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.yoavst.quickapps.R

/**
 * Created by Yoav.
 */
public class RegularFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.calculator_fragment_regular, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fun numberClicked(view: View) = getActivity() as CCalculatorActivity onNumberClicked view
        for (id in intArray(R.id.digit0, R.id.digit1, R.id.digit2, R.id.digit3, R.id.digit4, R.id.digit5, R.id.digit6, R.id.digit7, R.id.digit8, R.id.digit9, R.id.dot)) {
           view!!.findViewById(id).setOnClickListener(::numberClicked)
        }
        fun operatorClicked(view: View) = getActivity() as CCalculatorActivity onOperatorClicked view
        for (id in intArray(R.id.div, R.id.exponentiation, R.id.mul, R.id.minus, R.id.plus)) {
            view!!.findViewById(id).setOnClickListener(::operatorClicked)
        }
        fun trigoClicked(view: View) = getActivity() as CCalculatorActivity onTrigoClicked  view
        for (id in intArray(R.id.sin, R.id.asin, R.id.cos, R.id.acos, R.id.tan, R.id.atan)) {
            view!!.findViewById(id).setOnClickListener(::trigoClicked)
        }
        val pi = view!!.findViewById(R.id.pi)
        pi.setTag(CCalculatorActivity.PI)
        pi.setOnClickListener { v -> (getActivity() as CCalculatorActivity).onPiClicked(v) }
        view.findViewById(R.id.del).setOnClickListener { v -> (getActivity() as CCalculatorActivity).deleteLastChar() }
        view.findViewById(R.id.paren).setOnClickListener { v -> (getActivity() as CCalculatorActivity).onBracketsClicked() }
        view.findViewById(R.id.ans).setOnClickListener { v -> (getActivity() as CCalculatorActivity).onAnsClicked() }
        view.findViewById(R.id.allClear).setOnClickListener { v -> (getActivity() as CCalculatorActivity).clearAll() }
        view.findViewById(R.id.del).setOnLongClickListener { v -> (getActivity() as CCalculatorActivity).clearAll() }
        view.findViewById(R.id.equal).setOnClickListener { v -> (getActivity() as CCalculatorActivity).compute() }

    }
}