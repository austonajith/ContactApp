package list.user.listingapp.ui.component.userdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation
import list.user.listingapp.data.local.entity.UserInfoEntity
import list.user.listingapp.data.remote.network.Status
import list.user.listingapp.databinding.FragmentUserDetailBinding
import list.user.listingapp.ui.component.home.UsersAdapter
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale




@AndroidEntryPoint
@ExperimentalPagingApi
class UserDetailFragment : Fragment() {

    private val viewModel: UserDetailViewModel by viewModels()
    private val args: UserDetailFragmentArgs by navArgs()
    private var userInfoEntity: UserInfoEntity? = null
    private lateinit var binding: FragmentUserDetailBinding
    var adapter: UsersAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupObservable()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
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

    private fun initViews() {

        with(binding) {
            btnBack.setOnClickListener { activity?.onBackPressed() }
            userInfoEntity = args.userinfo
            if (userInfoEntity?.lat!=null && userInfoEntity?.lon!=null)
            viewModel.getWeather(userInfoEntity?.lat?.toDouble()!!, userInfoEntity?.lon?.toDouble()!!)
            Glide.with(requireContext()).load(userInfoEntity?.img).into(ivDp)
            Glide.with(requireContext()).load(userInfoEntity?.img)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 4)))
                .into(ivDpBLur)
            tvMobile.text = userInfoEntity?.phone
            tvEmail.text = userInfoEntity?.email
            tvDob.text = getFormattedDate(userInfoEntity?.dob)
            tvName.text = userInfoEntity?.firstName+ " "+ userInfoEntity?.lastName
            ViewCompat.setTransitionName(binding.ivDp, "ivDp_${userInfoEntity?.uuid ?: "123"}")
            ViewCompat.setTransitionName(binding.tvName, "tvName_${userInfoEntity?.uuid ?: "123"}")
        }
    }

    private fun getFormattedDate(dob: String?): String {
        try {
            dob?:return ""
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'", Locale.ENGLISH)
            originalFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            val targetFormat = SimpleDateFormat("dd-MMM-yyyy",Locale.getDefault())
            val date = originalFormat.parse(dob)?:return ""
            val formattedDate: String = targetFormat.format(date)
            return formattedDate

        }catch (e:Exception){
            e.printStackTrace()
            return ""
        }

    }


}