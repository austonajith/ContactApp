package list.user.listingapp.ui.component.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import list.user.listingapp.USERS_PER_PAGE
import list.user.listingapp.data.local.entity.UserInfoEntity
import list.user.listingapp.data.model.UserModel
import list.user.listingapp.data.remote.network.Resource
import list.user.listingapp.paging.UserDataPagingSource
import list.user.listingapp.repo.MainRepository
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {


    fun getUsersLocal(search: String):Flow<PagingData<UserInfoEntity>> {
        return Pager(PagingConfig(pageSize = USERS_PER_PAGE)) {
            UserDataPagingSource(mainRepository, search)
        }.flow
            .cachedIn(viewModelScope)
    }


    @ExperimentalPagingApi
    fun getUserDataViaMediator(): Flow<PagingData<UserInfoEntity>>{
        return mainRepository.getUsersViaMediator().cachedIn(viewModelScope)
    }
}