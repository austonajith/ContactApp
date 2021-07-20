package list.user.listingapp.repo

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import list.user.listingapp.USERS_PER_PAGE
import list.user.listingapp.data.local.LocalHelper
import list.user.listingapp.data.local.entity.UserInfoEntity
import list.user.listingapp.data.model.UserModel
import list.user.listingapp.data.remote.api.ApiHelper
import list.user.listingapp.db.UserDatabase
import list.user.listingapp.paging.UsersMediator
import javax.inject.Inject

class MainRepository @Inject constructor(
    var apiHelper: ApiHelper,
    var localHelper: LocalHelper,
    var db: UserDatabase
) {
    suspend fun getRandomUsers(pg: Int) = apiHelper.getRandomUsers(pg)
    suspend fun insertUsersLocal(userList: List<UserInfoEntity>) = localHelper.insetUsers(userList)
    suspend fun getRandomUsersLocal(count: Int, search: String) = localHelper.getUserListFromLocal(count, search)
    suspend fun getWeather(url: String, lat:Double, lon:Double) = apiHelper.getWeather(url, lat, lon)

    @ExperimentalPagingApi
    fun getUsersViaMediator(): Flow<PagingData<UserInfoEntity>> {
        val paginSourceFactory = {db.userDao().getAllUserList()}
        val remoteMediator = UsersMediator(this,db)
        val config = PagingConfig(pageSize = USERS_PER_PAGE, enablePlaceholders = false)
        return Pager(
            config = config,
            pagingSourceFactory = paginSourceFactory,
            remoteMediator = remoteMediator
        ).flow
    }

}