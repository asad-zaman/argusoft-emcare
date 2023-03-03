@file:Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")

package com.argusoft.who.emcare.utils.extention

import android.view.View
import androidx.annotation.DrawableRes
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.widget.ApiViewStateConstraintLayout

fun <T> ApiResponse<T>?.handleApiView(
    progressLayout: ApiViewStateConstraintLayout?,
    skipIds: List<Int> = listOf(R.id.headerLayout),
    isSuccess: (t: T?) -> Unit = {}
) {
    when (this) {
        is ApiResponse.Loading -> {
            progressLayout?.showHorizontalProgress(false, skipIds)
        }
        is ApiResponse.Success -> {
            progressLayout?.showContent(skipIds)
            progressLayout?.updateProgressUi(true, false)
            isSuccess(data)
        }
        is ApiResponse.ApiError -> {
            progressLayout?.showContent(skipIds)
            progressLayout?.updateProgressUi(true, false)
            progressLayout?.context?.showSnackBar(
                view = progressLayout,
                message = apiErrorMessage ?: apiErrorMessageResId?.let { progressLayout.context?.getString(it) },
                isError = true
            )
        }
        is ApiResponse.ServerError -> {
            progressLayout?.showContent(skipIds)
            progressLayout?.updateProgressUi(true, false)
            progressLayout?.context?.showSnackBar(
                view = progressLayout,
                message = errorMessage,
                isError = true
            )
        }
        is ApiResponse.NoInternetConnection -> {
            progressLayout?.showContent(skipIds)
            progressLayout?.updateProgressUi(true, false)
            progressLayout?.context?.showSnackBar(
                view = progressLayout,
                message = progressLayout.getString(R.string.no_internet_message),
                isError = true
            )
        }
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> ApiResponse<T>?.handleListApiView(
    progressLayout: ApiViewStateConstraintLayout?,
    skipIds: List<Int> = listOf(R.id.headerLayout),
    onClickListener: View.OnClickListener? = null,
    @DrawableRes drawableResId: Int? = 0,
    crossinline isSuccess: (t: T?) -> Unit = {}
) {
    val adapter: BaseAdapter<T>? = progressLayout?.recyclerView?.adapter as? BaseAdapter<T>?
    when (this) {
        is ApiResponse.Loading -> {
            if (isLoadMore) {
                progressLayout?.recyclerView?.post { adapter?.addLoadMore() }
                progressLayout?.swipeRefreshLayout?.isEnabled = false
            } else {
                progressLayout?.swipeRefreshLayout?.isRefreshing = isRefresh
                if (!isRefresh) {
                    progressLayout?.showProgress(skipIds = skipIds)
                    progressLayout?.swipeRefreshLayout?.isEnabled = false
                }
            }
        }
        is ApiResponse.Success -> {
            progressLayout?.updateProgressUi(true, true)
            if (isRequiredClear) {
                adapter?.clearAllItems()
            }
            if (progressLayout?.swipeRefreshLayout?.isRefreshing == true)
                progressLayout.recyclerView?.post { adapter?.clearAllItems() }
            (data as? List<T?>).takeIf { it != null }?.also {
                progressLayout?.recyclerView?.post {
                    adapter?.removeLoadMore()
                    adapter?.addAll(it)
                    if ((adapter?.itemCount ?: 0) == 0) {
                        progressLayout.showError(
                            drawableResId ?: R.drawable.ic_no_record,
                            message = successMessage ?: progressLayout.getString(R.string.no_record_found),
                            buttonTextResId = R.string.button_refresh,
                            onClickListener = onClickListener,
                            skipIds = skipIds
                        )
                    } else progressLayout.showContent(skipIds)
                }
            } ?: let {
                progressLayout?.recyclerView?.post {
                    adapter?.removeLoadMore(true)
                    data?.let { isSuccess(it) }
                    if ((adapter?.itemCount ?: 0) == 0) {
                        progressLayout.showError(
                            R.drawable.ic_no_record,
                            message = successMessage ?: progressLayout.getString(R.string.no_record_found),
                            buttonTextResId = R.string.button_refresh,
                            onClickListener = onClickListener,
                            skipIds = skipIds
                        )
                    } else progressLayout.showContent(skipIds)
                }
            }
            progressLayout?.scrollListener?.resetState()
            if (progressLayout?.recyclerView == null) {
                data?.let { isSuccess(it) }
                progressLayout?.showContent(skipIds)
            }
            progressLayout?.swipeRefreshLayout?.apply {
                isRefreshing = false
                isEnabled = true
            }
        }
        is ApiResponse.ServerError -> {
            progressLayout?.updateProgressUi(true, false)
            if (progressLayout?.swipeRefreshLayout?.isRefreshing == true || (adapter?.itemCount ?: 0) > 0) {
                progressLayout?.swipeRefreshLayout?.isRefreshing = false
                progressLayout?.swipeRefreshLayout?.isEnabled = true
                progressLayout?.recyclerView?.post { adapter?.removeLoadMore(true) }
                progressLayout?.rootView?.let { progressLayout.context?.showSnackBar(view = it, message = errorMessage, isError = true) }
            } else {
                progressLayout?.showError(
                    R.drawable.ic_cloud_off_black_24dp,
                    R.string.server_error_title,
                    message = errorMessage,
                    buttonTextResId = R.string.button_try_again,
                    onClickListener = onClickListener,
                    skipIds = skipIds
                )
            }
        }
        is ApiResponse.NoInternetConnection -> {
            progressLayout?.updateProgressUi(true, false)
            if (progressLayout?.swipeRefreshLayout?.isRefreshing == true || (adapter?.itemCount ?: 0) > 0) {
                progressLayout?.swipeRefreshLayout?.isRefreshing = false
                progressLayout?.swipeRefreshLayout?.isEnabled = true
                progressLayout?.recyclerView?.post { adapter?.removeLoadMore(true) }
                progressLayout?.rootView?.let { progressLayout.context?.showSnackBar(view = it, messageResId = R.string.no_internet_message, isError = true) }
            } else {
                progressLayout?.showError(
                    R.drawable.ic_no_internet_24dp,
                    R.string.no_internet_title,
                    messageResId = R.string.no_internet_message,
                    buttonTextResId = R.string.button_try_again,
                    onClickListener = onClickListener,
                    isDisplayInternetSettingPanel = true,
                    skipIds = skipIds
                )
            }
        }
    }
}


inline fun <T> ApiResponse<T>.whenLoading(function: () -> Unit): ApiResponse<T> {
    when (this) {
        is ApiResponse.Loading -> {
            function()
        }
    }
    return this
}

inline fun <T> ApiResponse<T>.whenSuccess(function: (T) -> Unit): ApiResponse<T> {
    when (this) {
        is ApiResponse.Success -> {
            data?.let { function(it) }
        }
    }
    return this
}

inline fun <T> ApiResponse<T>.whenFailed(function: () -> Unit): ApiResponse<T> {
    when (this) {
        is ApiResponse.Success -> {
            //Empty Block
        }
        is ApiResponse.Loading -> {
            //Empty Block
        }
        else -> {
            function()
        }
    }
    return this
}

inline fun <T> ApiResponse<T>.whenResult(
    onSuccess: (T) -> Unit,
    onFailed: (String) -> Unit,
): ApiResponse<T> {
    when (this) {
        is ApiResponse.Success -> {
            data?.let { onSuccess(it) }
        }
        is ApiResponse.ServerError -> {
            onFailed(errorMessage)
        }
        is ApiResponse.ApiError -> {
            apiErrorMessage?.let { onFailed(it) }
        }
        is ApiResponse.NoInternetConnection -> {
           onFailed("Make sure that mobile data or Wi-Fi is turn on, then try again")
        }
        else -> {
            //Empty Block
        }
    }
    return this
}

