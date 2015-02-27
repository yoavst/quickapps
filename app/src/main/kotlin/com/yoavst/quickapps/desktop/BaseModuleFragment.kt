package com.yoavst.quickapps.desktop

import android.app.Fragment
import android.widget.CompoundButton
import com.yoavst.quickapps.PrefManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import kotlin.properties.Delegates
import com.mobsandgeeks.ake.viewById

public abstract class BaseModuleFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    abstract val CompoundButtonIds: IntArray
    abstract val rowsIds: IntArray
    abstract val layoutId: Int
    val prefs: PrefManager by Delegates.lazy { PrefManager(getActivity()) }

    abstract fun shouldCheck(id: Int): Boolean

    open fun init() {
    }

    public override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View? = inflater.inflate(layoutId, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super<Fragment>.onViewCreated(view, savedInstanceState)
        init()
        for (i in 0..CompoundButtonIds.size() - 1) {
            val CompoundButton: CompoundButton = viewById(CompoundButtonIds[i])
            CompoundButton.setChecked(shouldCheck(CompoundButtonIds[i]))
            CompoundButton.setOnCheckedChangeListener(this)
            val v = viewById<View>(rowsIds[i])
            v.setTag(CompoundButtonIds[i])
            v.setOnClickListener { v -> viewById<CompoundButton>(v.getTag() as Int).toggle() }
        }
    }
}