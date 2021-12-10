package com.argusoft.who.emcare.data.local.pref

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.hybrid.HybridConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager

object EncPref {

    private val EMPTY_ASSOCIATED_DATA = ByteArray(0)
    private const val DEFAULT_SUFFIX = "_defaultPref"
    private const val ENC_SUFFIX = "_defaultEnc"
    private const val DEBUG_SUFFIX = "_debug"
    private var KEY_URI = "android-keystore://"
    private var _mDefaultPref: SharedPreferences? = null
    private var aead: Aead? = null
    private var isDebuggable: Boolean = false

    class Builder {
        private var mPrefName: String? = null
        private var mContext: Context? = null
        private var mUseDefaultPref = false

        fun setContext(mContext: Context): Builder {
            this.mContext = mContext
            return this
        }

        fun serPrefName(mPrefName: String): Builder {
            this.mPrefName = mPrefName
            return this
        }

        fun serUseDefaultPref(mUseDefaultPref: Boolean): Builder {
            this.mUseDefaultPref = mUseDefaultPref
            return this
        }

        fun setDebuggable(isDebuggable: Boolean): Builder {
            EncPref.isDebuggable = isDebuggable
            return this
        }

        private fun getOrGenerateKeyHandle(): KeysetHandle? {
            return AndroidKeysetManager.Builder()
                .withSharedPref(
                    mContext,
                    base64Encode(mContext?.packageName!!.toByteArray(Charsets.US_ASCII)),
                    base64Encode("${mContext?.packageName}$ENC_SUFFIX".toByteArray(Charsets.US_ASCII))
                )
                .withKeyTemplate(KeyTemplates.get("AES128_GCM"))
                .withMasterKeyUri(KEY_URI)
                .build()
                .keysetHandle
        }

        fun build(): EncPref {
            if (mContext == null) {
                throw RuntimeException("Context should not be null, please set context.")
            }
            if (mPrefName.isNullOrEmpty()) {
                mPrefName = mContext?.packageName
            }
            if (mUseDefaultPref) {
                mPrefName = "${mPrefName}$DEFAULT_SUFFIX"
            }
            if (aead == null) {
                HybridConfig.register()
                KEY_URI = "$KEY_URI${mContext?.packageName}"
                aead = getOrGenerateKeyHandle()?.getPrimitive(Aead::class.java)
            }
            initPref(
                mContext!!,
                mPrefName!!
            )
            return EncPref
        }

    }

    private fun initPref(mContext: Context, mPrefName: String) {
        _mDefaultPref = mContext.getSharedPreferences(mPrefName, ContextWrapper.MODE_PRIVATE)
    }

    private fun base64Encode(input: ByteArray): String? {
        return Base64.encodeToString(input, Base64.DEFAULT)
    }

    private fun base64Decode(input: String): ByteArray? {
        return Base64.decode(input, Base64.DEFAULT)
    }

    private fun encryptText(textToEncrypt: String?) = try {
        val cipherText =
            aead?.encrypt(
                textToEncrypt?.toByteArray(Charsets.UTF_8),
                EMPTY_ASSOCIATED_DATA
            )
        when {
            (cipherText == null) -> textToEncrypt
            else -> base64Encode(cipherText)
        }
    } catch (ignore: Exception) {
        null
    }

    private fun decryptText(textToDecrypt: String?): String? = try {
        val plainTextArray =
            aead?.decrypt(
                base64Decode(
                    textToDecrypt ?: ""
                ),
                EMPTY_ASSOCIATED_DATA
            )
        when {
            (plainTextArray == null) -> textToDecrypt
            else -> String(plainTextArray, Charsets.UTF_8)
        }
    } catch (ignore: Exception) {
        null
    }

    private val myPref: SharedPreferences
        get() {
            if (_mDefaultPref != null) {
                return _mDefaultPref!!
            }
            throw RuntimeException(
                "EncPref class not correctly instantiated. Please call Builder.setContext().build() in the Application class onCreate."
            )
        }

    private fun getValueFromPref(key: String): String? {
        return if (isDebuggable) {
            myPref.getString("$key$DEBUG_SUFFIX", "")
        } else {
            decryptText(
                myPref.getString(
                    base64Encode(
                        key.toByteArray(Charsets.US_ASCII)
                    ), ""
                )
            )
        }
    }

    private fun setValueOnPref(key: String, value: String) {
        if (isDebuggable) {
            myPref.edit().apply {
                putString(
                    "$key$DEBUG_SUFFIX", value
                )
                apply()
            }
        } else {
            myPref.edit().apply {
                putString(
                    base64Encode(
                        key.toByteArray(Charsets.US_ASCII)
                    ), encryptText(
                        value
                    ) ?: value
                )
                apply()
            }
        }
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return getValueFromPref(key) ?: defaultValue
    }

    fun putString(key: String, value: String) {
        setValueOnPref(
            key,
            value
        )
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        val value = getValueFromPref(key)
        return if (value.isNullOrEmpty()) defaultValue else value.toBoolean()
    }

    fun putBoolean(key: String, value: Boolean) {
        setValueOnPref(
            key,
            value.toString()
        )
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        val value = getValueFromPref(key)
        return if (value.isNullOrEmpty()) defaultValue else value.toLong()
    }

    fun putLong(key: String, value: Long) {
        setValueOnPref(
            key,
            value.toString()
        )
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        val value = getValueFromPref(key)
        return if (value.isNullOrEmpty()) defaultValue else value.toInt()
    }

    fun putInt(key: String, value: Int) {
        setValueOnPref(
            key,
            value.toString()
        )
    }

    fun getDouble(key: String, defaultValue: Double = 0.0): Double {
        val value = getValueFromPref(key)
        return if (value.isNullOrEmpty()) defaultValue else value.toDouble()
    }

    fun putDouble(key: String, value: Double) {
        setValueOnPref(
            key,
            value.toString()
        )
    }

    fun getFloat(key: String, defaultValue: Float = 0.0f): Float {
        val value = getValueFromPref(key)
        return if (value.isNullOrEmpty()) defaultValue else value.toFloat()
    }

    fun putFloat(key: String, value: Float) {
        setValueOnPref(
            key,
            value.toString()
        )
    }

    fun getAll(): MutableMap<String, Any?> {
        val map =
            (if (isDebuggable) myPref.all.filter { it.key.contains(DEBUG_SUFFIX) } else myPref.all.filter {
                !it.key.contains(DEBUG_SUFFIX)
            }).toMutableMap()
        return if (isDebuggable) {
            map
        } else {
            val tmpMap = mutableMapOf<String, Any?>()
            for ((key, value) in map) {
                tmpMap[String(base64Decode(key) ?: EMPTY_ASSOCIATED_DATA, Charsets.UTF_8)] =
                    decryptText(value.toString())
            }
            tmpMap
        }
    }

    fun clear() {
        myPref.edit().clear().apply()
    }

}