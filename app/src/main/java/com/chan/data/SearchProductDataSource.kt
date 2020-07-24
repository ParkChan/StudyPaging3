package com.chan.data

import androidx.paging.PagingSource
import com.chan.data.response.mapToModel
import com.chan.network.api.GoodChoiceApi
import com.chan.ui.home.model.ProductModel
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SearchProductDataSource @Inject constructor(
    private val goodChoiceApi: GoodChoiceApi
) : PagingSource<Int, ProductModel>() {

    private val initialPageIndex: Int = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductModel> {
        val position = params.key ?: initialPageIndex
        return try {
            val response = goodChoiceApi.getProductListAsync(page = position)
            val data = response.body()?.data.mapToModel()
            LoadResult.Page(
                data = data.productList,
                prevKey = if (position == initialPageIndex) null else position - 1,
                nextKey = if (data.productList.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}