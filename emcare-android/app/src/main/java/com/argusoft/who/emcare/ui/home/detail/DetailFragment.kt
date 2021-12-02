package com.argusoft.who.emcare.ui.home.detail

import com.argusoft.who.emcare.databinding.FragmentDetailBinding
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_ALBUM
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.model.Album

class DetailFragment : BaseFragment<FragmentDetailBinding>() {

    override fun initView() {
        requireArguments().getParcelable<Album>(INTENT_EXTRA_ALBUM)?.let {
            binding.nameTextView.text = it.id.toString()
        }
    }

    override fun initListener() {
    }

    override fun initObserver() {
    }
}