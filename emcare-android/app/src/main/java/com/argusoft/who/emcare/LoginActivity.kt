package com.argusoft.who.emcare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.argusoft.who.emcare.api.KeycloakLoginService
import com.argusoft.who.emcare.databinding.ActivityLoginBinding
import com.argusoft.who.emcare.model.AccessToken
import com.argusoft.who.emcare.static.CompanionValues.Companion.KEYCLOAK_CLIENT_SECRET
import com.argusoft.who.emcare.static.CompanionValues.Companion.KEYCLOAK_CLIENT_ID
import com.argusoft.who.emcare.static.CompanionValues.Companion.KEYCLOAK_GRANT_TYPE
import com.argusoft.who.emcare.static.CompanionValues.Companion.KEYCLOAK_SCOPE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                if (response.isSuccessful) {
                    val accessToken: AccessToken? = response.body()
                    Toast.makeText(
                        this@LoginActivity,
                        "You`ve logged in successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val mainActivityIntent: Intent =
                        Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(mainActivityIntent)
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

}