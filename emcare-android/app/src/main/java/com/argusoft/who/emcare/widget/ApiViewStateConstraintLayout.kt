package com.argusoft.who.emcare.widget

import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.PorterDuff
import android.os.Build
import android.provider.Settings
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.ViewStateCircularProgrssBinding
import com.argusoft.who.emcare.databinding.ViewStateErrorViewBinding
import com.argusoft.who.emcare.databinding.ViewStateHorizontalLoadingBinding
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import java.util.*

class ApiViewStateConstraintLayout : ConstraintLayout {

    private lateinit var inflater: LayoutInflater
    private val contentViews = ArrayList<View>()
    private var viewState = ViewState.SHOW_CONTENT
    private var circularProgressView: ViewStateCircularProgrssBinding? = null
    private var horizontalProgressView: ViewStateHorizontalLoadingBinding? = null
    private var errorView: ViewStateErrorViewBinding? = null
    private var snackbar: Snackbar? = null
    private var typedArray: TypedArray? = null
    private var isEnableRecyclerView: Boolean = false
    private var isEnableSwipeRefreshLayout: Boolean = false
    var recyclerView: RecyclerView? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var scrollListener: EndlessRecyclerViewScrollListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        initAttribute(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        initAttribute(attributeSet)
    }

    private fun initAttribute(attributeSet: AttributeSet?) {
        inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.ApiViewStateConstraintLayout)

        isEnableSwipeRefreshLayout = typedArray?.getBoolean(R.styleable.ApiViewStateConstraintLayout_enableSwipeRefreshLayout, false) ?: false
        isEnableRecyclerView = typedArray?.getBoolean(R.styleable.ApiViewStateConstraintLayout_enableRecyclerView, false) ?: false

        if (isEnableSwipeRefreshLayout) {
            swipeRefreshLayout = SwipeRefreshLayout(context)
        }

