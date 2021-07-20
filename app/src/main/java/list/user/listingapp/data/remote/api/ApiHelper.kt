package list.user.listingapp.data.remote.api

import list.user.listingapp.data.model.UserModel
import list.user.listingapp.data.model.WeatherModel
import retrofit2.Response

interface ApiHelper {
    suspend fun getRandomUsers(pg:Int): Response<UserModel>
    suspend fun getWeather(url:String, lat:Double, lon: Double): Response<WeatherModel>
}