package list.user.listingapp.ui.component.users

import list.user.listingapp.data.local.entity.UserInfoEntity
import list.user.listingapp.databinding.RowUserAdapterBinding

interface OnUserClickListener {
    fun onUserClicked(binding: RowUserAdapterBinding, model: UserInfoEntity?)
}