        if (isEnableRecyclerView) {
            recyclerView = RecyclerView(context)
        }

        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ).apply {
            topToTop = ConstraintSet.PARENT_ID
            bottomToBottom = ConstraintSet.PARENT_ID
            startToStart = ConstraintSet.PARENT_ID
            endToEnd = ConstraintSet.PARENT_ID
        }

        swipeRefreshLayout?.let { swipeRefreshLayout ->
            addView(swipeRefreshLayout, layoutParams)
            recyclerView?.let {
                swipeRefreshLayout.addView(it, layoutParams)
            }
        }

        if (!isEnableSwipeRefreshLayout)
            recyclerView?.let {
                addView(it, layoutParams)
            }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        if (child?.tag != ViewState.ERROR_VIEW && child?.tag != ViewState.HORIZONTAL_LOADING &&
            child?.tag != ViewState.PROGRESS_LOADING && !isEnableRecyclerView
        ) {
            child?.let { contentViews.add(it) }
        }
    }

    private fun setContentVisibility(visible: Boolean, skipIds: List<Int> = emptyList()) {
        contentViews.filterNot { skipIds.contains(it.id) }
            .forEach {
                it.isVisible = visible
            }
    }

    fun setupSwipeRefreshLayout(prop: SwipeRefreshLayout.() -> Unit) {
        swipeRefreshLayout?.prop()
    }

    fun setupRecyclerView(prop: RecyclerView.() -> Unit) {
        recyclerView?.prop()
    }

    fun setOnSwipeRefreshLayout(setOnRefreshListener: () -> Unit) {
        swipeRefreshLayout?.setOnRefreshListener {
            setOnRefreshListener()
        }
    }

    fun setOnLoadMore(setOnLoadMore: () -> Unit) {
        val adapter = recyclerView?.adapter as? BaseAdapter<*>
        scrollListener = object : EndlessRecyclerViewScrollListener(recyclerView?.layoutManager as? LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                if (adapter?.isLoadMore() == false && swipeRefreshLayout?.isRefreshing != true)
                    setOnLoadMore()
            }
        }
        scrollListener?.also { recyclerView?.addOnScrollListener(it) }
    }

    fun showContent(skipIds: List<Int> = emptyList()) {
        viewState = ViewState.SHOW_CONTENT
        circularProgressView?.root?.isVisible = false
        horizontalProgressView?.root?.isVisible = false
        errorView?.root?.isVisible = false
        setContentVisibility(true, skipIds)
    }

    fun showProgress(skipIds: List<Int> = emptyList()) {
        viewState = ViewState.PROGRESS_LOADING
        circularProgressView ?: inflateCircularProgressView()
        circularProgressView?.root?.isVisible = true
        horizontalProgressView?.root?.isVisible = false
        errorView?.root?.isVisible = false
        setContentVisibility(false, skipIds)
        typedArray?.getDimensionPixelSize(
            R.styleable.ApiViewStateConstraintLayout_circularProgressBarWidth,
            0
        )?.takeIf { it != 0 }?.let {
            circularProgressView?.circularProgressBar?.layoutParams?.width = it
        }
        typedArray?.getDimensionPixelSize(
            R.styleable.ApiViewStateConstraintLayout_circularProgressBarHeight,
            0
        )?.takeIf { it != 0 }?.let {
            circularProgressView?.circularProgressBar?.layoutParams?.height = it
        }
        typedArray?.getColor(
            R.styleable.ApiViewStateConstraintLayout_circularProgressBarColor,
            0
        )?.takeIf { it != 0 }?.let {
            circularProgressView?.circularProgressBar?.indeterminateDrawable?.setColorFilter(it, PorterDuff.Mode.SRC_IN)
        }
        circularProgressView?.circularProgressBar?.requestLayout()
    }

    fun showHorizontalProgress(skipIds: List<Int> = emptyList()) {
        viewState = ViewState.HORIZONTAL_LOADING
        horizontalProgressView ?: inflateHorizontalProgressView()
        horizontalProgressView?.root?.isVisible = true
        circularProgressView?.root?.isVisible = false
        errorView?.root?.isVisible = false
        setContentVisibility(true, skipIds)
        typedArray?.getDimensionPixelSize(
            R.styleable.ApiViewStateConstraintLayout_horizontalProgressBarWidth,
            0
        )?.takeIf { it != 0 }?.let {
            horizontalProgressView?.horizontalProgressBar?.layoutParams?.width = it
        }
        typedArray?.getDimensionPixelSize(
            R.styleable.ApiViewStateConstraintLayout_horizontalProgressBarHeight,
            0
        )?.takeIf { it != 0 }?.let {
            horizontalProgressView?.horizontalProgressBar?.layoutParams?.height = it
        }
        typedArray?.getColor(
            R.styleable.ApiViewStateConstraintLayout_horizontalProgressBarColor,
            0
        )?.takeIf { it != 0 }?.let {
            horizontalProgressView?.horizontalProgressBar?.indeterminateDrawable?.setColorFilter(
                it,
                PorterDuff.Mode.SRC_IN
            )
        }
        horizontalProgressView?.horizontalProgressBar?.requestLayout()
    }


    fun showError(
        @DrawableRes drawableResId: Int? = null,
        @StringRes titleResId: Int? = null,
        title: String? = null,
        @StringRes messageResId: Int? = null,
        message: String? = null,
        @StringRes buttonTextResId: Int? = null,
        onClickListener: OnClickListener? = null,
        isDisplayInternetSettingPanel: Boolean = false,
        skipIds: List<Int> = emptyList()
    ) {
        viewState = ViewState.ERROR_VIEW
        errorView ?: inflateErrorView()
        errorView?.root?.isVisible = true
        horizontalProgressView?.root?.isVisible = false
        circularProgressView?.root?.isVisible = false
        setContentVisibility(false, skipIds)
        drawableResId?.let {
            errorView?.errorImageView?.setImageResource(it)
            errorView?.errorImageView?.isVisible = true
        }
        titleResId?.let {
            errorView?.titleTextView?.setText(it)
            errorView?.titleTextView?.isVisible = true
        }
        title?.let {
            errorView?.titleTextView?.text = it
            errorView?.titleTextView?.isVisible = true
        }
        messageResId?.let {
            errorView?.messageTextView?.setText(it)
            errorView?.messageTextView?.isVisible = true
        }
        message?.let {
            errorView?.messageTextView?.text = it
            errorView?.messageTextView?.isVisible = true
        }
        buttonTextResId?.let { errorView?.errorButton?.setText(buttonTextResId) }
        onClickListener?.let { errorView?.errorButton?.setOnClickListener(onClickListener) }
        errorView?.errorButton?.isVisible = onClickListener != null
        errorView?.internetSettingPanelButton?.isVisible =
            isDisplayInternetSettingPanel && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        errorView?.internetSettingPanelButton?.setOnClickListener { context?.startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)) }
        typedArray?.getDimensionPixelSize(
            R.styleable.ApiViewStateConstraintLayout_errorImageWidth,
            0
        )?.takeIf { it != 0 }?.let {
            errorView?.errorImageView?.layoutParams?.width = it
        }
        typedArray?.getDimensionPixelSize(
            R.styleable.ApiViewStateConstraintLayout_errorImageHeight,
            0
        )?.takeIf { it != 0 }?.let {
            errorView?.errorImageView?.layoutParams?.height = it
        }
        typedArray?.getDimensionPixelSize(
            R.styleable.ApiViewStateConstraintLayout_errorTitleTextSize,
            0
        )?.takeIf { it != 0 }?.let {
            errorView?.titleTextView?.textSize = it.toFloat()
        }
        typedArray?.getDimensionPixelSize(
            R.styleable.ApiViewStateConstraintLayout_errorMessageTextSize,
            0
        )?.takeIf { it != 0 }?.let {
            errorView?.messageTextView?.textSize = it.toFloat()
        }
        typedArray?.getColor(
            R.styleable.ApiViewStateConstraintLayout_errorTitleTextColor,
            0
        )?.takeIf { it != 0 }?.let {
            errorView?.titleTextView?.setTextColor(it)
        }
        typedArray?.getColor(
            R.styleable.ApiViewStateConstraintLayout_errorMessageTextColor,
            0
        )?.takeIf { it != 0 }?.let {
            errorView?.messageTextView?.setTextColor(it)
        }
        typedArray?.getColor(
            R.styleable.ApiViewStateConstraintLayout_errorButtonTextColor,
            0
        )?.takeIf { it != 0 }?.let {
            errorView?.errorButton?.setTextColor(it)
        }
        typedArray?.getColor(
            R.styleable.ApiViewStateConstraintLayout_errorButtonBackgroundColor,
            0
        )?.takeIf { it != 0 }?.let {
            errorView?.errorButton?.setBackgroundColor(it)
        }
        errorView?.errorImageView?.requestLayout()
        errorView?.titleTextView?.requestLayout()
        errorView?.messageTextView?.requestLayout()
        errorView?.errorButton?.requestLayout()
    }

    private fun inflateCircularProgressView() {
        circularProgressView = ViewStateCircularProgrssBinding.bind(inflater.inflate(R.layout.view_state_circular_progrss, null))
        circularProgressView?.root?.tag = ViewState.PROGRESS_LOADING

        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ).apply {
            topToTop = ConstraintSet.PARENT_ID
            bottomToBottom = ConstraintSet.PARENT_ID
            startToStart = ConstraintSet.PARENT_ID
            endToEnd = ConstraintSet.PARENT_ID
        }
        addView(circularProgressView?.root, layoutParams)
    }

    private fun inflateHorizontalProgressView() {
        horizontalProgressView = ViewStateHorizontalLoadingBinding.bind(inflater.inflate(R.layout.view_state_horizontal_loading, null))
        horizontalProgressView?.root?.tag = ViewState.HORIZONTAL_LOADING

        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ).apply {
            topToTop = ConstraintSet.PARENT_ID
            bottomToBottom = ConstraintSet.PARENT_ID
            startToStart = ConstraintSet.PARENT_ID
            endToEnd = ConstraintSet.PARENT_ID
        }
        addView(horizontalProgressView?.root, layoutParams)
    }

    private fun inflateErrorView() {
        errorView = ViewStateErrorViewBinding.bind(inflater.inflate(R.layout.view_state_error_view, null))
        errorView?.root?.tag = ViewState.ERROR_VIEW

        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ).apply {
            topToTop = ConstraintSet.PARENT_ID
            bottomToBottom = ConstraintSet.PARENT_ID
            startToStart = ConstraintSet.PARENT_ID
            endToEnd = ConstraintSet.PARENT_ID
        }
        addView(errorView?.root, layoutParams)
    }

    private enum class ViewState {
        PROGRESS_LOADING,
        HORIZONTAL_LOADING,
        ERROR_VIEW,
        SHOW_CONTENT
    }

    abstract class EndlessRecyclerViewScrollListener : RecyclerView.OnScrollListener {
        private var visibleThreshold = 2
        private var currentPage = 0
        private var previousTotalItemCount = 0
        private var loading = true
        private val startingPageIndex = 0
        private var mLayoutManager: RecyclerView.LayoutManager?

        constructor(layoutManager: LinearLayoutManager?) {
            mLayoutManager = layoutManager
        }

        constructor(layoutManager: GridLayoutManager?) {
            mLayoutManager = layoutManager
            visibleThreshold *= layoutManager?.spanCount ?: 0
        }

        constructor(layoutManager: StaggeredGridLayoutManager?) {
            mLayoutManager = layoutManager
            visibleThreshold *= layoutManager?.spanCount ?: 0
        }

        fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
            var maxSize = 0
            for (i in lastVisibleItemPositions.indices) {
                if (i == 0) {
                    maxSize = lastVisibleItemPositions[i]
                } else if (lastVisibleItemPositions[i] > maxSize) {
                    maxSize = lastVisibleItemPositions[i]
                }
            }
            return maxSize
        }

        override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
            var lastVisibleItemPosition = 0
            val totalItemCount = mLayoutManager?.itemCount
            when (mLayoutManager) {
                is StaggeredGridLayoutManager -> {
                    val lastVisibleItemPositions = (mLayoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                    // get maximum element within the list
                    lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
                }
                is GridLayoutManager -> {
                    lastVisibleItemPosition = (mLayoutManager as GridLayoutManager).findLastVisibleItemPosition()
                }
                is LinearLayoutManager -> {
                    lastVisibleItemPosition = (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                }
            }

            if (totalItemCount ?: 0 < previousTotalItemCount) {
                currentPage = startingPageIndex
                previousTotalItemCount = totalItemCount ?: 0
                if (totalItemCount == 0) {
                    loading = true
                }
            }

            if (loading && totalItemCount ?: 0 > previousTotalItemCount) {
                loading = false
                previousTotalItemCount = totalItemCount ?: 0
            }

            if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount ?: 0) {
                currentPage++
                onLoadMore(currentPage, totalItemCount ?: 0, view)
                loading = true
            }
        }

        fun resetState() {
            currentPage = startingPageIndex
            previousTotalItemCount = 0
            loading = true
        }

        abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView)
    }
}