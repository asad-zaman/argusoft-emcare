package com.argusoft.who.emcare.ui.common.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoggedInUser(
    @Json(name = "acr")
    var acr: String? = null,
    @Json(name = "address")
    var address: Any? = null,
    @Json(name = "allowed-origins")
    var allowedOrigins: List<String?>? = null,
    @Json(name = "at_hash")
    var atHash: Any? = null,
    @Json(name = "aud")
    var aud: String? = null,
    @Json(name = "auth_time")
    var authTime: Any? = null,
    @Json(name = "authorization")
    var authorization: Any? = null,
    @Json(name = "azp")
    var azp: String? = null,
    @Json(name = "birthdate")
    var birthdate: Any? = null,
    @Json(name = "c_hash")
    var cHash: Any? = null,
    @Json(name = "claims_locales")
    var claimsLocales: Any? = null,
    @Json(name = "cnf")
    var cnf: Any? = null,
    @Json(name = "email")
    var email: Any? = null,
    @Json(name = "email_verified")
    var emailVerified: Boolean? = null,
    @Json(name = "exp")
    var exp: Int? = null,
    @Json(name = "family_name")
    var familyName: String? = null,
    @Json(name = "gender")
    var gender: Any? = null,
    @Json(name = "given_name")
    var givenName: String? = null,
    @Json(name = "iat")
    var iat: Int? = null,
    @Json(name = "iss")
    var iss: String? = null,
    @Json(name = "jti")
    var jti: String? = null,
    @Json(name = "locale")
    var locale: Any? = null,
    @Json(name = "middle_name")
    var middleName: Any? = null,
    @Json(name = "name")
    var name: String? = null,
    @Json(name = "nbf")
    var nbf: Any? = null,
    @Json(name = "nickname")
    var nickname: Any? = null,
    @Json(name = "nonce")
    var nonce: Any? = null,
    @Json(name = "phone_number")
    var phoneNumber: Any? = null,
    @Json(name = "phone_number_verified")
    var phoneNumberVerified: Any? = null,
    @Json(name = "picture")
    var picture: Any? = null,
    @Json(name = "preferred_username")
    var preferredUsername: String? = null,
    @Json(name = "profile")
    var profile: Any? = null,
    @Json(name = "realm_access")
    var realmAccess: RealmAccess? = null,
    @Json(name = "resource_access")
    var resourceAccess: ResourceAccess? = null,
    @Json(name = "s_hash")
    var sHash: Any? = null,
    @Json(name = "scope")
    var scope: String? = null,
    @Json(name = "session_state")
    var sessionState: String? = null,
    @Json(name = "sid")
    var sid: String? = null,
    @Json(name = "sub")
    var sub: String? = null,
    @Json(name = "trusted-certs")
    var trustedCerts: Any? = null,
    @Json(name = "typ")
    var typ: String? = null,
    @Json(name = "updated_at")
    var updatedAt: Any? = null,
    @Json(name = "website")
    var website: Any? = null,
    @Json(name = "zoneinfo")
    var zoneinfo: Any? = null
) {
    @JsonClass(generateAdapter = true)
    data class RealmAccess(
        @Json(name = "roles")
        var roles: List<String?>? = null,
        @Json(name = "verify_caller")
        var verifyCaller: Any? = null
    )

    @JsonClass(generateAdapter = true)
    data class ResourceAccess(
        @Json(name = "account")
        var account: Account? = null,
        @Json(name = "emcare_client")
        var emcareClient: EmcareClient? = null
    ) {
        @JsonClass(generateAdapter = true)
        data class Account(
            @Json(name = "roles")
            var roles: List<String?>? = null,
            @Json(name = "verify_caller")
            var verifyCaller: Any? = null
        )

        @JsonClass(generateAdapter = true)
        data class EmcareClient(
            @Json(name = "roles")
            var roles: List<String?>? = null,
            @Json(name = "verify_caller")
            var verifyCaller: Any? = null
        )
    }
}