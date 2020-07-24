package com.chan.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import javax.inject.Inject

class SearchProductRepository @Inject constructor(
    private val searchProductDataSource: SearchProductDataSource
) {

    fun getProductList() = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { searchProductDataSource })
}