package com.chan.common.base

import android.util.ArrayMap
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class BaseAdapter<ITEM : Any>(
    private val layoutBindingId: Triple<Int, Int, Int>,
    private val viewModels: ArrayMap<Int, BaseViewModel>? = null
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val items = mutableListOf<ITEM>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        BaseViewHolder(parent, layoutBindingId, viewModels)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindViewHolder(items[position], position)
    }

    fun replaceItems(items: List<ITEM>?) {
        if (items == null) return

        this.items.run {
            clear()
            addAll(items)
            notifyDataSetChanged()
        }
    }

    fun addAllItems(items: List<ITEM>?) {
        if (items == null) return

        this.items.run {
            val positionStart = this.size
            addAll(items)
            notifyItemRangeInserted(positionStart, this.size)
        }
    }
}