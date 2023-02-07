package com.argusoft.who.emcare.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import javax.inject.Inject


class HomePagerAdapter @Inject constructor(
    activity: Fragment,
    private val patientListFragment: PatientListFragment,
    private val consultationListFragment: ConsultationListFragment): FragmentStateAdapter( activity) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                patientListFragment
            }
            1 -> {
                consultationListFragment
            }
            else -> {
                patientListFragment
            }
        }
    }


}