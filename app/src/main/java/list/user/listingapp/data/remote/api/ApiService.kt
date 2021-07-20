package list.user.listingapp.data.remote.api

import list.user.listingapp.PAGING_SEED
import list.user.listingapp.USERS_PER_PAGE
import list.user.listingapp.WEATHER_API_KEY
import list.user.listingapp.data.model.UserModel
import list.user.listingapp.data.model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET(".")
    suspend fun getRandomUser(
        @Query("page") pg: Int,
        @Query("results") result: Int?= USERS_PER_PAGE,
        @Query("seed") seed: String?= PAGING_SEED
    ): Response<UserModel>

    @GET
    suspend fun getWeather(
        @Url url : String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String?= WEATHER_API_KEY,
    ): Response<WeatherModel>
}