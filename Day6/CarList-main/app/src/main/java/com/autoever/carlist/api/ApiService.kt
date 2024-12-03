package com.autoever.carlist.api

import com.autoever.carlist.Car
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("cars") // API 엔드포인트를 여기에 입력합니다.
    suspend fun getList(): Response<List<Car>>

    @POST("cars")
    suspend fun create(@Body line: Car): Response<Car>

    @PUT("cars/{id}")
    suspend fun update(@Path("id") id: String, @Body line: Car): Response<Car>

    @DELETE("cars/{id}")
    suspend fun delete(@Path("id") id: String): Response<Unit>
}