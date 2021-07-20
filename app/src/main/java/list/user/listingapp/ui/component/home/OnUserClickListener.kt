package list.user.listingapp.ui.component.home

import list.user.listingapp.data.local.entity.UserInfoEntity
import list.user.listingapp.databinding.RowUserAdapterBinding

interface OnUserClickListener {
    fun onUserClicked(binding: RowUserAdapterBinding, model: UserInfoEntity?)
}