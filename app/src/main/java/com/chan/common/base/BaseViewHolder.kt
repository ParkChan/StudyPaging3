package com.chan.common.base

import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder(
    parent: ViewGroup,
    private val layoutBindingId: Triple<Int, Int, Int>,
    private val viewModels: ArrayMap<Int, BaseViewModel>? = null
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(layoutBindingId.first, parent, false)
) {
    protected val binding: ViewDataBinding = DataBindingUtil.bind(itemView)!!

    open fun bindViewHolder(item: Any?, position: Int) {
        if (item == null) return
        binding.setVariable(layoutBindingId.second, item)
        binding.setVariable(layoutBindingId.third, position)

        if (viewModels == null) return
        for (key in viewModels.keys) {
            binding.setVariable(key, viewModels[key])
        }
    }
}