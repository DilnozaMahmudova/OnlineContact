package uz.dilnoza.onlinecontact.source.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient{
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(" http://161.35.73.101:88/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}