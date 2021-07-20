package list.user.listingapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import list.user.listingapp.USERS_PER_PAGE
import list.user.listingapp.data.local.entity.UserInfoEntity
import list.user.listingapp.data.model.UserModel
import list.user.listingapp.repo.MainRepository
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject
import kotlin.math.ceil

class UserDataPagingSource (private val mainRepository: MainRepository, private val search:String) :
    PagingSource<Int, UserInfoEntity>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserInfoEntity> {
        try {

            val position = params.key ?: INITIAL_PAGE_INDEX
            val totalSize = mainRepository.localHelper.getUserListSizeFromLocal(search)?.toDouble()?:0.toDouble()
            val nextLoadSize = params.loadSize*position
            val lastPage = ceil(totalSize/USERS_PER_PAGE)
            val randomUsers =
                mainRepository.localHelper.getUserListFromLocal(nextLoadSize,search) ?: throw Exception(
                    "No users found"
                )

            return LoadResult.Page(
                data = randomUsers,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (position+1>lastPage) null else position + 1
            )


        } catch (e: Exception) {
            Timber.e(e)
            return LoadResult.Error(Exception(e))
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

    override fun getRefreshKey(state: PagingState<Int, UserInfoEntity>): Int? {
        return state.anchorPosition
    }


    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}