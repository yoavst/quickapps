package com.yoavst.quickapps.desktop

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.modules.DialerModuleView

/**
 * Created by yoavst.
 */
public class ModulesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.desktop_fragment_modules, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        (getView().findViewById(R.id.dialer_module) as DialerModuleView).onActivityResult(requestCode, resultCode, data)
    }
}