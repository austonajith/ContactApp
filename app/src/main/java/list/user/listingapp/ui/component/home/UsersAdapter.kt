package list.user.listingapp.ui.component.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import list.user.listingapp.data.local.entity.UserInfoEntity
import list.user.listingapp.databinding.RowUserAdapterBinding

class UsersAdapter(var listener: OnUserClickListener) :
    PagingDataAdapter<UserInfoEntity, UsersAdapter.UserViewHolder>(UsersListComparator) {

    var modelList: List<UserInfoEntity>? = null

    class UserViewHolder(private val binding: RowUserAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: UserInfoEntity, listener: OnUserClickListener) {
            binding.apply {

                ViewCompat.setTransitionName (binding.ivDp , "ivDp_${model.uuid?:"123"}")
                ViewCompat.setTransitionName(binding.etName, "tvName_${model.uuid?:"123"}")

                Glide.with(ivDp.context).load(model.thumbnail).into(ivDp)
                clRoot.setOnClickListener {
                    listener.onUserClicked(binding, model)
                }
                etName.text = "${model.firstName} ${model.lastName}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding =
            RowUserAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, listener)
        }

    }

    fun updateAdapter(newModelList: List<UserInfoEntity>?) {
        newModelList ?: return
        modelList = newModelList
        notifyDataSetChanged()
    }

    object UsersListComparator : DiffUtil.ItemCallback<UserInfoEntity>() {
        override fun areItemsTheSame(oldItem: UserInfoEntity, newItem: UserInfoEntity): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: UserInfoEntity, newItem: UserInfoEntity): Boolean {
            return oldItem == newItem
        }

    }
}