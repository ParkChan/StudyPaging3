package com.chan.network.api

import com.chan.ui.home.domain.entity.res.MainResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GoodChoiceApi {

    @GET("App/json/{page}.json")
    suspend fun getProductListAsync(
        @Path("page") page: Int
    ): Response<MainResponse>
}