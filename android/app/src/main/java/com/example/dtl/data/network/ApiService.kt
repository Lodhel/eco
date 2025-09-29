package com.example.dtl.data.network

import com.example.dtl.data.network.model.AnalysisResult
import com.example.dtl.data.network.model.OrderResultData
import com.example.dtl.data.network.model.PlantDetails
import com.example.dtl.data.network.model.Response
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

const val auth_token = 	"Bearer 123456"

interface ApiService {

    @Multipart
    @POST("orders/")
    suspend fun definePlant(
        @Header("Authorization") token: String = auth_token,
        @Part file: MultipartBody.Part,
        @Query("title") title: String = "",
    ): Response<OrderResultData>

    @GET("orders/")
    suspend fun getOrders(
        @Header("Authorization") token: String = auth_token,
        @Query("start") start: Int = 0,
        @Query("limit") limit: Int = 20,
        @Query("order_by") order_by: String = "id_asc",
    ): Response<OrderResultData>

    @GET("orders/{order_id}/")
    suspend fun getOrderById(
        @Header("Authorization") token: String = auth_token,
        @Path("order_id") id: Int,
    ): Response<OrderResultData>

    @GET("result-orders/{order_id}/")
    suspend fun getOrderAnalysisById(
        @Header("Authorization") token: String = auth_token,
        @Path("order_id") order_id: Int,
    ): Response<AnalysisResult>

    @GET("result-orders/{order_id}/{result_id}/")
    suspend fun getPlantDetailsById(
        @Header("Authorization") token: String = auth_token,
        @Path("order_id") order_id: Int,
        @Path("result_id") result_id: Int,
    ): Response<PlantDetails>

    @DELETE("orders/{order_id}/")
    suspend fun deleteOrderById(
        @Header("Authorization") token: String = auth_token,
        @Path("order_id") id: Int,
    )
}