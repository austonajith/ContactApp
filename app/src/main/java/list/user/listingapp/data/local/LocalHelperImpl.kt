package list.user.listingapp.data.local

import androidx.paging.PagingSource
import list.user.listingapp.data.local.dao.RemoteKeysDao
import list.user.listingapp.data.local.dao.UserDao
import list.user.listingapp.data.local.entity.RemoteKeys
import list.user.listingapp.data.local.entity.UserInfoEntity
import javax.inject.Inject

class LocalHelperImpl @Inject constructor(
    private val userDao: UserDao,
    private val remoteKeysDao: RemoteKeysDao
) : LocalHelper {

    override suspend fun insetUsers(usersList: List<UserInfoEntity>) {
        return userDao.insertUsers(usersList)
    }

    override suspend fun getUserListFromLocal(count: Int, search: String): List<UserInfoEntity>? {
        return userDao.getUserSearchList(count, search)
    }

    override suspend fun getUserListSizeFromLocal(search: String): Int? {
        return userDao.getUserSearchSize(search)
    }

    override fun getAllUserListFromLocal(): PagingSource<Int, UserInfoEntity>? {
        return userDao.getAllUserList()
    }

    override suspend fun clearAllUsers() {
        return userDao.clearAllUsers()
    }

    override suspend fun insertAllRemoteKeys(remoteKey: List<RemoteKeys>) {
        return remoteKeysDao.insertAll(remoteKey)
    }

    override suspend fun getRemoteKeysUuId(id: String): RemoteKeys? {
        return remoteKeysDao.remoteKeysUuId(id)
    }

    override suspend fun clearRemoteKeys() {
        return remoteKeysDao.clearRemoteKeys()
    }
}