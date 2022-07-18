package com.argusoft.who.emcare.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


class HomePagerAdapter @Inject constructor(
    activity: Fragment,
    private val patientListFragment: PatientListFragment,
    private val consultationListFragment: ConsultationListFragment): FragmentStateAdapter( activity) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        if(position == 0) {
            return patientListFragment
        } else if(position == 1) {
            return consultationListFragment
        } else {
            return patientListFragment
        }
    }

}