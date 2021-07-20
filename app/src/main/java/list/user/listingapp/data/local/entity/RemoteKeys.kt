package list.user.listingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "remotekeys")
data class RemoteKeys(
    @PrimaryKey val uuid: String,
    val prevKey: Int?,
    val nextKey: Int?
)