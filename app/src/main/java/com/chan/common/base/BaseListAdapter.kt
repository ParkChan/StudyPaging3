package com.chan.common.base

import android.util.ArrayMap
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

class BaseListAdapter<ITEM : Any>(
    private val layoutBindingId: Triple<Int, Int, Int>,
    private val viewModels: ArrayMap<Int, BaseViewModel>? = null,
    diffCallback: DiffUtil.ItemCallback<ITEM>
) : PagingDataAdapter<ITEM, BaseViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        BaseViewHolder(parent, layoutBindingId, viewModels)

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindViewHolder(getItem(position), position)
    }

}