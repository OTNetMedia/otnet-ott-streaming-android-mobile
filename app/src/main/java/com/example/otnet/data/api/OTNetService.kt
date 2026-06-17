package com.example.otnet.data.api

import com.example.otnet.data.models.ChannelsResponse
import com.example.otnet.data.models.ChildrenResponse
import com.example.otnet.data.models.Content
import com.example.otnet.data.models.ContentPage
import com.example.otnet.data.models.DeviceHelloRequest
import com.example.otnet.data.models.DeviceHelloResponse
import com.example.otnet.data.models.DeviceProgressRequest
import com.example.otnet.data.models.DeviceProgressResponse
import com.example.otnet.data.models.EpgResponse
import com.example.otnet.data.models.GenreNode
import com.example.otnet.data.models.HomepageResponse
import com.example.otnet.data.models.LiveMintRequest
import com.example.otnet.data.models.PublisherSettings
import com.example.otnet.data.models.VodMintRequest
import com.example.otnet.data.models.VodMintResponse
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

    @GET("catalog/content")
    suspend fun searchContent(
        @Query("search") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 24,
    ): ContentPage

    @GET("catalog/categories/tree")
    suspend fun categoriesTree(): List<GenreNode>

    @GET("catalog/settings")
    suspend fun settings(): PublisherSettings

    @GET("catalog/channels")
    suspend fun channels(): ChannelsResponse

    @GET("catalog/epg")
    suspend fun epg(
        @Query("channelId") channelId: String? = null,
        @Query("back") backHours: Int? = null,
        @Query("ahead") aheadHours: Int? = null,
    ): EpgResponse

    @POST("playback/vod/mint")
    suspend fun vodMint(@Body request: VodMintRequest): VodMintResponse

    @POST("playback/live/{channelId}/mint")
    suspend fun liveMint(
        @Path("channelId") channelId: String,
        @Body request: LiveMintRequest,
    ): VodMintResponse

    @POST("device/hello")
    suspend fun deviceHello(@Body request: DeviceHelloRequest): DeviceHelloResponse

    @POST("device/progress")
    suspend fun postDeviceProgress(@Body request: DeviceProgressRequest): DeviceHelloResponse

    @GET("device/progress")
    suspend fun getDeviceProgress(@Query("deviceId") deviceId: String): DeviceProgressResponse
}
