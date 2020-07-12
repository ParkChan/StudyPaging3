package com.chan.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.chan.R
import com.chan.common.base.BaseFragment
import com.chan.databinding.FragmentHomeBinding
import com.chan.ui.detail.ProductDetailActivityContract
import com.chan.ui.detail.ProductDetailContractData
import com.chan.ui.home.adapter.ProductListAdapter
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

    private val activityResultLauncher: ActivityResultLauncher<ProductDetailContractData> =
        registerForActivityResult(
            ProductDetailActivityContract()
        ) { result: ProductDetailContractData ->
            (binding.rvProduct.adapter as ProductListAdapter).notifyItemChanged(result.position)
            Logger.d("registerForActivityResult >>> position is ${result.position} ")
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Logger.d("onViewCreated() >>> ")

        initViewModel()
        iniViewModelObserve()
        requestFistPage()
    }

    private fun initViewModel() {
        binding.homeViewModel = homeViewModel
        binding.rvProduct.adapter = ProductListAdapter(binding.homeViewModel as HomeViewModel)
    }

    private fun iniViewModelObserve() {
//        binding.homeViewModel?.productListModel?.observe(
//            viewLifecycleOwner,
//            Observer {
//                Logger.d("homeViewModel observe listData $it")
//            })
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
                (binding.rvProduct.adapter as ProductListAdapter).run {
                    submitData(it)
                }
            }
        }
    }

    fun listUpdate(model: ProductModel) {
        (binding.rvProduct.adapter as ProductListAdapter).notifyDataSetChanged()
    }
}
