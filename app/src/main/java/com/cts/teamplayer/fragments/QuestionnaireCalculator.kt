package com.cts.teamplayer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cts.teamplayer.R
import com.cts.teamplayer.util.TeamPlayerSharedPrefrence

class QuestionnaireCalculator: Fragment(), View.OnClickListener {
    lateinit var v: View
    private var mpref: TeamPlayerSharedPrefrence? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_questionnairecalculator, container, false)
        mpref = TeamPlayerSharedPrefrence.getInstance(activity!!)
        return v
    }

    override fun onClick(v: View?) {
    }

}