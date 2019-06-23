package com.jeon.mvvm_kakao_image.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeon.mvvm_kakao_image.datas.ImageData
import kotlinx.android.synthetic.main.item_image.view.*
import android.net.Uri
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.jeon.mvvm_kakao_image.R


class ImageViewHolder private constructor(view: View,val onClickItem: (String) -> Unit) : RecyclerView.ViewHolder(view) {

    fun bind(imageData: ImageData?) {
        if (imageData != null) {
            val uri = Uri.parse(imageData.image_url)
            ImageRequestBuilder.newBuilderWithSource(uri).build().let {
                itemView.my_image_view.setImageRequest(it)
            }

            itemView.setOnClickListener {
                onClickItem(imageData.image_url)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup,  onClickItem: (String) -> Unit): ImageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image, parent, false)
            return ImageViewHolder(view, onClickItem)
        }
    }

}