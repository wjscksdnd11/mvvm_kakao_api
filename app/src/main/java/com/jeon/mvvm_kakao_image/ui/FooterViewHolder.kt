package com.jeon.mvvm_kakao_image.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeon.mvvm_kakao_image.R
import com.jeon.mvvm_kakao_image.State
import kotlinx.android.synthetic.main.item_list_footer.view.*


class FooterViewHolder private constructor(view: View): RecyclerView.ViewHolder(view){
    fun bind(status: State?){
        itemView.progress_bar.visibility = if(status == State.LOADING) View.VISIBLE else View.INVISIBLE
        itemView.txt_error.visibility = if(status== State.ERROR) View.VISIBLE else View.INVISIBLE

    }

    companion object{
        fun create(retry: () -> Unit, parent: ViewGroup): FooterViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_footer, parent, false)
            view.txt_error.setOnClickListener { retry() }
            return FooterViewHolder(view)
        }
    }
}