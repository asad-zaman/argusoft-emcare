package com.argusoft.who.emcare.ui.common.base

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.argusoft.who.emcare.BuildConfig
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.auth.login.LoginViewModel
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.utils.common.UnauthorizedAccess
import com.argusoft.who.emcare.utils.extention.getDeviceModel
import com.argusoft.who.emcare.utils.extention.getDeviceName
import com.argusoft.who.emcare.utils.extention.getDeviceOS
import com.argusoft.who.emcare.utils.extention.getDeviceUUID
import com.argusoft.who.emcare.utils.extention.handleListApiView
import com.argusoft.who.emcare.utils.extention.hideKeyboard
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.extention.onViewBinding
import com.argusoft.who.emcare.utils.extention.showSnackBar
import com.argusoft.who.emcare.utils.extention.showToast
import com.argusoft.who.emcare.utils.extention.whenInProgress
import com.argusoft.who.emcare.utils.extention.whenLoading
import com.argusoft.who.emcare.widget.ApiViewStateConstraintLayout
import com.google.android.fhir.sync.SyncJobStatus
import com.google.android.material.snackbar.Snackbar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


abstract class BaseFragment<B : ViewBinding> : Fragment(), View.OnClickListener {

    @Inject
    lateinit var preference: Preference
    private var _binding: B? = null
    private val syncViewModel: SyncViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    protected val binding
        get() = _binding
            ?: throw RuntimeException("Should only use binding after onCreateView and before onDestroyView")

    abstract fun initView()
    abstract fun initListener()
    abstract fun initObserver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = onViewBinding(inflater, container)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initObserver()
    }

    fun initObserverSync(progressLayout: ApiViewStateConstraintLayout, isRedirectToHome: Boolean) {
        observeNotNull(syncViewModel.syncState) { apiResponse ->

            apiResponse.whenLoading {
                if (preference.getFacilityId().isNotEmpty())
                    progressLayout.showHorizontalProgress(true)
            }

            apiResponse.whenInProgress {
                Log.d("it.total", it.first.toDouble().toString())
                Log.d("it.progress", it.second.toDouble().toString())
                if (it.second >= 100) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        progressLayout.updateProgressUi(true, true)
                        loginViewModel.addDevice(
                            getDeviceName(),
                            getDeviceOS(),
                            getDeviceModel(),
                            requireContext().getDeviceUUID().toString(),
                            BuildConfig.VERSION_NAME
                        )
                        if(isRedirectToHome) {
                            startActivity(Intent(requireContext(), HomeActivity::class.java))
                            requireActivity().finish()
                        }
                    }, 5000)
                } else if (it.first > 0 && it.second <= 100) {
                    val progress = it.second
                    "Synced $progress%".also {
                        progressLayout.showProgress(it)
                        Log.d("Synced", "$progress%")
                    }
                } else if (it.first == 0) {
                    if(isRedirectToHome) {
                        if (preference.getFacilityId().isNotEmpty()) {
                            progressLayout.updateProgressUi(true, true)
                            startActivity(Intent(requireContext(), HomeActivity::class.java))
                            requireActivity().finish()
                        } else {
                            progressLayout.hideProgressUi()
                        }
                    }else{
                        progressLayout.updateProgressUi(true, true)
                    }
                }
            }

            apiResponse.handleListApiView(progressLayout) {
                when (it) {
                    is SyncJobStatus.Failed -> {
                        progressLayout.showContent()
                        progressLayout.hideProgressUi()
                        requireContext().showSnackBar(
                            view = progressLayout,
                            message = getString(R.string.msg_sync_failed),
                            duration = Snackbar.LENGTH_SHORT,
                            isError = true
                        )
                        if(isRedirectToHome) {
                            startActivity(Intent(requireContext(), HomeActivity::class.java))
                            requireActivity().finish()
                        }
                    }
                }
            }
        }
    }

    fun Toolbar.setTitleAndBack(id: String? = null) {
        id?.let {
            title = it
        }
        setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        setNavigationIcon(R.drawable.ic_back)
    }

    fun Toolbar.setTitleDashboard(id: String?){
        inflateMenu(R.menu.dashboard)
        id?.let {
            title = it
        }
    }

    fun Toolbar.setTitleSidepane(id: String? = null){
        id?.let {
            title = it
        }
        setNavigationOnClickListener {
            (activity as? HomeActivity)?.toggleSidepane()
        }
        setNavigationIcon(R.drawable.ic_menu)
    }

    fun Toolbar.setTitleDashboardSidepane(id: String?) {
        id?.let {
            title = it
        }
        inflateMenu(R.menu.dashboard)
        setNavigationOnClickListener {
            (activity as? HomeActivity)?.toggleSidepane()
        }
        setNavigationIcon(R.drawable.ic_menu)
    }


    override fun onClick(view: View?) {
        hideKeyboard(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onEvent(unauthorizedAccess: UnauthorizedAccess) {
        context.showToast(messageResId = R.string.msg_session_expired)
        (activity as? BaseActivity<*>)?.logout()
    }

    fun Application.isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }
}