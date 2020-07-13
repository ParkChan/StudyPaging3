package com.chan.ui.home

import android.os.Bundle
import android.util.ArrayMap
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import com.chan.BR
import com.chan.R
import com.chan.common.base.BaseFragment
import com.chan.common.base.BaseListAdapter
import com.chan.common.base.BaseViewModel
import com.chan.databinding.FragmentHomeBinding
import com.chan.ui.detail.ProductDetailActivityContract
import com.chan.ui.detail.ProductDetailContractData
import com.chan.ui.home.model.ProductModel
import com.chan.ui.home.viewmodel.HomeViewModel
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    R.layout.fragment_home
) {

    private val homeViewModel by viewModels<HomeViewModel>()
    lateinit var adapter: BaseListAdapter<ProductModel>

    private val activityResultLauncher: ActivityResultLauncher<ProductDetailContractData> =
        registerForActivityResult(
            ProductDetailActivityContract()
        ) { result: ProductDetailContractData ->
            adapter.notifyItemChanged(result.position)
            Logger.d("registerForActivityResult >>> position is ${result.position} ")
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        iniViewModelObserve()
        requestFistPage()
    }

    private fun initViewModel() {
        binding.homeViewModel = homeViewModel

        val viewModels = ArrayMap<Int, BaseViewModel>()
        viewModels[BR.homeViewModel] = homeViewModel

        adapter = BaseListAdapter(
            layoutBindingId = Triple(R.layout.item_product, BR.productModel, BR.itemPosition),
            viewModels = viewModels,
            diffCallback = object : DiffUtil.ItemCallback<ProductModel>() {
                override fun areItemsTheSame(
                    oldItem: ProductModel,
                    newItem: ProductModel
                ): Boolean =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: ProductModel, newItem: ProductModel
                ): Boolean = oldItem == newItem
            })

        binding.rvProduct.adapter = adapter
    }

    private fun iniViewModelObserve() {

        homeViewModel.errorMessage.observe(
            viewLifecycleOwner,
            Observer {
                context?.let {
                    Toast.makeText(
                        it,
                        getString(R.string.common_toast_msg_network_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        homeViewModel.productItemSelected.observe(
            viewLifecycleOwner,
            Observer {
                activityResultLauncher.launch(
                    ProductDetailContractData(
                        it.position,
                        it.productModel
                    )
                )
            })
    }

    private fun requestFistPage() {
        lifecycleScope.launch {
            homeViewModel.getProductListStream().collect {
                adapter.run {
                    submitData(it)
                }
            }
        }
    }

    fun listUpdate() {
        adapter.notifyDataSetChanged()
    }
}
