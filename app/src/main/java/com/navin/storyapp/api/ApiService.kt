package com.navin.storyapp.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

data class LoginRequest(
    @field:SerializedName("email")
    val email: String? = null,
    @field:SerializedName("password")
    val password: String? = null
)

data class LoginResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null,
    @field:SerializedName("loginResult")
    val loginResult: LoginResult
)

data class LoginResult(
    @field:SerializedName("userId")
    val userId: String? = null,
    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("token")
    val token: String? = null
)

data class RegisterRequest(
    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("email")
    val email: String? = null,
    @field:SerializedName("password")
    val password: String? = null
)

data class RegisterResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null
)

data class StoryResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null,
    @field:SerializedName("listStory")
    val listStory: List<Story>
) {
    data class Story(
        @field:SerializedName("id")
        val id: String? = null,
        @field:SerializedName("name")
        val name: String? = null,
        @field:SerializedName("description")
        val description: String? = null,
        @field:SerializedName("photoUrl")
        val photoUrl: String? = null,
        @field:SerializedName("createdAt")
        val createdAt: String,
        @field:SerializedName("lat")
        val lat: Double? = null,
        @field:SerializedName("lon")
        val lon: Double? = null

    )
}

data class UploadResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String
)

interface ApiService {

    @POST("/v1/login")
    fun loginUser(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("/v1/register")
    fun registerUser(
        @Body request: RegisterRequest
    ): Call<RegisterResponse>

    @GET("/v1/stories")
    suspend fun getStoryPaging(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): StoryResponse

    @GET("/v1/stories")
    fun getAllStoryWithLocation(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ): Call<StoryResponse>

    @Multipart
    @POST("/v1/stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?
    ): Call<UploadResponse>
}

