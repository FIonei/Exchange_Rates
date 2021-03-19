package com.example.exchangerates

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface APIService {
    @GET("/api/v1/employees")
    suspend fun getEmployees(): Response<ResponseBody>
}