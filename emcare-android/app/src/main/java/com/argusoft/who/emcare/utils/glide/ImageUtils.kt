package com.argusoft.who.emcare.utils.glide

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.ImageView
import androidx.annotation.*
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.argusoft.who.emcare.utils.extention.isValidURL
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException


/**
 * USAGE : Used to load URL/DrawableRes into ImageView
 * <p>
 * For Better Optimization in RecyclerView, please follow this link{@link http://bumptech.github.io/glide/int/recyclerview.html}.
 * Created by
 */

/**
 * Used to display String URL into ImageView
 *
 * For Better Optimization in RecyclerView, please follow this link(@see http://bumptech.github.io/glide/int/recyclerview.html).
 *
 * @param url Url which needs to be loaded in ImageView
 * @param resPlaceHolderId Resource Id of Drawable resource which used to display in case of Error or while before loading the actual URL.
 * @param isLoadThumb Whether needs to load thumb or not. This will load only 25% of the actual request first then loads the actual URL.
 *                      If the actual request will executed before the thumbnail then after applying actual request, this will not load the thumbnail URL.
 */
fun GlideRequests.loadUrl(
    imageView: ImageView, @Nullable url: String, @Nullable @RawRes @DrawableRes resPlaceHolderId: Int = 0, isLoadThumb:
    Boolean = false
) {
    takeIf { url.isNotEmpty() && url.isValidURL() }?.let {
        if (url.contains(".gif")) {
            asGif().placeholder(resPlaceHolderId).error(resPlaceHolderId).load(url).into(imageView)
        } else {
            val glideRequests = load(url)
                .placeholder(resPlaceHolderId).error(resPlaceHolderId)

            if (isLoadThumb) {
                val thumbnail = glideRequests.thumbnail(0.25f)
                    .placeholder(resPlaceHolderId).error(resPlaceHolderId)
                thumbnail.into(imageView)
            } else {
                glideRequests.into(imageView)
            }
        }
    } ?: imageView.loadRes(resPlaceHolderId)
}

/**
 * Used to display thumbnail url before the Actual url
 *
 * For Better Optimization in RecyclerView, please follow this link(@see http://bumptech.github.io/glide/int/recyclerview.html).
 *
 * @param url Url which needs to be loaded in ImageView
 * @param thumbUrl thumbUrl which needs to be loaded in before the actual URL load into ImageView
 * @param resPlaceHolderId Resource Id of Drawable resource which used to display in case of Error or while before loading the actual URl.
 */
fun GlideRequests.loadUrl(
    imageView: ImageView, @Nullable url: String, @Nullable thumbUrl: String, @Nullable @RawRes @DrawableRes resPlaceHolderId: Int = 0
) {
    takeIf { thumbUrl.isNotEmpty() && thumbUrl.isValidURL() }?.let {
        load(url)
            .placeholder(resPlaceHolderId).error(resPlaceHolderId)
            .thumbnail(
                load(thumbUrl)
            ).into(imageView)
    } ?: loadUrl(imageView, url, resPlaceHolderId)
}

/**
 * Used to display Local File into ImageView
 *
 * @param resPlaceHolderId Resource Id of Drawable resource which used to display in case of Error or while before loading the actual URL.
 */
@Throws
fun GlideRequests.loadFile(imageView: ImageView, @Nullable file: File, @Nullable @RawRes @DrawableRes resPlaceHolderId: Int = 0) {

    takeIf { !file.exists() }?.let {
        throw FileNotFoundException("Specified File is not found: ${file.absoluteFile}")
    }
    takeIf { !file.canRead() }?.let {
        throw RuntimeException("Runtime Permission Exception found. You haven't provided READ_EXTERNAL_STORAGE or WRITE_EXTERNAL_STORAGE permission.")
    }
    takeIf { !file.isFile }?.let {
        throw FileNotFoundException("File not found: ${file.absoluteFile}")
    }

    load(file)
        .placeholder(resPlaceHolderId).error(resPlaceHolderId)
        .into(imageView)
}

fun GlideRequests.loadRoundCorner(
    imageView: ImageView, @Nullable file: File, @DimenRes dimenId: Int, @Nullable @RawRes @DrawableRes resPlaceHolderId: Int = 0
) {
    val radius = imageView.context.resources.getDimensionPixelSize(dimenId)
    this.load(file)
        .transform(RoundedCorners(radius))
        .placeholder(resPlaceHolderId).error(resPlaceHolderId)
        .into(imageView)
}

fun GlideRequests.loadRoundCorner(
    imageView: ImageView, @Nullable url: String, @DimenRes dimenId: Int, @Nullable @RawRes @DrawableRes resPlaceHolderId: Int = 0
) {
    val radius = imageView.context.resources.getDimensionPixelSize(dimenId)
    load(url)
        .transform(RoundedCorners(radius))
        .placeholder(resPlaceHolderId).error(resPlaceHolderId)
        .into(imageView)
}

/**
 * Used to display Resource Id of Drawable resource into ImageView
 *
 * @param resId Resource Id of Drawable resource
 */
fun ImageView.loadRes(@Nullable @RawRes @DrawableRes resId: Int = android.R.drawable.ic_menu_gallery) {
    this.setImageResource(resId)
}

@SuppressLint("LogNotTimber")
fun ImageView.loadUri(context: Context, @NonNull uri: Uri, @Nullable @RawRes @DrawableRes resPlaceHolderId: Int = 0) {
    try {
        // Use the MediaStore to load the image.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val bitmap = ImageDecoder.createSource(context.contentResolver, uri)
            this.setImageBitmap(ImageDecoder.decodeBitmap(bitmap))
        } else {
            GlideApp.with(this)
                .load(uri)
                .placeholder(resPlaceHolderId).error(resPlaceHolderId)
                .into(this)
        }

    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("TAG", "Error: " + e.message + "Could not open URI: " + uri.toString())
    }
}