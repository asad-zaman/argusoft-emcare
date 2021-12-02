package com.argusoft.who.emcare.ui.home

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentHomeBinding
import com.argusoft.who.emcare.ui.common.ImagePickerDialog
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.utils.common.ImagePickerUtils
import com.argusoft.who.emcare.utils.extention.handleListApiView
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.extention.showSnackBar
import com.argusoft.who.emcare.utils.glide.GlideApp
import com.argusoft.who.emcare.utils.glide.GlideRequests
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var glideRequests: GlideRequests
    private lateinit var homeAdapter: HomeAdapter

    val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        ImagePickerUtils.onActivityResult(ImagePickerUtils.REQ_TAKE_PHOTO_URI, it.resultCode, it.data)
    }
    val chooseGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        ImagePickerUtils.onActivityResult(ImagePickerUtils.REQ_CHOOSE_GALLERY, it.resultCode, it.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequests = GlideApp.with(this)
        homeAdapter = HomeAdapter(glideRequests = glideRequests, onClickListener = this)
        homeViewModel.getRepository()
    }

    override fun initView() {
        binding.rootLayout.recyclerView = binding.recyclerView
        binding.rootLayout.swipeRefreshLayout = binding.swipeRefreshLayout
        binding.rootLayout.recyclerView?.adapter = homeAdapter
        /*  rootLayout.setupRecyclerView {
              layoutManager = LinearLayoutManager(context)
              adapter = listAdapter
          }
          rootLayout.setupSwipeRefreshLayout {
              setColorSchemeResources(R.color.colorAccent)
          }*/
        binding.rootLayout.setOnSwipeRefreshLayout {
            homeViewModel.getRepository(isRefresh = true)
        }
        binding.rootLayout.setOnLoadMore {
            homeViewModel.getRepository(isLoadMore = true)
        }
    }

    override fun initListener() {
        /*
        //Navigate screen example
        navigate(R.id.action_homeFragment_to_detailFragment){
            // Add bundle arguments
        }
        */
    }

    override fun initObserver() {
        observeNotNull(homeViewModel.apiState) {
            it.handleListApiView(binding.rootLayout, onClickListener = { homeViewModel.getRepository() }) {

            }
        }
        /*
        //For login & register api observer example
        observeNotNull(listViewModel.apiState) {
            it.handleApiView(rootLayout, View.OnClickListener { listViewModel.getRepository() }) {

            }
        }
        */
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.thumbnail -> {
                ImagePickerDialog.newInstance(object : ImagePickerUtils.OptionSelect {
                    override fun cameraClick() {
                        ImagePickerUtils.selectFromCamera(
                            this@HomeFragment,
                            takePhotoLauncher,
                            imageSelector,
                            binding.rootLayout
                        )
                    }

                    override fun galleryClick() {
                        ImagePickerUtils.selectFromGallery(
                            this@HomeFragment,
                            chooseGalleryLauncher,
                            imageSelector,
                            binding.rootLayout
                        )
                    }

                }, false).show(childFragmentManager, ImagePickerDialog::class.java.simpleName)
            }
        }
    }

    private val imageSelector = object : ImagePickerUtils.ImageSelector {
        override fun onImageSelected(path: String) {
            binding.root.context.showSnackBar(binding.root, "File path = $path")
        }

        override fun onShowProgress() {
            binding.rootLayout.showHorizontalProgress()
        }

        override fun onHideProgress() {
            binding.rootLayout.showContent()
        }

        override fun onMultipleImageSelected(paths: List<String>) {
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ImagePickerUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}