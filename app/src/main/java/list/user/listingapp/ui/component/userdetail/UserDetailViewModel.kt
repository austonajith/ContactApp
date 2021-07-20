package list.user.listingapp.ui.component.userdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import list.user.listingapp.USERS_PER_PAGE
import list.user.listingapp.WEATHER_API_URL
import list.user.listingapp.data.local.entity.UserInfoEntity
import list.user.listingapp.data.model.WeatherModel
import list.user.listingapp.data.remote.network.Resource
import list.user.listingapp.paging.UserDataPagingSource
import list.user.listingapp.repo.MainRepository
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {

    val weatherResoponseLiveData = MutableLiveData<Resource<WeatherModel>>()

    fun getWeather(lat:Double, lon:Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                weatherResoponseLiveData.postValue(Resource.loading(null))
                val result = mainRepository.getWeather(WEATHER_API_URL,lat, lon)
                result.apply {
                    if (isSuccessful) {
                        weatherResoponseLiveData.postValue(Resource.success(body()))
                    } else {
                        weatherResoponseLiveData.postValue(Resource.error(errorBody().toString(), null))
                    }
                }
            }catch (e: Exception){
                Timber.e(e)
                weatherResoponseLiveData.postValue(Resource.error(e.message.toString(),null))
            }

        }
    }
}