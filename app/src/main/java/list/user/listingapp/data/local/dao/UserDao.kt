package list.user.listingapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import list.user.listingapp.data.local.entity.UserInfoEntity

@Dao
interface UserDao {

    /**
     * insert user to db
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(userInfo: List<UserInfoEntity>)

    /**
     * select limited search user from db
     */
    @Query("SELECT * FROM USERINFO WHERE firstName LIKE '%' || :search || '%' LIMIT :count")
    suspend fun getUserSearchList(count:Int, search: String): List<UserInfoEntity>?

    /**
     * get total size of user records from db
     */
    @Query("SELECT COUNT(uuid) FROM USERINFO WHERE firstName LIKE '%' || :search || '%'")
    suspend fun getUserSearchSize(search: String): Int?

    /*
    * get all userinfo
    * */
    @Query("SELECT * FROM USERINFO")
    fun getAllUserList(): PagingSource<Int, UserInfoEntity>

    /*
    * delete all users
    * */
    @Query("DELETE FROM USERINFO")
    suspend fun clearAllUsers()
}