package com.argusoft.who.emcare.ui.home

import android.graphics.Color
import android.text.SpannableString
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.ListItemBinding
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_ALBUM
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.ui.common.model.Album
import com.argusoft.who.emcare.utils.extention.*
import com.argusoft.who.emcare.utils.glide.GlideRequests
import com.argusoft.who.emcare.utils.glide.loadUrl

class HomeAdapter(
    val list: ArrayList<Album?> = arrayListOf(),
    private val glideRequests: GlideRequests,
    private val onClickListener: View.OnClickListener
) : BaseAdapter<Album>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType != LIST_ITEM_PROGRESS) {
            return AlbumViewHolder(parent.toBinding())
        }
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder) {
            is AlbumViewHolder -> {
                holder.bind(list[position])
            }
        }
    }

    inner class AlbumViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.title.setOnClickListener {
                it.navigate(R.id.action_homeFragment_to_detailFragment) {
                    putParcelable(INTENT_EXTRA_ALBUM, list[bindingAdapterPosition])
                }
            }
            binding.thumbnail.setOnClickListener (onClickListener)
        }

        fun bind(album: Album?) = with(album) {
            binding.title.text = album?.title ?: "loading"
            binding.subtitle.text = album?.thumbnailUrl.toString()

            val s = spannable {
                SpannableString(binding.root.context.getString(R.string.app_name)) +
                        bold(" some") +
                        getColor(Color.RED, italic(" formatted")) +
                        customFont(fontName = "sans-serif-medium", text = " appended") +
                        customFont(binding.root.context, text = " Custom Font", font = R.font.open_sans_bold)
            }
            binding.subtitle.text = s

            val string = spannable {
                bold(italic("nested ")) +
                        binding.title.url("www.google.com", "text")
            }

            binding.title.text = string
            glideRequests.loadUrl(binding.thumbnail, this?.url ?: "")
            binding.score.text = "${album?.id ?: 0}"
        }
    }
}