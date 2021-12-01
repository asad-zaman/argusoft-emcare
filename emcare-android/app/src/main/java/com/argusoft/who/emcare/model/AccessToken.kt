package com.argusoft.who.emcare.model

import com.google.gson.annotations.SerializedName

class AccessToken (
    private var _accessToken : String,
    private var _expiresIn : Integer,
    private var _refreshExpiresIn : Integer,
    private var _refreshToken : String,
    private var _tokenType : String,
    private var _idToken : String,
    private var _notBeforePolicy : Integer,
    private var _sessionState : String,
    private var _scope : String,
){
    @SerializedName("access_token")
    public lateinit var accessToken: String

    @SerializedName("expires_in")
    private lateinit var expiresIn: Integer

    @SerializedName("refresh_expires_in")
    private lateinit var refreshExpiresIn: Integer

    @SerializedName("refresh_token")
    private lateinit var refreshToken: String

    @SerializedName("token_type")
    private lateinit var tokenType: String

    @SerializedName("id_token")
    private lateinit var idToken: String

    @SerializedName("not_before_policy")
    private lateinit var notBeforePolicy: Integer

    @SerializedName("session_state")
    private lateinit var sessionState: String

    @SerializedName("scope")
    private lateinit var scope: String

    init {
        this.accessToken = _accessToken
        this.expiresIn = _expiresIn
        this.refreshExpiresIn = _refreshExpiresIn
        this.refreshToken = _refreshToken
        this.tokenType = _tokenType
        this.idToken = _idToken
        this.notBeforePolicy = _notBeforePolicy
        this.sessionState = _sessionState
        this.scope = _scope
    }

}