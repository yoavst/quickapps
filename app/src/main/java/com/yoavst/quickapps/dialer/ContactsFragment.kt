package com.yoavst.quickapps.dialer

import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yoavst.kotlin.toPx
import com.yoavst.quickapps.tools.DividerItemDecoration

/**
 * Created by Yoav.
 */
public class ContactsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val recycler = RecyclerView(getActivity())
        recycler.setPadding(32.toPx(getActivity()), 0, 32.toPx(getActivity()), 0)
        recycler.setLayoutManager(LinearLayoutManager(getActivity()))
        recycler.setAdapter(ContactsAdapter(getActivity() as CDialerActivity) {
            if (it.isNotEmpty())
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + it)))
        })
        recycler.addItemDecoration(DividerItemDecoration(getActivity(), null))
        return recycler
    }
}