package list.user.listingapp.paging

import android.net.Uri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import list.user.listingapp.data.local.entity.RemoteKeys
import list.user.listingapp.data.local.entity.UserInfoEntity
import list.user.listingapp.data.model.UserModel
import list.user.listingapp.db.UserDatabase
import list.user.listingapp.repo.MainRepository
import timber.log.Timber
import java.io.InvalidObjectException
import java.lang.Exception

@ExperimentalPagingApi
class UsersMediator(private val mainRepository: MainRepository, private val db: UserDatabase) :
    RemoteMediator<Int, UserInfoEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserInfoEntity>
    ): MediatorResult {

        try {
            val pagedKeyData = when (loadType) {
                LoadType.REFRESH -> DEFAULT_PAGE_INDEX
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    val remoteKey = db.withTransaction {
                        if (lastItem?.uuid != null) {
                            mainRepository.localHelper.getRemoteKeysUuId(lastItem.uuid)
                        } else null
                    }
                    if (remoteKey?.nextKey == null) {
                        MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey?.nextKey
                }
            }

            val page = pagedKeyData ?: DEFAULT_PAGE_INDEX
            val response = mainRepository.apiHelper.getRandomUsers(page)
            val isEndOfList = response.body()?.results?.isEmpty() ?: true
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    mainRepository.localHelper.clearRemoteKeys()
                    mainRepository.localHelper.clearAllUsers()
                }
                val prevKey = if (page == DEFAULT_PAGE_INDEX) null else page + 1
                val nextKey = if (isEndOfList) null else page + 1
                val body = parseUserList(response.body()!!)!!
                val keys = body.map {
                    RemoteKeys(it.uuid, prevKey, nextKey)
                }
                mainRepository.localHelper.insertAllRemoteKeys(keys)
                mainRepository.localHelper.insetUsers(body)
            }
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }



    private fun parseUserList(userList: UserModel): List<UserInfoEntity>? {
        return userList.results?.map {
            UserInfoEntity(
                it?.login?.uuid ?: return null,
                it.name?.first,
                it.name?.last,
                it.gender,
                it.location?.city,
                it.email,
                it.dob?.date,
                it.phone,
                it.location?.coordinates?.latitude,
                it.location?.coordinates?.longitude,
                it.picture?.thumbnail,
                it.picture?.large
            )
        }

    }

    companion object {
        const val DEFAULT_PAGE_INDEX = 1
    }

}