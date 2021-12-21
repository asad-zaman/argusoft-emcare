package com.argusoft.who.emcare.oldstruct

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.argusoft.who.emcare.databinding.ActivityDeviceBlockedBinding
import kotlin.system.exitProcess

class BlockedDeviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceBlockedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeviceBlockedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonOk.setOnClickListener {
            onOkButtonClicked()
        }
    }

    private fun onOkButtonClicked() {
        finishAffinity()
        exitProcess(0)
    }

}