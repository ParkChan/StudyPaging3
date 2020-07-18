package com.chan.ui.home

import android.os.Bundle
import android.util.ArrayMap
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.chan.BR
import com.chan.R
import com.chan.common.base.BaseFragment
import com.chan.common.base.BaseListAdapter
import com.chan.common.base.BaseViewModel
import com.chan.databinding.FragmentHomeBinding
import com.chan.ui.detail.ProductDetailActivityContract
import com.chan.ui.detail.ProductDetailContractData
import com.chan.ui.home.adapter.ProductDiffer
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
        initAdapter()
        iniViewModelObserve()
        requestFistPage()
    }

    private fun initViewModel() {
        binding.homeViewModel = homeViewModel
    }

    private fun initAdapter() {
        val viewModels = ArrayMap<Int, BaseViewModel>().apply {
            this[BR.homeViewModel] = homeViewModel
        }

        adapter = BaseListAdapter(
            layoutBindingId = Triple(R.layout.item_product, BR.productModel, BR.itemPosition),
            viewModels = viewModels,
            diffCallback = ProductDiffer()
        )

        adapter.addLoadStateListener { loadState ->
            /*
             * loadState.refresh - represents the load state for loading the PagingData for the first time.
             * loadState.prepend - represents the load state for loading data at the start of the list.
             * loadState.append - represents the load state for loading data at the end of the list.
             *
             */
            if (loadState.refresh is LoadState.Loading ||
                loadState.append is LoadState.Loading
            ) {
                showProgressBar(true)
            } else {
                showProgressBar(false)

                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                errorState?.let {
                    Toast.makeText(context, it.error.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.rvProduct.adapter = adapter


    }

    private fun iniViewModelObserve() {

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

    private fun showProgressBar(display: Boolean) {
        binding.progressLoading.run {
            if (!display)
                this.visibility = View.GONE
            else
                this.visibility = View.VISIBLE
        }
    }

}
