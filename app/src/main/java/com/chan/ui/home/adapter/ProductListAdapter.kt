package com.chan.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chan.BR
import com.chan.databinding.ItemProductBinding
import com.chan.ui.home.model.ProductModel
import com.chan.ui.home.viewmodel.HomeViewModel
import javax.inject.Inject

class ProductListAdapter @Inject constructor(
    private val homeViewModel: HomeViewModel
) : PagingDataAdapter<ProductModel, ProductListAdapter.CustomViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding =
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        binding.setVariable(BR.homeViewModel, homeViewModel)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(position, it) }
    }

    class CustomViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, model: ProductModel) {
            binding.setVariable(BR.itemPosition, position)
            binding.setVariable(BR.productModel, model)
        }
    }

    override fun getItemViewType(position: Int): Int = position

    companion object {
        private val REPO_COMPARATOR =
            object : DiffUtil.ItemCallback<ProductModel>() {
                override fun areItemsTheSame(
                    oldItem: ProductModel,
                    newItem: ProductModel
                ): Boolean =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: ProductModel, newItem: ProductModel
                ): Boolean = oldItem == newItem
            }
    }

}