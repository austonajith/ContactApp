package list.user.listingapp.data.remote.api

import list.user.listingapp.data.model.UserModel
import list.user.listingapp.data.model.WeatherModel
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl
@Inject
constructor(private val apiService: ApiService): ApiHelper {

    override suspend fun getRandomUsers(pg: Int): Response<UserModel> {
       return apiService.getRandomUser(pg)
    }

    override suspend fun getWeather(url: String, lat: Double, lon: Double): Response<WeatherModel> {
        return apiService.getWeather(url,lat,lon)
    }
}