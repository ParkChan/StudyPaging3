package com.chan.ui.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.chan.ui.home.model.ProductModel

internal class ProductDiffer : DiffUtil.ItemCallback<ProductModel>() {
    override fun areItemsTheSame(
        oldItem: ProductModel,
        newItem: ProductModel
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: ProductModel,
        newItem: ProductModel
    ): Boolean = oldItem == newItem
}