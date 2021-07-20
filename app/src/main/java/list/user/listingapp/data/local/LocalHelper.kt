package list.user.listingapp.data.local

import androidx.paging.PagingSource
import list.user.listingapp.data.local.entity.RemoteKeys
import list.user.listingapp.data.local.entity.UserInfoEntity

interface LocalHelper {

    suspend fun insetUsers(usersList: List<UserInfoEntity>)
    suspend fun getUserListFromLocal(count: Int, search: String): List<UserInfoEntity>?
    suspend fun getUserListSizeFromLocal(search: String): Int?
    fun getAllUserListFromLocal(): PagingSource<Int, UserInfoEntity>?
    suspend fun clearAllUsers()
    suspend fun insertAllRemoteKeys(remoteKey: List<RemoteKeys>)
    suspend fun getRemoteKeysUuId(id: String): RemoteKeys?
    suspend fun clearRemoteKeys()
}