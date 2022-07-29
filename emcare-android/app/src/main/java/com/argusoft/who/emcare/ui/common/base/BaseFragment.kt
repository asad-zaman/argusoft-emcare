package com.argusoft.who.emcare.ui.common.base

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.utils.common.UnauthorizedAccess
import com.argusoft.who.emcare.utils.extention.hideKeyboard
import com.argusoft.who.emcare.utils.extention.onViewBinding
import com.argusoft.who.emcare.utils.extention.showToast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


abstract class BaseFragment<B : ViewBinding> : Fragment(), View.OnClickListener {

    @Inject
    lateinit var preference: Preference
    private var _binding: B? = null
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

    fun Toolbar.setTitleAndBack(id: String? = null) {
        id?.let {
            setTitle(it)
        }
        setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    fun Toolbar.setUpDashboard(id: Int? = null) {
        setNavigationIcon(R.drawable.ic_menu)
        inflateMenu(R.menu.dashboard)
        setNavigationOnClickListener {
//            (activity as? HomeActivity)?.openDrawer() TODO: Add sidepane on window click
        }
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