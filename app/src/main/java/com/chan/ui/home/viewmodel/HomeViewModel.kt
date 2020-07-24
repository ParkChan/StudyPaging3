package com.chan.ui.home.viewmodel

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.chan.common.base.BaseViewModel
import com.chan.data.SearchProductRepository
import com.chan.db.BookmarkRepository
import com.chan.db.DataBaseResult
import com.chan.ui.bookmark.model.BookmarkModel
import com.chan.ui.detail.ProductDetailContractData
import com.chan.ui.home.model.ProductModel
import com.orhanobut.logger.Logger
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val searchProductRepository: SearchProductRepository,
    private val bookmarkRepository: BookmarkRepository
) : BaseViewModel() {

    private val _productItemSelected = MutableLiveData<ProductDetailContractData>()
    val productItemSelected get() = _productItemSelected

    fun getProductListStream(): Flow<PagingData<ProductModel>> =
        searchProductRepository.getProductList().flow.cachedIn(viewModelScope)

    //상품 상세화면으로 이동
    fun startProductDetailActivity(position: Int, productModel: ProductModel) {
        _productItemSelected.value = ProductDetailContractData(position, productModel)
    }

    fun isBookMark(
        context: Context,
        productModel: ProductModel,
        onResult: (isBookMark: Boolean) -> Unit
    ) = viewModelScope.launch {
        val bookmarkResult = async {
            bookmarkRepository.isExists(context, productModel)
        }

        when (val dbResult = bookmarkResult.await()) {
            is DataBaseResult.Success -> onResult(dbResult.data)
            is DataBaseResult.Failure -> Logger.d(dbResult.exception.message ?: "")
        }
    }

    fun onClickBookMark(context: Context, productModel: ProductModel) {
        isBookMark(context, productModel, onResult = {
            viewModelScope.launch {
                if (it) {
                    bookmarkRepository.deleteBookMark(
                        context,
                        convertToBookMarkModel(productModel)
                    )
                } else {
                    bookmarkRepository.insertBookMark(
                        context,
                        convertToBookMarkModel(productModel)
                    )
                }
            }
        })
    }

    private fun convertToBookMarkModel(model: ProductModel): BookmarkModel {
        return BookmarkModel(
            id = model.id,
            name = model.name,
            thumbnail = model.thumbnail,
            imagePath = model.descriptionModel.imagePath,
            subject = model.descriptionModel.subject,
            price = model.descriptionModel.price,
            rate = model.rate,
            regTimeStamp = System.currentTimeMillis()
        )
    }
}