package com.android_a865.gebril_app.features.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentContacts : Fragment(R.layout.fragment_contacts) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setUpActionBarWithNavController()


    }

}