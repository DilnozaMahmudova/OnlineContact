package uz.dilnoza.onlinecontact.source.retrofit

import retrofit2.Call
import retrofit2.http.*
import uz.dilnoza.onlinecontact.source.room.entity.ContactData


interface ContactApi {
    /**
     * 1. Get all contacts
     * */
    @GET("contact")
    fun getAll(): Call<ResponseData<List<ContactData>>>

    /**
     * 2. add new a contact
     * */
    @POST("contact")
    fun add(@Body contactData: ContactData): Call<ResponseData<ContactData>>

    /**
     * 3. remove a contact
     * */
    @HTTP(method = "DELETE", path = "contact", hasBody = true)
    fun remove(@Body contactData: ContactData): Call<ResponseData<ContactData>>

    @PUT("contact")
    fun update(@Body contactData: ContactData): Call<ResponseData<ContactData>>
}