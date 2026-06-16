package com.example.otnet.data.api

import com.example.otnet.data.models.ChildrenResponse
import com.example.otnet.data.models.Content
import com.example.otnet.data.models.ContentPage
import com.example.otnet.data.models.DrmSessionRequest
import com.example.otnet.data.models.DrmSessionResponse
import com.example.otnet.data.models.GenreNode
import com.example.otnet.data.models.HomepageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OTNetService {
    @GET("catalog/homepage")
    suspend fun homepage(): HomepageResponse

    @GET("catalog/content/{id}")
    suspend fun contentDetail(@Path("id") id: String): Content

    @GET("catalog/content/{id}/children")
    suspend fun contentChildren(@Path("id") id: String): ChildrenResponse

    @GET("catalog/content/category/{id}")
    suspend fun contentByCategory(
        @Path("id") id: String,
        @Query("page") page: Int = 1,
    ): ContentPage

    @GET("catalog/categories/tree")
    suspend fun categoriesTree(): List<GenreNode>

    @POST("playback/drm/session")
    suspend fun drmSession(@Body request: DrmSessionRequest): DrmSessionResponse
}
