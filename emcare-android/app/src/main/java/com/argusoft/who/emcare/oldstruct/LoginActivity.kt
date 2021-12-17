package com.argusoft.who.emcare.oldstruct

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.argusoft.who.emcare.oldstruct.api.DeviceManagementService
import com.argusoft.who.emcare.data.remote.fhirService.HttpRequestInterceptor
import com.argusoft.who.emcare.oldstruct.api.KeycloakLoginService
import com.argusoft.who.emcare.databinding.ActivityLoginBinding
import com.argusoft.who.emcare.oldstruct.model.AccessToken
import com.argusoft.who.emcare.oldstruct.model.DeviceInfo
import com.argusoft.who.emcare.oldstruct.static.CompanionValues.Companion.KEYCLOAK_CLIENT_SECRET
import com.argusoft.who.emcare.oldstruct.static.CompanionValues.Companion.KEYCLOAK_CLIENT_ID
import com.argusoft.who.emcare.oldstruct.static.CompanionValues.Companion.KEYCLOAK_GRANT_TYPE
import com.argusoft.who.emcare.oldstruct.static.CompanionValues.Companion.KEYCLOAK_SCOPE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.NetworkInterface
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var device: DeviceInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermission(Manifest.permission.READ_PHONE_STATE, 111)
        //Adding login button click function
        binding.buttonLogin.setOnClickListener {
            onLoginButtonClick()
        }
    }

    fun getAccessToken() {
        val service: KeycloakLoginService = KeycloakLoginService.create()

        val password: String = binding.editTextPassword.text.toString()
        val username: String = binding.editTextUsername.text.toString()

        val call: Call<AccessToken> = service.getAccessToken(
            KEYCLOAK_CLIENT_ID,
            KEYCLOAK_GRANT_TYPE,
            KEYCLOAK_CLIENT_SECRET,
            KEYCLOAK_SCOPE,
            username,
            password
        )

        call.enqueue(object : Callback<AccessToken> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                if (response.isSuccessful) {
                    val accessToken: AccessToken? = response.body()
                    if (accessToken != null) {
                        HttpRequestInterceptor.token = accessToken.accessToken
                    }
                    device = getDeviceDetails()
                    if (device != null) {
                        isDeviceBlocked()
                    }
                } else if (response.code() == 401) {//Unauthorized request code
                    Toast.makeText(this@LoginActivity, "Invalid Credentials!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@LoginActivity, "Error Encountered!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error Encountered!", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun onLoginButtonClick() {
        getAccessToken()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDeviceDetails(): DeviceInfo {
        val android_version = Build.VERSION.RELEASE
        val tm: TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val imei = tm.imei
        val mac_address = getMacAddr()
        return DeviceInfo(
            null, null,
            null, null,
            null, android_version,
            imei, mac_address,
            null, false
        )
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@LoginActivity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@LoginActivity, arrayOf(permission), requestCode)
        }
    }

    private fun getMacAddr(): String {
        try {
            val all = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.getName().equals("wlan0", ignoreCase=true)) continue

                val macBytes = nif.getHardwareAddress() ?: return ""

                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }

                if (res1.length > 0) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (ex: Exception) {
        }
        return "02:00:00:00:00:00"
    }

    private fun isDeviceBlocked() {
        val deviceManagementService: DeviceManagementService = DeviceManagementService.create()
        val isBlockedCall: Call<DeviceInfo> = deviceManagementService.getDeviceByMacAddress(
            device.getMacAddress()
        )

        isBlockedCall.enqueue(object: Callback<DeviceInfo> {
            override fun onResponse(call: Call<DeviceInfo>?, response: Response<DeviceInfo>?) {
                val retrievedDevice: DeviceInfo?
                if (response?.body() == null) {
                    addDeviceInfo()
                    goToMainActivity()
                } else if (response?.body() != null) {
                    retrievedDevice = response?.body()
                    if(retrievedDevice?.getIsBlocked() == true) {
                        val blockedDeviceActivityIntent: Intent =
                            Intent(this@LoginActivity, BlockedDeviceActivity::class.java)
                        startActivity(blockedDeviceActivityIntent)
                    } else {
                        if (retrievedDevice != null) {
                            retrievedDevice.setAndroidVersion(device.getAndroidVersion())
                            retrievedDevice.setImeiNumber(device.getImeiNumber())
                            retrievedDevice.setMacAddress(device.getMacAddress())
                            retrievedDevice.setIsBlocked(device.getIsBlocked())
                            updateDeviceInfo(retrievedDevice)
                        }
                        goToMainActivity()
                    }
                } else if (response?.code() == 401) {//Unauthorized request code
                    Toast.makeText(this@LoginActivity, "Invalid Credentials!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@LoginActivity, "Error Encountered!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<DeviceInfo?>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error getting device info", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addDeviceInfo() {
        val deviceManagementService: DeviceManagementService = DeviceManagementService.create()
        val addDeviceCall = deviceManagementService.addDevice(device)

        addDeviceCall.enqueue(object: Callback<DeviceInfo> {
            override fun onResponse(call: Call<DeviceInfo>, response: Response<DeviceInfo>) {
                if (response.body() != null) {
                    Toast.makeText(this@LoginActivity, "Device info synced!", Toast.LENGTH_SHORT).show()
                } else if (response.code() == 401) {//Unauthorized request code
                    Toast.makeText(this@LoginActivity, "Invalid Credentials!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@LoginActivity, "Error Encountered!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<DeviceInfo>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error saving device info", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateDeviceInfo(retrievedDevice: DeviceInfo) {
        val deviceManagementService: DeviceManagementService = DeviceManagementService.create()
        val updateDeviceCall = deviceManagementService.updateDevice(retrievedDevice)

        updateDeviceCall.enqueue(object: Callback<DeviceInfo> {
            override fun onResponse(call: Call<DeviceInfo>, response: Response<DeviceInfo>) {
                if (response.body() != null) {
                    Toast.makeText(this@LoginActivity, "Device info updated!", Toast.LENGTH_SHORT).show()
                } else if (response.code() == 401) {//Unauthorized request code
                    Toast.makeText(this@LoginActivity, "Invalid Credentials!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@LoginActivity, "Error Encountered!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<DeviceInfo>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error updating device info", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun goToMainActivity() {
        Toast.makeText(
            this@LoginActivity,
            "You`ve logged in successfully!",
            Toast.LENGTH_SHORT
        ).show()
        val mainActivityIntent: Intent =
            Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(mainActivityIntent)
    }

}