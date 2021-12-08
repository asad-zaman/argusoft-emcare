package com.argusoft.who.emcare.oldstruct.model

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
    var accessToken: String

    @SerializedName("expires_in")
    private var expiresIn: Integer

    @SerializedName("refresh_expires_in")
    private var refreshExpiresIn: Integer

    @SerializedName("refresh_token")
    private var refreshToken: String

    @SerializedName("token_type")
    private var tokenType: String

    @SerializedName("id_token")
    private var idToken: String

    @SerializedName("not_before_policy")
    private var notBeforePolicy: Integer

    @SerializedName("session_state")
    private var sessionState: String

    @SerializedName("scope")
    private var scope: String

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