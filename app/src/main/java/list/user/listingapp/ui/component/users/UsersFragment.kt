package list.user.listingapp.ui.component.users

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import list.user.listingapp.data.local.entity.UserInfoEntity
import list.user.listingapp.data.remote.network.Status
import list.user.listingapp.databinding.FragmentUsersBinding
import list.user.listingapp.databinding.RowUserAdapterBinding
import list.user.listingapp.ui.component.home.OnUserClickListener
import list.user.listingapp.ui.component.home.UsersAdapter
import timber.log.Timber


@AndroidEntryPoint
@ExperimentalPagingApi
class UsersFragment : Fragment() {

    private val viewModel: UsersViewModel by viewModels()
    private lateinit var binding: FragmentUsersBinding
    private var isLocalSearchEnable = false
    private var adapter: UsersAdapter? = null
    private var client: FusedLocationProviderClient?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        getUsersViaRemoteMediator()
        fetchLocation()
        setupObservable()
    }

    private fun setupObservable() {
        viewModel.weatherResoponseLiveData.observe(viewLifecycleOwner, {
            when(it.status){
                Status.LOADING ->{}
                Status.SUCCESS ->{
                    it.data?.let {model->
                        if (model.weather.isNullOrEmpty()) return@observe
                        model.weather?.get(0)?.apply {
                            val iconCode = icon
                            iconCode?.let {
                                val icon = "https://openweathermap.org/img/w/$iconCode.png"
                                Glide.with(requireContext()).load(icon).into(binding.ivWeather)
                                binding.tvWeatherDesc.text = main
                                binding.tvTemperature.text = getCelsius(model.main?.temp)
                            }
                        }


                    }
                }
                Status.ERROR ->{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getCelsius(kelvin: Double?): String {
        kelvin?:return "N/A"
        return (kelvin-273.15).toInt().toString()+"\u2103"
    }

    private fun fetchLocation() {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Dexter.withContext(requireContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        requestLocationUpdates()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Timber.d(response.permissionName)
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                }).check()
        }else{
            requestLocationUpdates()
        }
    }

    private fun initViews() {
        setupAdapter()
        binding.rvUsers.apply {
                postponeEnterTransition()
                viewTreeObserver.addOnPreDrawListener {
                    startPostponedEnterTransition()
                    true
                }
        }
        binding.etSearch.addTextChangedListener {
            if (!it.isNullOrEmpty()) {
                isLocalSearchEnable = true
                getUsersLocal(it.toString())
            } else {
                isLocalSearchEnable = false
                getUsersViaRemoteMediator()
            }
        }
    }

    private fun setupAdapter() {
        adapter = UsersAdapter(object : OnUserClickListener {
            override fun onUserClicked(binding: RowUserAdapterBinding, model: UserInfoEntity?) {

                val extras = FragmentNavigatorExtras(
                    binding.ivDp to "ivDp_${model?.uuid?:"123"}",
                    binding.etName to "tvName_${model?.uuid?:"123"}"
                )

                findNavController().navigate(
                    UsersFragmentDirections.actionUserToUserdetail(
                        model ?: return
                    ), extras
                )
            }
        })
        binding.rvUsers.apply {
            layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
            adapter = this@UsersFragment.adapter
        }

        adapter?.addLoadStateListener { state ->
            if (state.refresh is LoadState.Loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                val error = when {
                    state.prepend is LoadState.Error -> state.prepend as LoadState.Error
                    state.append is LoadState.Error -> state.append as LoadState.Error
                    state.refresh is LoadState.Error -> state.refresh as LoadState.Error
                    else -> null
                }
                error?.let {
                    if (!it.error.message.isNullOrEmpty())
                    Toast.makeText(context, it.error.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun getUsersLocal(search: String) {
        lifecycleScope.launch {
            viewModel.getUsersLocal(search).collectLatest {
                adapter?.submitData(it)
            }
        }

    }

    private fun getUsersViaRemoteMediator() {
        lifecycleScope.launch {
            viewModel.getUserDataViaMediator().collectLatest {
                if (isLocalSearchEnable) return@collectLatest
                adapter?.submitData(it)
            }
        }
    }

    private fun requestLocationUpdates() {
        val request = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime= 100
        }

        client =  LocationServices.getFusedLocationProviderClient(requireContext())
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client?.requestLocationUpdates(request, locationListener , null)
        }
    }

    var locationListener = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            location.let {
                val latitude = location.latitude
                val longitude = location.longitude
                viewModel.getWeather(latitude, longitude)
                client?.removeLocationUpdates(this)
            }

        }
    }

}